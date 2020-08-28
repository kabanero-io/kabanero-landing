
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

package io.website;

public final class Constants {

    // Kabanero
    public static final String PRODUCT_VERSION = getEnv("PRODUCT_VERSION", "");

    // Kabanero Instance
    public static final String DEFAULT_USER_NAME = getEnv("USER_NAME", "");
    public static final String DEFAULT_INSTANCE_NAME = getEnv("INSTANCE_NAME", "kabanero");
    public static final String DEFAULT_DATE_CREATED = getEnv("DATE_CREATED", "");
    public static final String DEFAULT_CLUSTER_NAME = getEnv("CLUSTER_NAME", "");
    public static final String CLI_URL = getEnv("CLI_URL", "");

    //OAuth
    public static final String USER_API =  getEnv("USER_API", null);
    public static final String AUTHORIZATION_ENDPOINT =  getEnv("AUTHORIZATION_ENDPOINT", null);
    public static final String TOKEN_ENDPOINT =  getEnv("TOKEN_ENDPOINT", null);
    public static final String WEBSITE = getEnv("WEBSITE", null);

    // Kabanero Tools

    public static final String KAPPNAV_LABEL = "Application Navigator";
    public static final String KAPPNAV_URL = getEnv("KAPPNAV_URL", "");
    public static final String KAPPNAV_DESCRIPTION = "Manage your applications using Application Navigator";
    public static final String KAPPNAV_ACTION_TEXT = "Manage Applications";

    public static final String CODEREADY_LABEL = "Red Hat CodeReady Workspaces";
    public static final String CODEREADY_URL = getEnv("CODEREADY_URL", "");
    public static final String CODEREADY_DESCRIPTION = "Red Hat CodeReady Workspaces provides a consistent, secure, and zero-configuration development environment.";
    public static final String CODEREADY_ACTION_TEXT = "Go to CodeReady";


    // OKD
    public static final String LANDING_URL = getEnv("LANDING_URL", "https://kabanero.io");

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
                "title: 'Kabanero',",
                "icon: 'icon-kabanero-feature',",
                "url: '" + Constants.LANDING_URL + "',",
                "description: 'Get started with Kabanero.'",
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
                "title: 'Kabanero',",
                "iconClass: 'icon-kabanero-launcher',",
                "href: '" + Constants.LANDING_URL + "',",
                "tooltip: 'Get started with Kabanero.'",
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
            "background-image: url(" + Constants.LANDING_URL + "/img/gray-kabanero-logo.png);",
            "margin-right: 13px;",
            "width: 18px;",
            "background-size: contain;",
        "}",
        ".icon-kabanero-feature {",
            "display: block;",
            "background-repeat: no-repeat;",
            "background-image: url(" + Constants.LANDING_URL + "/img/Kabanero_logo_white.png);",
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
                "href: '" + Constants.LANDING_URL + "',",
            "});",
        "}());"
    );

    private static String getEnv(String envKey, String fallback){
        String envValue = System.getenv(envKey);
        return envValue == null ? fallback : envValue;
    }
}