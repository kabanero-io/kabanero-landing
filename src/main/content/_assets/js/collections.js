$(document).ready(function() {
    fetchAllInstances()
        .then(setInstanceSelections)
        .then(handleInitialCLIAuth)
        .then(getCollectionData)
        .then(updateCollectionView);
    //TODO then select/fetch current instance from url param for the dropdown

    $("#instance-accordion li").on("click", e => {
        // prevent carbon from doing its normal thing with the accordion
        e.stopPropagation();
        
        let newName = handleInstanceSelection(e.target);
        fetchAnInstance(newName)
            .then(updateInstanceView);
    });
});

function getCollectionData(instanceName){
    if(typeof instanceName === "undefined"){
        return;
    }

    let collectionJSON = null;
    let cliVersion = null;

    return fetch(`/api/auth/kabanero/${instanceName}/collections/list`)
        .then(function(response) {
            return response.json();
        })
        .then(function(data) {
            collectionJSON = data;
            return fetch(`/api/auth/kabanero/${instanceName}/collections/version`)
            .then(function(response) {
                return response.json();
            })
            .catch(error => console.error("Error getting CLI Version", error));
        })
        .then(function(data) {
            cliVersion = data;
            return $.extend(collectionJSON, cliVersion)
        })
        .catch(error => console.error("Error getting collections", error));
}

function updateCollectionView(collectionJSON){
    if(typeof collectionJSON === "undefined"){
        return;
    }
    
    let collections = collectionJSON["kabanero collections"];
    let cliVersion = collectionJSON["image"].split(":")[1];
    console.log(collections)
    console.log(cliVersion)
  
    collections.forEach(coll => {
        $("#collection-table-body").append(createCollRow(coll));
    });

    // hide loader table and show this one
    $(".table-loader").hide();
    $("#collection-table").show();
    $(".cli-version").append(cliVersion);
    $("#table-header-cli-version").show();

    function createCollRow(coll){
        let row = $("<tr>");
        let name = $("<td>").text(coll.name);
        let version = $("<td>").text(coll.version);
        let status = $("<td>").text(coll.status);
        return row.append([name, version, status]);
    }
}

function getCliVersion(instanceName){
    if(typeof instanceName === "undefined"){
        return;
    }

    return fetch(`/api/auth/kabanero/${instanceName}/collections/version`)
        .then(function(response) {
            return response.json();
        })
        .catch(error => console.error("Error getting CLI Version", error));
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