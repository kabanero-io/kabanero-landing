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

function fetchAllInstances(){
    return fetch("/api/instances")
        .then(function(response) {
            return response.json();
        })
        .catch(error => console.error("Error getting instance names:", error));
}

function fetchAInstance(instance){
    return fetch(`/api/instances/${instance}`)
        .then(function(response) {
            return response.json();
        })
        .catch(error => console.error(`Error getting instance info for: ${instance}`, error));
}

function fetchAllTools(){
    return fetch("/api/tools")
        .then(function(response) {
            return response.json();
        })
        .catch(error => console.error("Error getting tools", error));
}

function fetchATool(tool){
    return fetch(`/api/tools/${tool}`)
        .then(function(response) {
            return response.json();
        })
        .catch(error => console.error(`Error getting ${tool} tool`, error));
}

let ToolPane = class {
    constructor(label, location) {
        this.label = label;
        this.location = location;
    }

    get toolHTML(){
        let row = $("<div/>", {class: "row"});
        let col = $("<div/>", {class: "col-md"});
        col.append($("<strong/>", {text: `${this.label}: `}))
        col.append($("<a/>", {href: this.location, target: "_blank", text: this.location}));
        return row.append(col);
    }
};

let InstancePane = class {
    constructor(instanceName, date, collectionHubURL, cluster, collections) {
        this.instanceName = instanceName;

        // If Date cannot be parsed, then return it to original non-parsable value, otherwise, use UTC date
        this.date = String(new Date(date)) === "Invalid Date" ? date : new Date(date).toUTCString();
        this.collectionHubURL = collectionHubURL;
        this.cluster = cluster;
        this.collections = collections;
    }

    get instanceNameHTML(){
        let row = $("<div/>", {class: "row"});
        let col = $("<div/>", {class: "col"});
        col.append($("<h3/>", {text: this.instanceName}))
        return row.append(col);
    }

    createDetailRowHTMLForString(label, val, isHTML){
        // do not show any values that aren't set
        if(typeof val === "undefined" || val.length === 0){
            return;
        }

        let row = $("<div/>", {class: "row"});
        let col = $("<div/>", {class: "col-md"});
        col.append($("<strong/>", {text: `${label}: `}))

        // use html only when we have to, this mitigates xss
        let span = isHTML ? $("<span/>", {html: val }) : $("<span/>", {text: val })
        col.append(span);
        return row.append(col);
    }

    createCollectionHubInput(label, url){
        let input = $("<input/>",{type: "text", class: "collection-hub-input tooltip-copy", readonly: "readonly", onClick: "this.select();", value: url});
        let img = $("<img />",{id:"copy-img", src: "/img/copy-clipboard.png", alt: "copy collection hub url to clipboard icon", class: "copy-to-clipboard tooltip-copy"})
            .tooltip({title: "copied!", trigger: "click"});
        let wrapper = $("<span/>").append(input, img);

        return this.createDetailRowHTMLForString(label, wrapper, true);
    }

    createDetailRowHTMLForCollections(label, collObj){
        // do not show any values that aren't set
        if(typeof collObj === "undefined" || Object.keys(collObj).length === 0){
            return;
        }

        let row = $("<div/>", {class: "row"});
        let col = $("<div/>", {class: "col-md"});
        col.append($("<strong/>", {text: `${label}: `}))

        let sortedKeys = Object.keys(collObj).sort();

        let ul = $("<ul/>");
        sortedKeys.forEach(function(key){
            ul.append($("<li/>", {text: `${collObj[key].name} - ${collObj[key].version}`}));
        })

        col.append(ul);

        return row.append(col);
    }
    
    get instanceHTML(){
        let topRow = $("<div/>", {class: "row"});
        let innerCol = $("<div/>", {class: "col-md"});
        innerCol
            .append(this.instanceNameHTML)
            .append(this.createDetailRowHTMLForString("Date Created", this.date, false))
            .append(this.createCollectionHubInput("Collection Hub", this.collectionHubURL))
            .append(this.createDetailRowHTMLForString("Cluster", this.cluster, false))
            .append(this.createDetailRowHTMLForCollections("Collections", this.collections, false));
        topRow.append(innerCol);
        return topRow;
    }
};
