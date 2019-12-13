// Note: other side-bar functionality is brought in via carbon-components.min.js
$(document).ready(function(){
    handleSideNav();
    loadProductVersion();
});

function handleSideNav() {

    // If the side nav is expanded, any click not on the expanded side bar 
    // would close the side bar for useability improvements
    $(document).on("click", function (e) {
        let $sideNav = $("aside.bx--side-nav");
        
        if ($sideNav.hasClass("bx--side-nav--expanded") && $(e.target).parents("aside.bx--side-nav").length === 0) {
            $sideNav.removeClass("bx--side-nav--expanded");
        }
    });
}

function loadProductVersion(){
    fetch("/api/install/version")
        .then(function(response) {
            return response.json();
        })
        .then(function(installInfo){
            if(installInfo && installInfo.version !== ""){
                $("#footer-version").text(installInfo.version);
            }
        })
        .catch(error => console.error("Error getting install info", error));
}