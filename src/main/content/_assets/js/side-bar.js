// Note: other side-bar functionality is brought in via carbon-components.min.js
$(document).ready(function(){
    handleSideNavScroll();
    setNavLocation();
    loadProductVersion();
});

function handleSideNavScroll() {
    $(window).scroll(function () {
        var $height = $(window).scrollTop();
        setNavLocation($height);
    });
}

function setNavLocation() {
    let height = $(document).scrollTop();

    if (height > 50) {
        $(".bx--side-nav").addClass("scroll");
    }
    else {
        $(".bx--side-nav").removeClass("scroll");
    }
}

function loadProductVersion(){
    fetch("/api/kabanero")
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