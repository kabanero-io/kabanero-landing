
$(document).ready(function () {
    handleSideNavScroll();
    setNavLocation($(document).scrollTop());
    toggleSideNavExpand();
});

function handleSideNavScroll() {
    $(window).scroll(function () {
        var $height = $(window).scrollTop();
        setNavLocation($height)
    });
}

function setNavLocation(height) {
    if (height > 50) {
        //console.log(height)
        $('.bx--side-nav').addClass('scroll');
    }
    else {
        $('.bx--side-nav').removeClass('scroll');
    }
}

function toggleSideNavExpand() {
    $('#side-bar-nav-expand-icon').click(function () {
        $('.bx--side-nav').toggleClass('bx--side-nav--expanded');
    });
}