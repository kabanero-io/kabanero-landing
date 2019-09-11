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
    $(document).on('click', '.collection-hub-input', function(){
        $("#copy-img").tooltip("show");
        copy($(this));
    });

    $(document).on('click', '.copy-to-clipboard', function(){
        copy($(this).prev("input"));
    });

    function copy(input){
        $(input).select();
        document.execCommand('copy');

        setTimeout(function(){
            $(".tooltip-copy").tooltip("hide");
        }, 1000)
    }
}

// Request to get all instances names
function loadAllInfo(){
    fetchAllInstances()
    .then(setInstanceData)

    fetchAllTools()
    .then(setToolData);
}

// Set details on UI for any given instance
function setInstanceData(instances){
    for(instance of instances){
        let instanceName = instance.instanceName;
        let instanceDetails = instance.details || {};

        let pane = new InstancePane(instanceName, instanceDetails.dateCreated, instanceDetails.collectionHubURL, 
                                    instanceDetails.clusterName, instanceDetails.collections, instanceDetails.tektonDashboard);

        $("#instance-data-container").append(pane.instanceHTML);
    }
}

// Set details on UI for any given instance
function setToolData(tools){
    let hasTools = false;
    for(tool of tools){
        if(typeof tool.label === "undefined" || tool.label.length === 0 || 
        typeof tool.location === "undefined" || tool.location.length === 0){
            continue;
        }

        let toolPane = new ToolPane(tool.label, tool.location);
        $("#tool-data-container").append(toolPane.toolHTML);
        hasTools = true;
    }

    if(hasTools){
        $("#tool-data-container").show();
    }
}
