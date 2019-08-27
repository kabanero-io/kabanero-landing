
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

package io.kabanero.website;

public final class Constants {

    // OKD
    public static final String LANDING_URL = System.getenv("LANDING_URL") == null ? "https://kabanero.io" : System.getenv("LANDING_URL");
    public static final String TEKTON_DASHBOARD_URL = System.getenv("TEKTON_DASHBOARD_URL") == null ? "https://kabanero.io" : System.getenv("TEKTON_DASHBOARD_URL");

    /*
        title: The text label
        icon: The icon you want to appear
        url: where the go when this item is clicked
        description: Short description
    */
    public static final String FEATURED_APP_JS = String.join(
        "",
        "(function() {",
            "window.OPENSHIFT_CONSTANTS.SAAS_OFFERINGS = [{",
                "title: \"Kabanero\",",
                "icon: \"icon-kabanero-feature\",",
                "url: \"" + Constants.LANDING_URL + "\",",
                "description: \"Get started with Kabanero.\"",
            "}];",
        "}());"
    );

    /*
        title: The text label
        iconClass: The icon you want to appear
        href: where the go when this item is clicked
        tooltip: Optional tooltip to display on hover
    */
    public static final String APP_LAUNCHER_JS = String.join(
        "",
        "(function() {",
            "window.OPENSHIFT_CONSTANTS.APP_LAUNCHER_NAVIGATION = [{",
                "title: \"Kabanero\",",
                "iconClass: \"icon-kabanero-launcher\",",
                "href: \"" + Constants.LANDING_URL + "\",",
                "tooltip: \"Get started with Kabanero.\"",
            "}];",
        "}());"
    );

    /*
        title: The text label
        iconClass: The icon you want to appear
        href: where the go when this item is clicked
        tooltip: Optional tooltip to display on hover
    */
    public static final String APP_NAV_ICON_CSS = String.join(
        "",
        ".icon-kabanero-launcher {",
            "background-repeat: no-repeat;",
            "background-image: url(" + Constants.LANDING_URL + "/img/favicon/favicon-16x16.png);",
            "height: 16px;",
            "width: 16px;",
        "}",
        ".icon-kabanero-navigation {",
            "background-repeat: no-repeat;",
            "background-image: url(" + Constants.LANDING_URL + "/img/favicon/favicon-32x32.png);",
            "height: 32px;",
            "width: 32px;",
        "}",
        ".icon-kabanero-feature {",
            "display: block;",
            "background-repeat: no-repeat;",
            "background-image: url(" + Constants.LANDING_URL + "/img/Kabanero_Logo_Colored.png);",
            "height: 90px;",
            "width: 60px;",
        "}"
    );

    public static final String PROJECT_NAVIGATION_2_JS = String.join(
        "",
        "(function() {",
            "window.OPENSHIFT_CONSTANTS.PROJECT_NAVIGATION.push({",
                "label: 'Kabanero',",
                "iconClass: 'icon-kabanero-navigation',",
                "href: \"" + Constants.LANDING_URL + "\",",
            "});",
        "}());"
    );
}