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
});

function setListeners(){
    // event delegation for dynamic collection hub input copy
    $(document).on("click", ".collection-hub-input", function(){
        $(this).siblings(".input-group-append").children(".tooltip-copy").tooltip("show");
        copy($(this));
    });

    $(document).on("click", ".copy-to-clipboard", function(){
        let id = $(this).data("inputIDToCopy");
        copy($(`#${id}`));
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
        .then(setInstanceData);

    fetchAllTools()
        .then(setToolData);
}

// Set details on UI for any given instance
function setInstanceData(instances){
    if(typeof instances === "undefined" || areInstancesEmpty(instances)){
        setErrorHTML();
        return;
    }
    for(let instance of instances){
        let instanceName = instance.instanceName;
        let details = instance.details || {};
        let pane = new InstancePane(instanceName, details.dateCreated, details.repos, details.clusterName, 
            details.collections, details.cliURL);

        $("#instance-accordion").append(pane.instanceHTML);
    }

    $(".loading-row").hide();
    $(".accordion-title:first").click();
}

function areInstancesEmpty(instances){
    return typeof instances === "undefined" || instances.length === 0;
}

function setErrorHTML(){
    let errorHTML = $(
        `<li data-accordion-item class="bx--accordion__item">
            <button class="bx--accordion__heading accordion-title" aria-expanded="false" aria-controls="paneError" onclick=updateInstanceView(this)>
                <svg focusable="false" preserveAspectRatio="xMidYMid meet" style="will-change: transform;" xmlns="http://www.w3.org/2000/svg" class="bx--accordion__arrow" width="16" height="16" viewBox="0 0 16 16" aria-hidden="true">
                    <path d="M11 8L6 13 5.3 12.3 9.6 8 5.3 3.7 6 3z"></path>
                </svg>
                <div class="bx--accordion__title">No instance found</div>
            </button>
            <div id="paneError" class="bx--accordion__content hidden" data-hubName="n/a" data-appsodyURL="n/a" data-codewindURL="n/a" data-collections="" data-cliURL="n/a">
            </div>
        </li>`
    );
    $("#instance-accordion").append(errorHTML);
    $(".loading-row").hide();
    $(".accordion-title:first").click();
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

function updateInstanceView(element){
    //close any open accordion headings
    $(".bx--accordion__heading").attr("aria-expanded", false);
    $(".bx--accordion__heading").parent().removeClass("bx--accordion__item--active");

    //return if clicked element is already open and the collections card doesn"t need to be updated
    if($(element).attr("aria-expanded") === "true"){
        return;
    }

    //update the collections card
    let paneId = $(element).attr("aria-controls");
    let instancePane = $(`#${paneId}`);
    let appHubName = $(instancePane).data("hubname");
    let appsodyURL = $(instancePane).data("appsodyurl");
    let codewindURL = $(instancePane).data("codewindurl");
    let cliURL = $(instancePane).data("cliurl");
    let collections = $(instancePane).data("collections").split(",");
    let numberOfCollections = collections[0] === "" ? 0 : collections.length;

    // Instance Details
    $("#instance-details-card #apphub-name").text(appHubName);

    $("#instance-details-card #appsody-url").val(appsodyURL).attr("data-original-title", appsodyURL).attr("title", appsodyURL);
    $("#instance-details-card #appsody-url").next(".input-group-append").children(".tooltip-copy").attr("data-original-title", appsodyURL).attr("title", appsodyURL);

    $("#instance-details-card #codewind-url").val(codewindURL).attr("data-original-title", codewindURL).attr("title", codewindURL);
    $("#instance-details-card #codewind-url").next(".input-group-append").children(".tooltip-copy").attr("data-original-title", codewindURL).attr("title", codewindURL);

    $("#instance-details-card #management-cli").val(cliURL).attr("data-original-title", cliURL).attr("title", cliURL);
    $("#instance-details-card #management-cli").next(".input-group-append").children(".tooltip-copy").attr("data-original-title", cliURL).attr("title", cliURL);

    // Collections Card
    $("#collection-details-card #num-collections").text(numberOfCollections);
    let liColls = collections.reduce((acc, coll) => {
        return `${acc}<li>${coll}</li>`;
    },"");

    $("#collection-details-card #collection-list").html(`<ul>${liColls}</ul>`);

    // hide tile loaders
    $("#instance-details-card .bx--inline-loading").hide();
    $("#collection-details-card  .bx--inline-loading").hide();
}