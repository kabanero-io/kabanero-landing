$(document).ready(function () {
    handleInstancesRequests();
    $("#instance-accordion li").on("click", e => {
        // prevent carbon from doing its normal thing with the accordion
        e.stopPropagation();

        let newName = handleInstanceSelection(e.target);
        fetchAnInstance(newName)
            .then(updateInstanceView);
    });

    $("#sync-collections-icon").on("click", (e) => {
        if (e.target.getAttribute("class") == "icon-active") {
            let instanceName = $("#instance-accordion").find(".bx--accordion__title").text();
            hideAndEmptyTable();
            syncColletions(instanceName);
        }
    });

    $("#collection-table-body").on("click", ".deactivate-collection-icon", (e) => {
        let collectionName = e.currentTarget.getAttribute("collection-name");
        $("#modal-collection-name").text(collectionName);
    });

    $("#modal-confirm-deactivation").on("click", () => {
        let collectionName = $("#modal-collection-name").text();
        deactivateCollection(collectionName);
    });

});

function handleInstancesRequests(){
    hideAndEmptyTable();
    fetchAllInstances()
        .then(setInstanceSelections)
        .then(handleInitialCLIAuth)
        .then(handleCollectionsRequests);
        //TODO then select/fetch current instance from url param for the dropdown
}

function handleCollectionsRequests(instanceName) {
    getCollectionData(instanceName);
    getCliVersion(instanceName);
}

function getCollectionData(instanceName) {
    if (typeof instanceName === "undefined") {
        return;
    }
    return fetch(`/api/auth/kabanero/${instanceName}/collections/list`)
        .then(function (response) {
            return response.json();
        })
        .then(updateCollectionView)
        .catch(error => console.error("Error getting collections", error));
}

function getCliVersion(instanceName) {
    if (typeof instanceName === "undefined") {
        return;
    }
    return fetch(`/api/auth/kabanero/${instanceName}/collections/version`)
        .then(function (response) {
            return response.json()
        })
        .then(setCLIVersion)
        .catch(error => console.error("Error getting CLI Version", error));
}

function deactivateCollection(collectionName) {
    let instanceName = $("#instance-accordion").find(".bx--accordion__title").text();

    return fetch(`/api/auth/kabanero/${instanceName}/collections/deactivate/${collectionName}`)
        .then(function (response) {
            return response.json()
        })
        .then(handleInstancesRequests)
        .catch(error => console.error(`Error deactivating ${collectionName} collection`, error));
}

function syncColletions(instanceName) {
    if (typeof instanceName === "undefined") {
        return;
    }
    return fetch(`/api/auth/kabanero/${instanceName}/collections/sync`)
        .then(function (response) {
            return response.json()
        })
        .then(handleInstancesRequests)
        .catch(error => console.error("Error syncing collections", error))
}

function updateCollectionView(collectionJSON) {
    if (typeof collectionJSON === "undefined") {
        return;
    }

    let collections = collectionJSON["kabanero collections"];

    collections.forEach(coll => {
        $("#collection-table-body").append(createCollRow(coll));
    });

    function createCollRow(coll) {
        let row = $("<tr>");
        let name = $("<td>").text(coll.name);
        let version = $("<td>").text(coll.version);
        let status = $("<td>").text(coll.status);
        let deactivateCollection = createDeactivateCollectionButton(coll);
        return row.append([name, version, status, deactivateCollection]);
    }

    function createDeactivateCollectionButton(coll) {
        let iconStatus = coll.status === "active" ? "icon-active" : "icon-disabled";
        let deactivateCollection = $("<td>").addClass("deactivate-collection-td");
        let div = $("<div>").addClass(`deactivate-collection-icon ${iconStatus}`).attr("collection-name", coll.name).attr("data-modal-target", "#deactivate-collection-modal-" + iconStatus);
        let svg = `<svg focusable="false" preserveAspectRatio="xMidYMid meet" xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 32 32" aria-hidden="true" style="will-change: transform;"><path d="M16,2C8.2,2,2,8.2,2,16s6.2,14,14,14s14-6.2,14-14S23.8,2,16,2z M16,28C9.4,28,4,22.6,4,16S9.4,4,16,4s12,5.4,12,12	S22.6,28,16,28z"></path><path d="M21.4 23L16 17.6 10.6 23 9 21.4 14.4 16 9 10.6 10.6 9 16 14.4 21.4 9 23 10.6 17.6 16 23 21.4z"></path><title>Deactivate ${coll.name} collection</title></svg>`

        div.append(svg)
        deactivateCollection.append(div);

        return deactivateCollection;
    }

    showTable();
}

function setCLIVersion(cliVersion) {
    if(typeof cliVersion === "undefined"){
        return;
    }
    let version = cliVersion["image"].split(":")[1];
    $("#cli-version").append(version);
}

function hideAndEmptyTable(){
    $(".table-loader").show();
    $("#table-footer-cli-version").hide();
    $("#collections-sync-button").hide();
    $("#collection-table").hide();
    $("#collection-table-body").empty();
    $("#cli-version").empty();
    $("#instance-accordion").empty();
    $("#sync-collections-icon").removeClass("icon-active");
    $("#sync-collections-icon").addClass("icon-disabled");
}

function showTable(){
    // hide loader table and show table with data
    $(".table-loader").hide();
    $("#collection-table").show();
    $("#collections-sync-button").show();
    $("#table-footer-cli-version").show();
    $("#cli-version").show();
    $("#collections-sync-button").show();
    $("#sync-collections-icon").addClass("icon-active");
    $("#sync-collections-icon").removeClass("icon-disabled");
}

function getURLParam(key){
    return new URLSearchParams(window.location.search).get(key);
}

function handleInitialCLIAuth(instanceName){
    return fetch(`/api/auth/kabanero/${instanceName}/collections/list`)
        .then(function(response) {

            // Login via cli and retry if 401 is returned on initial call
            if(response.status === 401){
                return loginViaCLI(instanceName)
                    .then(() => {
                        return handleInitialCLIAuth(instanceName);
                    });
            }
            else if(response.status !== 200){
                console.warn(`Initial auth into instance ${instanceName} returned status code: ${response.status}`);
            }

            // pass on instance name var to the next function in the promise chain
            return instanceName;
        })
        .catch(error => console.error(`Error handling initial auth into instance ${instanceName} via CLI server`, error));
}

function loginViaCLI(instanceName){
    if(typeof instanceName === "undefined"){
        console.warn("CLI login cannot login without an instanceName");
        return;
    }

    return fetch(`/api/auth/kabanero/${instanceName}/collections/login`)
        .catch(error => console.error(`Error logging into instance ${instanceName} via CLI server`, error));
}