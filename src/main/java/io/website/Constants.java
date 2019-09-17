
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

import java.util.HashMap;
import java.util.Map;

import io.kabanero.KabaneroCollection;

public final class Constants {

    // ICPA Instance
    public static final String DEFAULT_USER_NAME = getEnv("USER_NAME", "");
    public static final String DEFAULT_INSTANCE_NAME = getEnv("INSTANCE_NAME", "kabanero");
    public static final String DEFAULT_DATE_CREATED = getEnv("DATE_CREATED", "");
    public static final String DEFAULT_COLLECTION_HUB_URL = getEnv("COLLECTION_HUB_URL", "");
    public static final String DEFAULT_CLUSTER_NAME = getEnv("CLUSTER_NAME", "");

    // Collections env value should be a comma separated list of "collection_name=version" enabled for this instance. For example "nodejs=v1.0.0,java-microprofile=v1.0.1"
    public static final Map<String, KabaneroCollection> DEFAULT_COLLECTIONS = collectionStringToCollections(getEnv("COLLECTIONS", ""));

    // Kabanero Tools
    public static final String TA_DASHBOARD_LABEL = "Transformation Advisor";
    public static final String TA_DASHBOARD_URL = getEnv("TA_DASHBOARD_URL", "");

    public static final String TEKTON_DASHBOARD_LABEL = "Tekton";
    public static final String TEKTON_DASHBOARD_URL = getEnv("TEKTON_DASHBOARD_URL", "");


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
                "title: \"Cloud Pak for Apps\",",
                "icon: \"icon-icpa-feature\",",
                "url: \"" + Constants.LANDING_URL + "\",",
                "description: \"Get started with IBM Cloud Pak for Applications.\"",
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
                "title: 'Kabanero Enterprise',",
                "iconClass: \"icon-icpa-launcher\",",
                "href: \"" + Constants.LANDING_URL + "\",",
                "tooltip: \"Get started with IBM Kabanero Enterprise.\"",
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
        ".icon-icpa-launcher {",
            "background-repeat: no-repeat;",
            "background-image: url(" + Constants.LANDING_URL + "/img/favicon/favicon-16x16.png);",
            "height: 16px;",
            "width: 16px;",
        "}",
        ".icon-icpa-navigation {",
            "background-repeat: no-repeat;",
            "background-image: url(" + Constants.LANDING_URL + "/img/gray-kabanero-logo.png);",
            "margin-right: 13px;",
            "width: 18px;",
            "background-size: contain;",
        "}",
        ".icon-icpa-feature {",
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
                "label: 'Kabanero Enterprise',",
                "iconClass: 'icon-icpa-navigation',",
                "href: \"" + Constants.LANDING_URL + "\",",
            "});",
        "}());"
    );

    private static String getEnv(String envKey, String fallback){
        String envValue = System.getenv(envKey);
        return envValue == null ? fallback : envValue;
    }

    private static Map<String, KabaneroCollection> collectionStringToCollections(String collections){
        Map<String, KabaneroCollection> activeCollections = new HashMap<String, KabaneroCollection>();
        if (collections.trim().isEmpty()) {
            return activeCollections;
        }
        String[] collectionsArray = collections.split(",");

        for(String collectionGroup:collectionsArray){
            String[] collectionNameVersion = collectionGroup.split("=");

            if(collectionNameVersion.length != 2){
                throw new IllegalArgumentException("Invalid Collections String");
            }

            KabaneroCollection KabaneroCollection = new KabaneroCollection(collectionNameVersion[0], collectionNameVersion[1]);
            activeCollections.put(KabaneroCollection.getName(), KabaneroCollection);
        }

        return activeCollections;
    }
}