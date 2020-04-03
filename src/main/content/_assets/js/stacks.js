const CLI_LOGIN_RETRY_COUNT = 3;
// 3 possible values for "digest check" field returned from CLI API
const DIGEST_MATCHED = "matched";
const DIGEST_MISMATCHED = "mismatched";
const DIGEST_UNKNOWN = "unknown";

$(document).ready(function () {
    let url = new URL(location.href);
    let instanceName = url.searchParams.get("name");

    setAllInstances(instanceName);
    fetchAnInstance(instanceName)
        .then(loadAllInfo);

    $("#sync-stacks-icon").on("click", (e) => {
        if (e.target.getAttribute("class") == "icon-active") {
            let instanceName = getActiveInstanceName();
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

    // removes digest notification rows
    $("#stack-table-body").on("click", ".digest-notification-x", e => {
        $(e.currentTarget).closest("tr").remove();
    });
    
});

function loadAllInfo(instanceJSON){
    if (typeof instanceJSON === "undefined") {
        console.log("instance data is undefined, cannot load instance");
        return;
    }

    displayDigest(instanceJSON);
    handleInitialCLIAuth(instanceJSON)
        .then(handleStacksRequests);
}

function handleStacksRequests(instanceJSON) {
    if(!instanceJSON.metadata || !instanceJSON.metadata.name){
        return;
    }

    getStacksData(instanceJSON);
    getCliVersion(instanceJSON);
}

function getStacksData(instanceJSON) {
    let instanceName = instanceJSON.metadata.name;

    return fetch(`/api/auth/kabanero/${instanceName}/stacks`)
        .then(function (response) {
            return response.json();
        })
        .then((stackJSON)=> updateStackView(instanceJSON, stackJSON))
        .catch(error => console.error("Error getting stacks", error));
}

function getCliVersion(instanceJSON) {
    let instanceName = instanceJSON.metadata.name;

    return fetch(`/api/auth/kabanero/${instanceName}/stacks/version`)
        .then(function (response) {
            return response.json();
        })
        .then(setCLIVersion)
        .catch(error => console.error("Error getting CLI Version", error));
}

function deactivateStack(name, version) {
    let instanceName = getActiveInstanceName();

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

function updateStackView(instanceJSON, stackJSON) {
    if (typeof instanceJSON === "undefined" || typeof stackJSON === "undefined") {
        return;
    }

    // Stack governance policy will help us know whether to display a digest error/warning/none when the digest mismatches on the stack
    let policy = instanceJSON.spec.governancePolicy.stackPolicy;

    // yaml metadata in kube, cannot be deactivated and has no status.
    let curatedStacks = stackJSON["curated stacks"];

    // difference between kabanero stacks and curated stacks. 
    // If there are stacks in this array then a "sync" needs to happen to pull them into kabanero
    let newCuratedStacks = stackJSON["new curated stacks"];

    let kabaneroStacks = stackJSON["kabanero stacks"];

    // when a stack yaml is deleted from the cluster, but it’s still out on Kabanero. A sync will clean these up.
    let obsoleteStacks = stackJSON["obsolete stacks"];

    kabaneroStacks.forEach(stack => {
        $("#stack-table-body").append(createKabaneroStackRow(stack));
    });

    curatedStacks.forEach(stack => {
        $("#curated-stack-table-body").append(createCuratedStackRow(stack));
    });

    function createKabaneroStackRow(stack) {
        let rows = [];
        let statusItems = stack.status;

        // status items are considered as different versions for the same stack
        statusItems.forEach(statusItem => {
            let name = $("<td>").text(stack.name);
            let versionTD = $("<td>").text(statusItem.version);
            let statusTD = $("<td>").text(statusItem.status);
            let deactivateStack = createDeactivateStackButton(stack.name, statusItem.status, statusItem.version);
            let row = $("<tr>").append([name, versionTD, statusTD, deactivateStack]);
            rows.push(row);

            // The error (one long colspan td) will appear under the row it 
            // references so that is why we append this new row after the row with all the info above
            let digestError = statusItem["digest check"] && (statusItem["digest check"] !== DIGEST_MATCHED) ? getDigestError(stack.name, statusItem) : "";
            rows.push(digestError);
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

    /*
        We only will create an error td on strictDigest if the digests mismatch. activeDigest is a possible problem, so we create a warning.
        For the other 2 policies there is nothing to do, but there are there for reference.
    */
    function getDigestError(stackName, statusItem){
        let numberOfStackTableHeaders = $("#stack-table th").length - 1;
        let digestCheckValue = statusItem["digest check"];
        let currentDigest = statusItem["image digest"];
        let kabaneroDigest = statusItem["kabanero digest"];
        let stackNameVer = `${stackName} - ${statusItem.version}`;
        let errorSVG = createSVG("error--glyph", "margin-right-icon digest-error-icon", 20, 20, "");
        let warnSVG = createSVG("warning--glyph", "margin-right-icon digest-warn-icon", 20, 20, "");
        let moreInfoLink = " <a target='_blank' href='/docs/ref/general/configuration/stack-governance.html'>More info</a>";

        // handle the error case where the CLI could not get the digest value
        if(digestCheckValue === DIGEST_UNKNOWN){
            console.log("CLI returned unknown for digest check", statusItem);
            let unknownMsg = "Digest Error: An error occurred when evaluating the digest match. The digest values are unknown.";
            return generateRowForDigestNotification("digest-error", unknownMsg, errorSVG);
        }

        switch (policy) {
        case "strictDigest":
            console.log(`strictDigest policy error: ${stackNameVer} current digest: ${currentDigest} does not match ${stackNameVer} Kabanero digest: ${kabaneroDigest}`);
            let strictMsg = `Digest Error: strictDigest policy enforces a strict digest match. The current ${stackNameVer} digest does not match the Kabanero ${stackNameVer} digest at time of activation.`;
            return generateRowForDigestNotification("digest-error", strictMsg, errorSVG);
        
        case "activeDigest":
            console.log(`activeDigest policy warning: ${stackNameVer} current digest: ${currentDigest} does not match ${stackNameVer} Kabanero digest: ${kabaneroDigest}`);
            let activeMsg = `Digest Warning: activeDigest policy enforces a Major.Minor semver digest match. The current ${stackNameVer} digest does not match the Kabanero ${stackNameVer} digest at time of activation, this may be a problem.`;
            return generateRowForDigestNotification("digest-error", activeMsg, warnSVG);


        case "ignoreDigest":
            console.log(`ignoreDigest policy info: the current ${stackNameVer} digest: ${currentDigest} does not match the ${stackNameVer} Kabanero digest: ${kabaneroDigest}`);
            return "";

        case "none":
            console.log(`noneDigest policy info: the current ${stackNameVer} digest: ${currentDigest} does not match the ${stackNameVer} Kabanero digest: ${kabaneroDigest}`);
            return "";
            
        default:
            console.error(`Invalid policy: ${policy}`);
        }

        function generateRowForDigestNotification(classNames, msg, svg){
            let row = $("<tr>").addClass("digest-notification");

            return row.append(
                $("<td>")
                    .addClass(classNames)
                    .attr("colspan", numberOfStackTableHeaders)
                    .text(msg)
                    .prepend(svg).append(moreInfoLink), 
                $("<td>").addClass("digest-notification-x").text("X"));
        }

    }

    function createDeactivateStackButton(stackName, status, version) {
        let iconStatus = status === "active" ? "icon-active" : "icon-disabled";
        let deactivateStack = $("<td>").addClass("deactivate-stack-td");

        let div = $("<div>").addClass(`deactivate-stack-icon ${iconStatus}`)
            .data("stackname", stackName)
            .data("stackversion", version)
            .attr("data-modal-target", `#deactivate-stack-modal-${iconStatus}`);

        let svg = `<img title="Deactivate ${stackName} - ${version}" src="/img/deactivateStack.svg"/>`;

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

function handleInitialCLIAuth(instanceJSON, retries) {
    let instanceName = instanceJSON.metadata.name;

    retries = typeof retries === "undefined" ? 0 : retries;
    // We use the stacks endpoint to check if a user is logged in on initial page load, if we get a 401 we'll login and retry this route
    // If we get back a 200 we consider ourselves successfully logged in
    return fetch(`/api/auth/kabanero/${instanceName}/stacks`)
        .then(function (response) {
            // Login via cli and retry if 401 is returned on initial call
            if (retries <= CLI_LOGIN_RETRY_COUNT && response.status === 401) {
                return loginViaCLI(instanceName)
                    .then(() => {
                        return handleInitialCLIAuth(instanceJSON, ++retries);
                    });
            }
            else if (retries >= CLI_LOGIN_RETRY_COUNT){
                console.log("exceeded max retries to login to CLI");
                return;
            }
            else if (response.status !== 200) {
                console.warn(`Initial auth into instance ${instanceName} returned status code: ${response.status}`);
            }

            // pass on instanceJSON var to the next function in the promise chain
            return instanceJSON;
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

function displayDigest(instance){
    if(!instance.spec || !instance.spec.governancePolicy){
        console.log("Failed to get stack govern policy. instance.spec or instance.spec.governancePoliy does not exist.");
        return;
    }
    // The way carbon dropdown works is different than normal select. 
    // This gets the current li that the server says is the current digest, and sets the display to that text.
    // Then it adds the selected class since it doesn't make sense to select the same li that is already the current digest.
    let policy = instance.spec.governancePolicy.stackPolicy;

    $("#stack-govern-dropdown li").show();
    let $currentPolicyLi = $(`#stack-govern-dropdown li[data-value='${policy}']`);
    let translatedPolicyText = $currentPolicyLi.find("a").first().text();
    $("#stack-govern-value").attr("data-value", policy);
    $("#stack-govern-value-text").text(translatedPolicyText);
    $currentPolicyLi.addClass("bx--dropdown--selected");
}
