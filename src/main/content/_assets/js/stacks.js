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
            emptyTable();
            syncColletions(instanceName);
        }
    });

    $("#collection-table-body").on("click", ".deactivate-collection-icon", (e) => {
        let collectionName = e.currentTarget.getAttribute("collection-name");
        $("#modal-collection-name").text(collectionName);
    });

    $("#modal-confirm-deactivation").on("click", () => {
        let collectionName = $("#modal-collection-name").text();
        emptyTable();
        deactivateCollection(collectionName);
    });

});

function handleInstancesRequests(){
    $("#instance-accordion").empty();
    fetchAllInstances()
        .then(setInstanceSelections)
        .then(handleInitialCLIAuth)
        .then(handleStacksRequests);
        //TODO then select/fetch current instance from url param for the dropdown
}

function handleStacksRequests(instanceName) {
    getStacksData(instanceName);
    getCliVersion(instanceName);
}

function getStacksData(instanceName) {
    if (typeof instanceName === "undefined") {
        return;
    }
    return fetch(`/api/auth/kabanero/${instanceName}/stacks/list`)
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
        .then(function(response){
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
        .then(handleInstancesRequests)
        .catch(error => console.error("Error syncing collections", error))
}

function updateCollectionView(collectionJSON) {
    if (typeof collectionJSON === "undefined") {
        return;
    }

    let collections = collectionJSON["kabanero collections"];

    stacks.forEach(coll => {
        $("#stack-table-body").append(createCollRow(coll));
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
        let svg =`<svg focusable="false" preserveAspectRatio="xMidYMid meet" xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 32 32" aria-hidden="true" style="will-change: transform;"><path d="M16,4A12,12,0,1,1,4,16,12,12,0,0,1,16,4m0-2A14,14,0,1,0,30,16,14,14,0,0,0,16,2Z"></path><path d="M10 15H22V17H10z"></path><title>Deactivate ${coll.name} collection</title></svg>`

        div.append(svg);
        deactivateCollection.append(div);

        return deactivateCollection;
    }

    $(".table-loader").hide();
    $("#collection-table").show();
}

function setCLIVersion(cliVersion) {
    if(typeof cliVersion === "undefined"){
        return;
    }
    let version = cliVersion["image"].split(":")[1];
    $("#cli-version").append(version);
}

function emptyTable(){
    $("#collection-table").hide();
    $(".table-loader").show();
    $("#collection-table-body").empty();
    $("#cli-version").empty();
}

function getURLParam(key){
    return new URLSearchParams(window.location.search).get(key);
}

function handleInitialCLIAuth(instanceName){
    return fetch(`/api/auth/kabanero/${instanceName}/stacks/list`)
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

    return fetch(`/api/auth/kabanero/${instanceName}/stacks/login`)
        .catch(error => console.error(`Error logging into instance ${instanceName} via CLI server`, error));
}
