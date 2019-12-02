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
        col.append($("<strong/>", {text: `${this.label}: `}));
        col.append($("<a/>", {href: this.location, target: "_blank", text: this.location}));
        return row.append(col);
    }
};

let InstancePane = class {
    constructor(instanceName, date, collectionHub, cluster, collections, cliURL) {
        this.instanceName = instanceName;
        // If Date cannot be parsed, then return it to original non-parsable value, otherwise, use UTC date
        this.date = String(new Date(date)) === "Invalid Date" ? date : new Date(date).toUTCString();
        this.collectionHub = collectionHub;
        this.cluster = cluster;
        this.collections = collections;
        this.cliURL = cliURL;
        this.stringCollections = this.collections.reduce((acc, coll, index) => {
            let pair = `${coll.name} - ${coll.version}`;
            if(index !== this.collections.length -1){
                pair += ",";
            }
            return acc + pair;
        }, "");
    }

    get instanceNameHTML(){
        let row = $("<div/>", {class: "row"});
        let col = $("<div/>", {class: "col"});
        col.append($("<h3/>", {text: this.instanceName}));
        return row.append(col);
    }

    static createDetailRowHTMLForString(label, val, isHTML){
        // do not show any values that aren't set
        if(typeof val === "undefined" || val.length === 0){
            return;
        }

        let row = $("<div/>", {class: "row"});
        let col = $("<div/>", {class: "col-md"});
        col.append($("<strong/>", {text: `${label}: `}));

        // use html only when we have to, this mitigates xss
        let span = isHTML ? $("<span/>", {html: val }) : $("<span/>", {text: val });
        col.append(span);
        return row.append(col);
    }

    static createCollectionHubTable(label, collectionHubMaturities){
        if(typeof collectionHubMaturities === "undefined" || collectionHubMaturities.length === 0){
            return;
        }

        let row = $("<div/>", {class: "row"});
        let col = $("<div/>", {class: "col-md-11"});
        col.append($("<strong/>", {text: `${label}: `}));

        // Each Collection Hub has categories (Maturities). These categories categorize the collections based on their maturity.
        // For example, collections that meet technical requirements that Kabanero considers ready for production would be in the "stable" maturity.
        // We will show the Collection Hub URL's for each maturity in the collection hub.
        for(let [index, maturity] of collectionHubMaturities.entries()){
            let maturityTable = $("<table/>", {class: "table indent coll-table"}).append($("<caption/>", {text: maturity.name}));

            let appsodyLabel = $("<td/>", {class: "align-middle"}).append("Appsody URL");
            let appsodyURL = $("<td/>", {class: "align-middle"}).append(InstancePane.createCopyInput(`appsodyURL${index}`, maturity.appsodyURL));
            let appsodyRow = $("<tr/>").append(appsodyLabel, appsodyURL);

            let codewindLabel = $("<td/>", {class: "align-middle"}).append("Codewind URL");
            let codewindURL = $("<td/>", {class: "align-middle"}).append(InstancePane.createCopyInput(`codewindURL${index}`, maturity.codewindURL));
            let codewindRow = $("<tr/>").append(codewindLabel, codewindURL);

            let tBody = $("<tbody/>").append(appsodyRow, codewindRow);
            maturityTable.append(tBody);
            col.append(maturityTable);
        }
        return row.append(col);
    }

    static createCopyInput(id, url){
        // Image is used to let the user know they can click to copy the URL. The inputIDToCopy data attribute will let the click 
        // event konw which input to copy the URL from (helpful when there's multiple)
        let img = $("<img />", {src: "/img/copy-clipboard.png", alt: "copy to clipboard icon", class: "img img-fluid copy-to-clipboard tooltip-copy"}).data("inputIDToCopy", id);

        img.tooltip({title: "copied!", trigger: "click"});

        let wrapper = $("<div/>", {class: "input-group"});

        let copyImgWrapper = $("<div/>", {class: "input-group-append"});
        copyImgWrapper.append(img);

        let input = $("<input/>", {id, type: "text", class: "form-control collection-hub-input tooltip-copy", readonly: "readonly", onClick: "this.select();", value: url}) 
            .tooltip({title: url, container: "body", placement: "top", trigger: "hover"});
        return wrapper.append(input, copyImgWrapper);
    }

    static createDetailRowHTMLForCollections(label, collArr){
        // do not show any values that aren't set
        if(typeof collArr === "undefined" || collArr.length === 0){
            return;
        }

        let row = $("<div/>", {class: "row"});
        let col = $("<div/>", {class: "col-md"});
        col.append($("<strong/>", {text: `${label}: `}));

        let sortedColls = InstancePane.sortColls(collArr);

        let ul = $("<ul/>");
        for(let collection of sortedColls){
            ul.append($("<li/>", {text: `${collection.name} - ${collection.version}`}));
        }

        col.append(ul);

        return row.append(col);
    }

    static createCLI(id, cliURL) {
        let row = $("<div/>", {class: "row"});
        let col = $("<div/>", {class: "col-md-11"});
        let copyInput = InstancePane.createCopyInput(id, cliURL).addClass("indent");
        let cliHTML = "Use this endpoint with the Kabanero Management CLI login command to login and manage your collections. " + 
            "For more information about using the CLI see the <a href='/docs/ref/general/kabanero-cli.html'>Kabanero Management CLI documentation</a>.";
        col.append($("<strong/>", {text: "Management CLI"}), $("<p/>", {html: cliHTML, class: "indent"}), copyInput);
        return row.append(col);
    }

    static sortColls(colArry){
        return colArry.sort((a,b) => a.name.localeCompare(b.name));
    }
    
    get instanceHTML(){
        return `<li data-accordion-item class="bx--accordion__item">
        <button class="bx--accordion__heading accordion-title" aria-expanded="false" aria-controls="pane${this.instanceName}" onclick=updateInstanceView(this)>
          <svg focusable="false" preserveAspectRatio="xMidYMid meet" style="will-change: transform;" xmlns="http://www.w3.org/2000/svg" class="bx--accordion__arrow" width="16" height="16" viewBox="0 0 16 16" aria-hidden="true"><path d="M11 8L6 13 5.3 12.3 9.6 8 5.3 3.7 6 3z"></path></svg>
          <div class="bx--accordion__title">${this.instanceName}</div>
        </button>
        <div id="pane${this.instanceName}" class="bx--accordion__content" data-hubName="${this.collectionHub[0].name}" data-appsodyURL="${this.collectionHub[0].appsodyURL}" data-codewindURL="${this.collectionHub[0].codewindURL}" data-collections="${this.stringCollections}" data-cliURL="${this.cliURL}">
          <p class="gray-text">Date created</p>
          <p>${this.date}</p>
        </div>
      </li>`;
    }
};

