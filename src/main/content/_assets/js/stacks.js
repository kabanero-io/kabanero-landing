$(document).ready(function () {
    handleInstancesRequests();
    $("#instance-accordion li").on("click", e => {
        // prevent carbon from doing its normal thing with the accordion
        e.stopPropagation();

        let newName = handleInstanceSelection(e.target);
        fetchAnInstance(newName)
            .then(updateInstanceView);
    });

    $("#sync-stacks-icon").on("click", (e) => {
        if (e.target.getAttribute("class") == "icon-active") {
            let instanceName = $("#instance-accordion").find(".bx--accordion__title").text();
            emptyTable();
            syncStacks(instanceName);
        }
    });

    $("#stack-table-body").on("click", ".deactivate-stack-icon", e => {
        let $event = $(e.currentTarget);
        let name = $event.data("stackname");
        let version = $event.data("stackversion");
        $("#modal-stack-name").text(name);
        $("#modal-stack-version").text(version);
    });

    $("#modal-confirm-deactivation").on("click", () => {
        let name = $("#modal-stack-name").text();
        let version = $("#modal-stack-version").text();
        emptyTable();
        deactivateStack(name, version);
    });

});

function handleInstancesRequests() {
    $("#instance-accordion").empty();
    fetchAllInstances()
        .then(setInstanceSelections)
        .then(handleInitialCLIAuth)
        .then(handleStacksRequests);
    //TODO select/fetch current instance from url param for the dropdown
}

function handleStacksRequests(instanceName) {
    getStacksData(instanceName);
    getCliVersion(instanceName);
}

function getStacksData(instanceName) {
    if (typeof instanceName === "undefined") {
        return;
    }
    return fetch(`/api/auth/kabanero/${instanceName}/stacks`)
        .then(function (response) {
            return response.json();
        })
        .then(updateStackView)
        .catch(error => console.error("Error getting stacks", error));
}

function getCliVersion(instanceName) {
    if (typeof instanceName === "undefined") {
        return;
    }
    return fetch(`/api/auth/kabanero/${instanceName}/stacks/version`)
        .then(function (response) {
            return response.json();
        })
        .then(setCLIVersion)
        .catch(error => console.error("Error getting CLI Version", error));
}

function deactivateStack(name, version) {
    let instanceName = $("#instance-accordion").find(".bx--accordion__title").text();

    return fetch(`/api/auth/kabanero/${instanceName}/stacks/${name}/versions/${version}`, { method: "DELETE" })
        .then(function (response) {
            return response.json();
        })
        .then(handleInstancesRequests)
        .catch(error => console.error(`Error deactivating ${name} ${version} stack`, error));
}

function syncStacks(instanceName) {
    if (typeof instanceName === "undefined") {
        return;
    }
    return fetch(`/api/auth/kabanero/${instanceName}/stacks/sync`, { method: "PUT" })
        .then(handleInstancesRequests)
        .catch(error => console.error("Error syncing stacks", error));
}

function updateStackView(stackJSON) {
    if (typeof stackJSON === "undefined") {
        return;
    }

    // yaml metadata in kube, cannot be deactivated and has no status.
    let curatedStacks = stackJSON["curated stacks"];

    // difference between kabanero stacks and curated stacks. 
    // If there are stacks in this array then a "sync" needs to happen to pull them into kabanero
    let newCuratedStacks = stackJSON["new curated stacks"];

    let kabaneroStacks = stackJSON["kabanero stacks"];

    // when a stack yaml is deleted from the cluter, but it’s still out on Kabanero. A sync will clean these up.
    let obsoleteStacks = stackJSON["obsolete stacks"];

    kabaneroStacks.forEach(stack => {
        $("#stack-table-body").append(createKabaneroStackRow(stack));
    });

    curatedStacks.forEach(stack => {
        $("#curated-stack-table-body").append(createCuratedStackRow(stack));
    });

    function createKabaneroStackRow(stack) {
        let rows = [];
        let versions = stack.status;

        versions.forEach(version => {
            let name = $("<td>").text(stack.name);
            let versionTD = $("<td>").text(version.version);
            let statusTD = $("<td>").text(version.status);
            let deactivateStack = createDeactivateStackButton(stack.name, version);
            let row = $("<tr>").append([name, versionTD, statusTD, deactivateStack]);
            rows.push(row);
        });
        return rows;
    }

    function createCuratedStackRow(stack) {
        let rows = [];
        let versions = stack.versions;

        versions.forEach(version => {
            let name = $("<td>").text(stack.name);
            let versionTD = $("<td>").text(version.version);
            let images = version.images.reduce((acc, imageObj) => {
                return acc += `${imageObj.image}<br/>`;
            }, "");

            let imagesTD = $("<td>").html(images);
            let row = $("<tr>").append([name, versionTD, imagesTD]);
            rows.push(row);
        });
        return rows;
    }

    function createDeactivateStackButton(stackName, versionObj) {
        let iconStatus = versionObj.status === "active" ? "icon-active" : "icon-disabled";
        let deactivateStack = $("<td>").addClass("deactivate-stack-td");

        let div = $("<div>").addClass(`deactivate-stack-icon ${iconStatus}`)
            .data("stackname", stackName)
            .data("stackversion", versionObj.version)
            .attr("data-modal-target", `#deactivate-stack-modal-${iconStatus}`);

        let svg = `<svg focusable="false" preserveAspectRatio="xMidYMid meet" xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 32 32" aria-hidden="true" style="will-change: transform;"><path d="M16,4A12,12,0,1,1,4,16,12,12,0,0,1,16,4m0-2A14,14,0,1,0,30,16,14,14,0,0,0,16,2Z"></path><path d="M10 15H22V17H10z"></path><title>Deactivate ${stackName} - ${versionObj.version} stack</title></svg>`;

        div.append(svg);
        return deactivateStack.append(div);
    }

    $(".table-loader").hide();
    $("#stack-table").show();
    $("#curated-stack-table").show();
}

function setCLIVersion(cliVersion) {
    if (typeof cliVersion === "undefined") {
        return;
    }
    let version = cliVersion["image"].split(":")[1];
    $("#cli-version").append(version);
}

function emptyTable() {
    $("#stack-table").hide();
    $("#curated-stack-table").hide();
    $(".table-loader").show();
    $("#stack-table-body").empty();
    $("#curated-stack-table-body").empty();
    $("#cli-version").empty();
}

function getURLParam(key) {
    return new URLSearchParams(window.location.search).get(key);
}

function handleInitialCLIAuth(instanceName) {
    return fetch(`/api/auth/kabanero/${instanceName}/stacks`)
        .then(function (response) {

            // Login via cli and retry if 401 is returned on initial call
            if (response.status === 401) {
                return loginViaCLI(instanceName)
                    .then(() => {
                        return handleInitialCLIAuth(instanceName);
                    });
            }
            else if (response.status !== 200) {
                console.warn(`Initial auth into instance ${instanceName} returned status code: ${response.status}`);
            }

            // pass on instance name var to the next function in the promise chain
            return instanceName;
        })
        .catch(error => console.error(`Error handling initial auth into instance ${instanceName} via CLI server`, error));
}

function loginViaCLI(instanceName) {
    if (typeof instanceName === "undefined") {
        console.warn("CLI login cannot login without an instanceName");
        return;
    }

    return fetch(`/api/auth/kabanero/${instanceName}/stacks/login`, { method: "POST" })
        .catch(error => console.error(`Error logging into instance ${instanceName} via CLI server`, error));
}
