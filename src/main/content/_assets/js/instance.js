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
    for(let instance of instances){
        let instanceName = instance.instanceName;
        let details = instance.details || {};

        let pane = new InstancePane(instanceName, details.dateCreated, details.repos, details.clusterName, 
            details.collections, details.cliURL);

        
        
        $("#instance-accordion").append(pane.instanceHTML);
        let pane2 = new InstancePane(`${instanceName}2`, details.dateCreated, details.repos, details.clusterName, 
            details.collections, `222222222${details.cliURL}`);
        let pane3 = new InstancePane(`${instanceName}3`, details.dateCreated, details.repos, details.clusterName, 
        details.collections, `333333333${details.cliURL}`);
        $("#instance-accordion").append(pane2.instanceHTML);
        $("#instance-accordion").append(pane3.instanceHTML);
    }
    $(".loading-row").hide();
    $(".accordion-title:first").click();
}

// Set details on UI for any given instance
function setToolData(tools){
    let noTools = true;
    for(let tool of tools){
        if(typeof tool.label === "undefined" || tool.label.length === 0 || 
        typeof tool.location === "undefined" || tool.location.length === 0){
            continue;
        }
        if(tool.label === "Application Navigator"){
            $("#appnav-link").attr("href", tool.location);
            $("#manage-apps-button").attr("disabled", false);
            $("#manage-apps-button-text").html('Manage Applications');
        }
        //set kappnav url to manage applications link
        let toolPane = new ToolPane(tool.label, tool.location);
        $("#tool-data-container").append(toolPane.toolHTML);
        noTools = false;
    }

    if(noTools){
        $("#no-tools").show();
    }
}

function updateInstanceView(element){
    $(".bx--accordion__heading").attr('aria-expanded', false);
    $(".bx--accordion__heading").parent().removeClass('bx--accordion__item--active');


    if($(element).attr('aria-expanded') === 'false'){
        let paneId = $(element).attr('aria-controls');
        let instancePane = $(`#${paneId}`);
        let appHubName = $(instancePane).data('hubname');
        let appsodyURL = $(instancePane).data('appsodyurl');
        let codewindURL = $(instancePane).data('codewindurl');
        let numberOfCollections = $(instancePane).data('collections');
        let clientURL = $(instancePane).data('cliurl');
        $("#collections-card").html(`
        <div class="bx--row instance-number-row">
            <div class="bx--col">
              <h2>${numberOfCollections}</h2>
            </div>
          </div>
          <div class="bx--row">
            <div class="bx--col">
              <h4>Collections</h4>
            </div>
          </div>
          <div class="bx--row">
            <div class="bx--col">
                <div class="bx--row">
                    <div class="bx--col">
                        
                        <p><span class='gray-text'>Application Hub: </span>${appHubName}</p>
                    </div>
                </div>
                <div class="bx--row">
                    <div class="bx--col">
                    <div class="input-group">
                    <p class="gray-text">Appsody URL: </p>
                    <input id="appsodyURL0" type="text" class="form-control collection-hub-input tooltip-copy" readonly="readonly" onclick="this.select();" value=${appsodyURL} data-original-title="" title="">
                    <div class="input-group-append">
                        <img src="/img/copy-clipboard.png" alt="copy to clipboard icon" class="img img-fluid copy-to-clipboard tooltip-copy" data-original-title="" title="">
                    </div>
                </div>
                    </div>
                </div>
                <div class="bx--row">
                    <div class="bx--col">
                        <div class="input-group">
                        <p class="gray-text">Codewind URL: </p>
                            <input id="appsodyURL0" type="text" class="form-control collection-hub-input tooltip-copy" readonly="readonly" onclick="this.select();" value=${codewindURL} data-original-title="" title="">
                            <div class="input-group-append">
                                <img src="/img/copy-clipboard.png" alt="copy to clipboard icon" class="img img-fluid copy-to-clipboard tooltip-copy" data-original-title="" title="">
                            </div>
                        </div>
                    </div>
                </div>
                <div class="bx--row">
                    <div class="bx--col">
                        <p>Managment CLI: Use this endpoint with the Kabanero Management CLI login command to login and manage your collections. For more informaiton about using the CLI see the <a href="/docs/ref/general/kabanero-cli.html">Kabanero Management CLI documentation</a></p>
                        <div class="input-group">
                            <input id="appsodyURL0" type="text" class="form-control collection-hub-input tooltip-copy" readonly="readonly" onclick="this.select();" value=${clientURL} data-original-title="" title="">
                            <div class="input-group-append">
                                <img src="/img/copy-clipboard.png" alt="copy to clipboard icon" class="img img-fluid copy-to-clipboard tooltip-copy" data-original-title="" title="">
                            </div>
                        </div>
                    </div>
                </div>      
            </div>
          </div>`);
    }
    
}