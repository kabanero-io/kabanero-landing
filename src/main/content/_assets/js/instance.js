/******************************************************************************
 *
 * Copyright 2019 IBM Corporation and others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 ******************************************************************************/

$(document).ready(function() {
    loadAllInfo();
    setListeners();

    $("#instance-accordion li").on("click", e => {
        e.stopPropagation();
        let newName = handleInstanceSelection(e.target);
        fetchAnInstance(newName)
            .then(updateInstanceView);
    });
});

function setListeners(){
    // event delegation for dynamic collection hub input copy
    $(document).on("mouseover", ".collection-hub-input", function(){
        $(this).attr("data-original-title", $(this).attr("data-original-title"));
        $(this).tooltip("show");
        //copy($(this));
    });

    $(document).on("mouseout", ".collection-hub-input", function(){
        $(this).tooltip("hide");
    });

    $(document).on("mouseover", ".copy-to-clipboard", function(){
        $(this).attr("data-original-title", $(this).attr("data-copy-text"));
        $(this).tooltip("show");
        //copy($(this));
    });

    $(document).on("mouseout", ".copy-to-clipboard", function(){
        $(this).tooltip("hide");
    });

    $(document).on("click", ".copy-to-clipboard", function(){
        copy($(this).parent().siblings(".collection-hub-input"));
        $(this).attr("data-original-title", "Copied!");
        $(this).tooltip("show");
    });

    function copy(input){
        $(input).select();
        document.execCommand("copy");

        setTimeout(function(){
            $(".tooltip-copy").tooltip("hide");
        }, 1000);
    }
}

// Request to get all instances names
function loadAllInfo(){
    fetchAllInstances()
        .then(setInstanceSelections)
        .then(fetchAnInstance)
        .then(updateInstanceView);

    fetchAllTools()
        .then(setToolData);

    fetchOAuthDetails()
        .then(setOAuth);
}

// Set details on UI for any given instance
function setToolData(tools){
    let noTools = true;

    if(typeof tools === "undefined"){
        $("#application-details-card .bx--inline-loading").hide();
        $("#pipelines-details-card .bx--inline-loading").hide();
        $("#no-tools").show();
        return;
    }

    for(let tool of tools){

        if(typeof tool.label === "undefined" || tool.label.length === 0 || 
        typeof tool.location === "undefined" || tool.location.length === 0){
            continue;
        }
        if(tool.label === "Application Navigator"){
            $("#appnav-link").attr("href", tool.location);
            $("#manage-apps-button").attr("disabled", false);
            $("#manage-apps-button-text").html("Manage Applications");
        }

        if(tool.label === "Tekton"){
            $("#pipeline-link").attr("href", tool.location);
            $("#pipeline-button").attr("disabled", false);
            $("#pipeline-button-text").text("Manage Pipelines");
        }

        if(tool.label === "Eclipse Che"){
            $("#che-link").attr("href", tool.location);
            $("#che-button").attr("disabled", false);
            $("#che-button-text").text("Go to Eclipse Che");
            $("#che-tile").show();
        }

        //set kappnav url to manage applications link
        let toolPane = new ToolPane(tool.label, tool.location);
        $("#tool-data-container").append(toolPane.toolHTML);
        noTools = false;
    }

    if(noTools){
        $("#no-tools").show();
    }

    $("#application-details-card .bx--inline-loading").hide();
    $("#pipelines-details-card .bx--inline-loading").hide();
}

function updateInstanceView(instanceJSON){
    if(typeof instanceJSON === "undefined"){
        return;
    }

    //update the various cards
    setInstanceCard(instanceJSON);
    setCollectionCard(instanceJSON);
}

function setInstanceCard(instanceJSON){
    let details = instanceJSON.details;
    let appHubName = details.repos[0].name;
    let appsodyURL = details.repos[0].appsodyURL;
    let codewindURL = details.repos[0].codewindURL;
    let cliURL = details.cliURL;

    // Instance Details
    $("#instance-details-card #apphub-name").text(appHubName);

    $("#instance-details-card #appsody-url").val(appsodyURL).attr("data-original-title", appsodyURL)
    $("#instance-details-card #appsody-url").next(".input-group-append").children(".tooltip-copy").attr("data-copy-text", "Click to copy Appsody URL");

    $("#instance-details-card #codewind-url").val(codewindURL).attr("data-original-title", codewindURL)
    $("#instance-details-card #codewind-url").next(".input-group-append").children(".tooltip-copy").attr("data-copy-text", "Click to copy Codewind URL");

    $("#instance-details-card #management-cli").val(cliURL).attr("data-original-title", cliURL)
    $("#instance-details-card #management-cli").next(".input-group-append").children(".tooltip-copy").attr("data-copy-text", "Click to copy Management CLI URL");

    // hide card loader
    $("#instance-details-card .bx--inline-loading").hide();
}

function setCollectionCard(instanceJSON){
    let details = instanceJSON.details;
    let collections = details.collections;
    let numberOfCollections = details.collections.length;
    
    // Collections Card
    $("#collection-details-card #num-collections").text(numberOfCollections);

    let liColls = "";
    $(collections).each(function(){
        liColls = liColls.concat(`<li>${this.name} : ${this.version}</li>`);
    });

    $("#collection-details-card #collection-list").html(`<ul>${liColls}</ul>`);

    $("#collection-details-card  .bx--inline-loading").hide();
}

// Sets up the UI in regards to OAuth data
function setOAuth(oauthJSON){
    if(oauthJSON && oauthJSON.isConfigured){
        let selectedInstance = $("#selected-instance-name").text().trim();
        $("#collections-oauth-msg").text("Manage Collections");
        $("#collections-link").attr("href", `/instance/collections?name=${selectedInstance}`);
    }
}