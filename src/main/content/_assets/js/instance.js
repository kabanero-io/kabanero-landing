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

$(document).ready(function () {
    fetchAllInstances()
        .then(setInstanceSelections)
        .then(fetchAnInstance)
        .then(loadAllInfo);

    setListeners();

    $("#instance-accordion li").on("click", e => {
        e.stopPropagation();
        let newName = handleInstanceSelection(e.target);
        fetchAnInstance(newName)
            .then(loadAllInfo);
    });
});

function setListeners() {
    // event delegation for dynamic stack hub input copy
    $(document).on("click", ".copy-to-clipboard", function () {
        copy($(this).parent().siblings(".stack-hub-input"));
    });

    $(document).on("click", ".stack-hub-input", function () {
        copy($(this));
    });

    function copy(input) {
        $(input).select();
        document.execCommand("copy");
        $(input).closest(".bx--col").find(".copied-text").show().delay(2000).fadeOut();
    }
}

// Request to get all instances names
function loadAllInfo(instanceJSON) {
    if (typeof instanceJSON === "undefined") {
        console.log("instance data is undefined, cannot load instance");
        return;
    }

    setInstanceCard(instanceJSON);
    fetchStacks(instanceJSON.metadata.name);

    fetchAllTools()
        .then(setToolData);

    fetchOAuthDetails()
        .then(setOAuth);
}

// Set details on UI for any given instance
function setToolData(tools) {
    let noTools = true;

    if (typeof tools === "undefined") {
        $(".bx--inline-loading").hide();
        $("#no-tools").show();
        return;
    }

    for (let tool of tools) {

        if (typeof tool.name === "undefined" || tool.name.length === 0 ||
            typeof tool.location === "undefined" || tool.location.length === 0) {
            continue;
        }
        if (tool.name === "Application Navigator") {
            $("#appnav-link").attr("href", `https://${tool.location}`);
            $("#manage-apps-button").attr("disabled", false);
            $("#manage-apps-button-text").html("Manage Applications");
        }

        if (tool.name === "Tekton") {
            $("#pipeline-link").attr("href", `https://${tool.location}`);
            $("#pipeline-button").attr("disabled", false);
            $("#pipeline-button-text").text("Manage Pipelines");
        }

        if (tool.name === "Red Hat CodeReady Workspaces") {
            $("#codeready-link").attr("href", `http://${tool.location}`);
            $("#codeready-button").attr("disabled", false);
            $("#codeready-button-text").text("Go to CodeReady");
            $("#codeready-container").show();
        }

        // TODO: remove redundency above
        // set kappnav url to manage applications link
        // let toolPane = new ToolPane(tool.name, tool.location);
        // $("#tool-data-container").append(toolPane.toolHTML);
        noTools = false;
    }

    if (noTools) {
        $("#no-tools").show();
    }

    // TODO: remove loading for each individual tool as it loads instead of all at once
    $(".bx--inline-loading").hide();
}

function setInstanceCard(instanceJSON) {    
    let repos = instanceJSON.spec.stacks.repositories;
    let cliURL = instanceJSON.status.cli.hostnames[0];

    // Instance Details
    let $instanceDetails = $("#repo-section");
    repos.forEach(repo => {
        $instanceDetails.append(createRepositorySection(repo.name, repo.https.url));
    });

    $("#instance-details-card #management-cli").val(cliURL).attr("title", cliURL);
    $("#instance-details-card #management-cli").next(".input-group-append > .copy-to-clipboard").attr("title", "Click to copy Management CLI URL");

    // hide card loader
    $("#instance-details-card .bx--inline-loading").hide();

    function createRepositorySection(name, appsodyURL) {
        let codewindURL = appsodyURL.replace(".yaml", ".json");

        let $nameTemplate = $("#stack-hub-name-row-template").clone().removeAttr("id").removeClass("hidden");;
        $($nameTemplate).find(".stack-hub-name").text(name);

        let $appsodyTemplate = $("#stack-hub-appsody-row-template").clone().removeAttr("id").removeClass("hidden");
        $($appsodyTemplate).find(".appsody-url").val(appsodyURL).attr("title", appsodyURL);
        $($appsodyTemplate).find(".input-group-append > .tooltip-copy").attr("title", "Click to copy the Appsody URL");

        let $codewindTemplate = $("#stack-hub-codewind-row-template").clone().removeAttr("id").removeClass("hidden");;

        $($codewindTemplate).find(".codewind-url").val(codewindURL).attr("title", codewindURL);
        $($codewindTemplate).find(".codewind-url > .tooltip-copy").attr("title", "Click to copy the Codewind URL");
        return [$nameTemplate, $appsodyTemplate, $codewindTemplate, $("<hr/>")];
    }
}

function setStackCard(instanceJSON) {
    //let details = instanceJSON.details;
    let stacks = instanceJSON.items;
    let numberOfStacks = instanceJSON.items.length;

    // Stacks Card
    $("#stack-details-card #num-stacks").text(numberOfStacks);

    let liColls = "";
    stacks.forEach(stack => {
        let versionLength = stack.spec.versions.length;
        versions = stack.spec.versions.reduce((acc, versionObj, idx) => {
            return acc += idx !== versionLength - 1 ? `${versionObj.version}, ` : ` ${versionObj.version}`;
        }, "");
        liColls = liColls.concat(`<li><span class="bold">${stack.spec.name}</span>: ${versions}</li>`);
    });

    $("#stack-details-card #stack-list").html(`<ul>${liColls}</ul>`);

    $("#stack-details-card  .bx--inline-loading").hide();
}

// Sets up the UI in regards to OAuth data
function setOAuth(oauthJSON) {
    if (oauthJSON && oauthJSON.isConfigured) {
        let selectedInstance = $("#selected-instance-name").text().trim();
        $("#stacks-oauth-msg").text("Manage Stacks");
        $("#stacks-link").attr("href", `/instance/stacks?name=${selectedInstance}`);
    }
}