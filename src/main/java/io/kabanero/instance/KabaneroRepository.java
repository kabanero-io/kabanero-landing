
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
package io.kabanero.instance;

public class KabaneroRepository {
    String name;
    String appsodyURL;
    String codewindURL;
    Boolean activateDefaultCollections;

    public KabaneroRepository(String name, String appsodyURL, Boolean activateDefaultCollections){
        this.name = name;
        this.appsodyURL = appsodyURL;
        this.activateDefaultCollections = activateDefaultCollections;

        /**
         * We need to add the JSON version of the collection hub index file for Codewind (Codewind only supports the JSON version).
         * instead of having the installer include the URL in the CRD, we will just replace the file extention with .json, 
         * since the files live next to each other. We validate this URL before showing it on the client side.
        */
        if(this.appsodyURL != null){
            this.codewindURL = appsodyURL.replace(".yaml", ".json");
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAppsodyURL() {
        return appsodyURL;
    }

    public void setAppsodyURL(String appsodyURL) {
        this.appsodyURL = appsodyURL;
    }

    public String getCodewindURL() {
        return codewindURL;
    }

    public void setCodewindURL(String codewindURL) {
        this.codewindURL = codewindURL;
    }

    public Boolean getActivateDefaultCollections() {
        return activateDefaultCollections;
    }

    public void setActivateDefaultCollections(Boolean activateDefaultCollections) {
        this.activateDefaultCollections = activateDefaultCollections;
    }

    @Override
    public String toString() {
        return "KabaneroRepository [activateDefaultCollections=" + activateDefaultCollections + ", appsodyURL="
                + appsodyURL + ", codewindURL=" + codewindURL + ", name=" + name + "]";
    }
    
}