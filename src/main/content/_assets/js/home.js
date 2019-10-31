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
$(document).ready(function() {
    fetchATool("Application Navigator")
        .then(setKAppNav);
    showLess();
    checkLocalStoragePageSettigns();
});

function setKAppNav(kAppNav){
    if(kAppNav && kAppNav.location){
        $("#manage-apps-link").attr("href", kAppNav.location).removeClass("hidden");
    }
    else{
        console.log(`kAppNav is not installed: ${JSON.stringify(kAppNav)}`);
    }
}

function hideAllFeaturedModelsCollapses(element){
    $(".featuredModelsCollapse").collapse("hide");
    $(".featured-model-box").removeClass("selectedStack");
    $(element).collapse("show");
    $(element).addClass("selectedStack");
}

function hideAllOpenSourcePlatformsCollapses(id){
    $(".open-source-platform-content-box").hide();
    $("#open-source-platform-" + id + "-collapse").show();
    $(".open-source-platform-box-selected").removeClass("open-source-platform-box-selected");
    $("#open-source-platform-" + id + "-box").addClass("open-source-platform-box-selected");
    $(".open-source-platform-vertical-separator-visible").removeClass("open-source-platform-vertical-separator-visible");
    $("#open-source-platform-" + id + "-separator").addClass("open-source-platform-vertical-separator-visible");
}

function checkLocalStoragePageSettigns(){
    if(localStorage.getItem('shortenedVersion') === 'true')
    {   
        console.log(localStorage.getItem('shortenedVersion'))
        $('.collapsible-row').addClass('show-less');
        $('#carrot-icon img').removeClass().attr('class', 'chevron-down');
    }
}

function showLess() {
    $('#carrot-icon img').click(function () {
        $('.collapsible-row').toggleClass('show-less');
        $(this).toggleClass('chevron-up chevron-down');
        if($('.collapsible-row').hasClass('show-less')){
            localStorage.setItem('shortenedVersion', true);
        }
        else{
            localStorage.setItem('shortenedVersion', false);
        }
    })
}