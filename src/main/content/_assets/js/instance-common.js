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
function fetchAllInstances() {
    return fetch("/api/kabanero")
        .then(function (response) {
            return response.json();
        })
        .catch(error => console.error("Error getting instance names:", error));
}

function fetchAnInstance(instanceName) {
    if (typeof instanceName === "undefined") {
        return;
    }
    return fetch(`/api/kabanero/${instanceName}`)
        .then(function (response) {
            return response.json();
        })
        .catch(error => console.error(`Error getting instance info for: ${instanceName}`, error));
}

function fetchOAuthDetails() {
    return fetch("/api/install/oauth")
        .then(function (response) {
            return response.json();
        })
        .catch(error => console.error("Error getting oauth info", error));
}

function fetchAllTools() {
    return fetch("/api/tools")
        .then(function (response) {
            return response.json();
        })
        .catch(error => console.error("Error getting tools", error));
}

function fetchATool(tool) {
    return fetch(`/api/tools/${tool}`)
        .then(function (response) {
            return response.json();
        })
        .catch(error => console.error(`Error getting ${tool} tool`, error));
}

function fetchStacks(instanceName) {
    if (typeof instanceName === "undefined") {
        return;
    }

    return fetch(`/api/kabanero/${instanceName}/stacks`)
        .then(function (response) {
            return response.json();
        })
        .then(setStackCard)
        .catch(error => console.error("Error getting stacks", error));
}

function fetchUserAdminStatus(oauthJSON) {
    let instanceName = $("#instance-accordion").find(".bx--accordion__title").text();
    
    if (typeof instanceName === "undefined" || !(oauthJSON && oauthJSON.isConfigured)) {
        return;
    }
    return fetch(`/api/kabanero/${instanceName}/isAdmin`)
        .then(function (response) {
            return response.json();
        })
        .then(fetchInstanceAdmins)
        .catch(error => console.error("Error getting github user", error));
}


function fetchInstanceAdmins(isAdmin){
    let instanceName = $("#instance-accordion").find(".bx--accordion__title").text();

    if (typeof instanceName === "undefined" || !isAdmin) {
        return;
    }
    
    return fetch(`/api/kabanero/${instanceName}/admin`)
        .then(function (response) {
            return response.json();
        })
        .then(updateInstanceAdminView)
        .catch(error => console.error("Error getting stacks", error));
}

function fetchGithubUserDetails(githubUsername){
    if (!githubUsername || githubUsername == null) {
        return;
    }
    return fetch(`/api/auth/git/user/${githubUsername}`)
        .then(function (response) {
            return response.json();
        })
        .catch(error => console.error(`Permission denied or user ${githubUsername} doesn’t exist`, error));
    }

function addTeamMember(target) {
    let $addUserContainer = $(target).closest(".addUser-input-container");
    let teamId = $(target).closest(".bx--accordion__item").find(".admin-modal-accordion-title").attr("teamId");
    let githubUsername = $addUserContainer.find(".add-user-text-input")[0].value;

    $addUserContainer.find(".text-input-wrapper").removeAttr("data-invalid");
    $addUserContainer.find(".input-error-msg-icon").addClass("hidden");

    if (typeof teamId === "undefined" || typeof githubUsername === "undefined" || githubUsername === null) {
        return;
    }

    if (githubUsername === "") {
        $addUserContainer.find(".input-error-message").text("Username is required");
        $addUserContainer.find(".text-input-wrapper").attr("data-invalid", true);
        $addUserContainer.find(".input-error-msg-icon").removeClass("hidden");
    }

    return fetch(`/api/auth/git/team/${teamId}/member/${githubUsername}`, {
        method: 'POST'
    })
        .then(function (response) {
            return response.json()
        })
        .then(data => {
            if (data.msg.includes("404")) {
                $addUserContainer.find(".input-error-message").text(`Permission denied or user ${githubUsername} doesn’t exist`);
                $addUserContainer.find(".text-input-wrapper").attr("data-invalid", true);
                $addUserContainer.find(".input-error-msg-icon").removeClass("hidden");
                return;
            }
            fetchOAuthDetails();
        })
        .catch(error => console.error(`Permission denied or user ${githubUsername} doesn’t exist`, error));

}

function removeTeamMember(target) {
    let teamId = $(target).closest(".bx--accordion__item").find(".admin-modal-accordion-title").attr("teamId");
    let teamName = $(target).closest(".bx--accordion__item").find(".admin-modal-accordion-title").text();
    let githubUsername = $(target).closest(".instance-inline-admin-list-notification").find(".github-admin-modal-username").text();

    if (typeof teamId === "undefined" || typeof githubUsername === "undefined") {
        return;
    }
    return fetch(`/api/auth/git/team/${teamId}/member/${githubUsername}`, {
        method: 'DELETE'
    })
        .then(function (response) {
            return response.json()
        })
        .then(data => {
            if (data.msg.includes("404")) {
                let inlineNotification = $(target).closest(".bx--accordion__content");
                inlineNotification.find(".remove-user-error-notification").removeClass("hidden");
                inlineNotification.find(".remove-user-error-notification-content").empty();
                inlineNotification.find(".remove-user-error-notification-content").text(`Permission denied or user ${githubUsername} doesn’t exist in team ${teamName}`);
                return;
            }
            fetchOAuthDetails();
        })
        .catch(error => console.error(`Permission denied or user ${githubUsername} doesn’t exist`, error));
}

function updateInstanceAdminView(adminMembersJson) {
    if (!adminMembersJson || adminMembersJson == null) {
        return;
    }

    $("#instance-accordion-admins-list").empty();
    $("#admin-modal-list").empty();

    let instanceAdmins = [];

    adminMembersJson.forEach(team => {
        if (team.members.length === 0) {
            return;
        }

        let row = $("#modal-teams-li-template").clone().removeAttr("id").removeClass("hidden");
        $(row).find(".admin-modal-accordion-title").text(team.name).attr("teamId", team.id);
        $("#admin-modal-list").append(row);

        let div = $("<div>").addClass("member-list-container");

        team.members.forEach(member => {
            instanceAdmins.push(member)
            fetchGithubUserDetails(member.login)
                .then((data) => {
                    let userDetails = data;
                    let userLogin = userDetails.login;
                    let userFullName = userDetails.name == null ? "Unavailable" : userDetails.name;
                    let userEmail = userDetails.email == null ? "Unavailable" : userDetails.email;

                    let userInfoBox = $("#instance-admin-list-notification-template").clone().removeAttr("id").removeClass("hidden");
                    $(userInfoBox).find(".github-admin-modal-username").text(userLogin);
                    $(userInfoBox).find(".github-admin-modal-full-name").text(userFullName);
                    $(userInfoBox).find(".github-admin-modal-email").text(userEmail);
                    $(userInfoBox).find(".bx--assistive-text").text(`Remove ${userLogin} from ${team.name} team`)
                    div.append(userInfoBox);
                });
        });

        $(row).find(".admin-modal-accordion-content").append(div)
        $("#admin-modal-list").append(row);

        let addUserInputText = $("#addUser-input-template").clone().removeAttr("id").removeClass("hidden");
        addUserInputText.find(".user-add-team-name").text(team.name);
        $(addUserInputText).insertAfter($(".admin-modal-accordion-content").children().last())
    });

    let uniqueAdminList = Array.from(new Set(instanceAdmins));
    uniqueAdminList.forEach(user => {
        let githubAdminUsername = user.login;
        $("#instance-accordion-admins-list").append(`<span class="instance-admin-names">${githubAdminUsername}<span>`);
        $("#instance-accordion-admin-view").removeClass("hidden");
    });
}

let ToolPane = class {
    constructor(label, location, paneText, buttonText, https) {
        this.label = label;
        this.location = location;
        this.paneText = paneText;
        this.buttonText = buttonText;
        this.https = https;
    }

    get toolHTML() {
        let tile = $($("#tool-tile-template").clone()[0].innerHTML);
        let protocol = this.https ? "https://" : "http://";

        $(tile).find(".bx--tile").attr("id", this.label.toLowerCase().split(" ").join("-"));
        $(tile).find(".tile-title").text(`${this.label}`);
        $(tile).find(".tile-text").text(`${this.paneText}`);
        $(tile).find("a").attr("href", protocol + this.location);
        $(tile).find(".button-text").text(`${this.buttonText}`);
        return tile;
    }
};

let InstancePane = class {
    constructor(instanceName, date, stackHub, cluster, stacks, cliURL) {
        this.instanceName = instanceName;
        // If Date cannot be parsed, then return it to original non-parsable value, otherwise, use UTC date
        this.date = String(new Date(date)) === "Invalid Date" ? date : new Date(date).toUTCString();
        this.stackHub = stackHub;
        this.cluster = cluster;
        this.stacks = stacks;
        this.cliURL = cliURL;
        // Take the stacks array of objects and turn it into a nice human readable string
        this.stringStacks = this.stacks.reduce((acc, coll, index) => {
            let pair = `${coll.name} - ${coll.version}`;
            if (index !== this.stacks.length - 1) {
                pair += ",";
            }
            return acc + pair;
        }, "");
    }

    get instanceNameHTML() {
        let row = $("<div/>", { class: "row" });
        let col = $("<div/>", { class: "col" });
        col.append($("<h3/>", { text: this.instanceName }));
        return row.append(col);
    }

    static createDetailRowHTMLForString(label, val, isHTML) {
        // do not show any values that aren't set
        if (typeof val === "undefined" || val.length === 0) {
            return;
        }

        let row = $("<div/>", { class: "row" });
        let col = $("<div/>", { class: "col-md" });
        col.append($("<strong/>", { text: `${label}: ` }));

        // use html only when we have to, this mitigates xss
        let span = isHTML ? $("<span/>", { html: val }) : $("<span/>", { text: val });
        col.append(span);
        return row.append(col);
    }

    static createStackHubTable(label, stackHubMaturities) {
        if (typeof stackHubMaturities === "undefined" || stackHubMaturities.length === 0) {
            return;
        }

        let row = $("<div/>", { class: "row" });
        let col = $("<div/>", { class: "col-md-11" });
        col.append($("<strong/>", { text: `${label}: ` }));

        // Each Stack Hub has categories (Maturities). These categories categorize the stacks based on their maturity.
        // For example, stacks that meet technical requirements that Kabanero considers ready for production would be in the "stable" maturity.
        // We will show the Stack Hub URL's for each maturity in the stack hub.
        for (let [index, maturity] of stackHubMaturities.entries()) {
            let maturityTable = $("<table/>", { class: "table indent coll-table" }).append($("<caption/>", { text: maturity.name }));

            let appsodyLabel = $("<td/>", { class: "align-middle" }).append("Appsody URL");
            let appsodyURL = $("<td/>", { class: "align-middle" }).append(InstancePane.createCopyInput(`appsodyURL${index}`, maturity.appsodyURL));
            let appsodyRow = $("<tr/>").append(appsodyLabel, appsodyURL);

            let codewindLabel = $("<td/>", { class: "align-middle" }).append("Codewind URL");
            let codewindURL = $("<td/>", { class: "align-middle" }).append(InstancePane.createCopyInput(`codewindURL${index}`, maturity.codewindURL));
            let codewindRow = $("<tr/>").append(codewindLabel, codewindURL);

            let tBody = $("<tbody/>").append(appsodyRow, codewindRow);
            maturityTable.append(tBody);
            col.append(maturityTable);
        }
        return row.append(col);
    }

    static createCopyInput(id, url) {
        // Image is used to let the user know they can click to copy the URL. The inputIDToCopy data attribute will let the click 
        // event konw which input to copy the URL from (helpful when there's multiple)
        let img = $("<img />", { src: "/img/copy-clipboard.png", alt: "copy to clipboard icon", class: "img img-fluid copy-to-clipboard tooltip-copy" }).data("inputIDToCopy", id);

        img.tooltip({ title: "copied!", trigger: "click" });

        let wrapper = $("<div/>", { class: "input-group" });

        let copyImgWrapper = $("<div/>", { class: "input-group-append" });
        copyImgWrapper.append(img);

        let input = $("<input/>", { id, type: "text", class: "form-control stack-hub-input tooltip-copy", readonly: "readonly", onClick: "this.select();", value: url })
            .tooltip({ title: url, container: "body", placement: "top", trigger: "hover" });
        return wrapper.append(input, copyImgWrapper);
    }

    static createDetailRowHTMLForStacks(label, collArr) {
        // do not show any values that aren't set
        if (typeof collArr === "undefined" || collArr.length === 0) {
            return;
        }

        let row = $("<div/>", { class: "row" });
        let col = $("<div/>", { class: "col-md" });
        col.append($("<strong/>", { text: `${label}: ` }));

        let sortedColls = InstancePane.sortColls(collArr);

        let ul = $("<ul/>");
        for (let stack of sortedColls) {
            ul.append($("<li/>", { text: `${stack.name} - ${stack.version}` }));
        }

        col.append(ul);

        return row.append(col);
    }

    static createCLI(id, cliURL) {
        let row = $("<div/>", { class: "row" });
        let col = $("<div/>", { class: "col-md-11" });
        let copyInput = InstancePane.createCopyInput(id, cliURL).addClass("indent");
        let cliHTML = "Use this endpoint with the Kabanero Management CLI login command to login and manage your stacks. " +
            "For more information about using the CLI see the <a href='/docs/ref/general/reference/kabanero-cli.html'>Kabanero Management CLI documentation</a>.";
        col.append($("<strong/>", { text: "Management CLI" }), $("<p/>", { html: cliHTML, class: "indent" }), copyInput);
        return row.append(col);
    }

    static sortColls(colArry) {
        return colArry.sort((a, b) => a.name.localeCompare(b.name));
    }

    get instanceHTML() {
        return `<li data-accordion-item class="bx--accordion__item">
        <button class="bx--accordion__heading accordion-title" aria-expanded="false" aria-controls="pane${this.instanceName}" onclick=updateInstanceView(this)>
          <svg focusable="false" preserveAspectRatio="xMidYMid meet" style="will-change: transform;" xmlns="http://www.w3.org/2000/svg" class="bx--accordion__arrow" width="16" height="16" viewBox="0 0 16 16" aria-hidden="true"><path d="M11 8L6 13 5.3 12.3 9.6 8 5.3 3.7 6 3z"></path></svg>
          <div class="bx--accordion__title">${this.instanceName}</div>
        </button>
        <div id="pane${this.instanceName}" class="bx--accordion__content" data-hubName="${this.stackHub[0].name}" data-appsodyURL="${this.stackHub[0].appsodyURL}" data-codewindURL="${this.stackHub[0].codewindURL}" data-stacks="${this.stringStacks}" data-cliURL="${this.cliURL}">
          <p class="gray-text">Date created</p>
          <p>${this.date}</p>
        </div>
      </li>`;
    }
};

// Set each instance name in the accordion selection and returns the instance name to be loaded
function setInstanceSelections(instancesJSON) {    
    let instances = instancesJSON.items;
    if (typeof instances === "undefined" || instances.length === 0) {
        $("#instance-accordion #error-li").show();

        $(".bx--inline-loading").hide();
        console.error("No Kabanero instances were returned");
        return;
    }

    $("#instance-accordion").empty();

    for (let instance of instances) {
        let dateCreated = instance.metadata.creationTimestamp;

        let row = $("#instance-li-template").clone().removeAttr("id").removeClass("hidden");
        $(row).find(".bx--accordion__title").text(instance.metadata.name);
        $(row).find(".creation-date").text(new Date(dateCreated).toLocaleDateString());
        $("#instance-accordion").append(row);
    }

    // Remove the error li from the carbon accordion. Carbon needs at least 1 li element there on load or it throws an error...
    $("#instance-accordion #error-li").remove();

    // Expand (select) the first instance
    let $firstInstance = $("#instance-accordion li").first();
    $firstInstance.addClass("bx--accordion__item--active").attr("selected-instance", "");
    return $firstInstance.find(".bx--accordion__title").text().trim();
}

// Change the accordion when a new instance is clicked and return the new selected instance name
function handleInstanceSelection(elem) {
    // children of the li can be clicked, we need to make sure the li is the one with focus here
    elem = elem.closest("li");

    // If the user selects the instance that is already selected, do nothing
    if ($(elem).attr("selected-instance")) {
        return;
    }

    // close the previous selection
    $("#instance-accordion li[selected-instance]").removeAttr("selected-instance");
    $("#instance-accordion li.bx--accordion__item--active").removeClass("bx--accordion__item--active");

    // open the next selection
    $(elem).attr("selected-instance", "");
    $(elem).addClass("bx--accordion__item--active");
    return $(elem).find(".bx--accordion__title").text().trim();
}