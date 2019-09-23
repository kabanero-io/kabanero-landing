
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

package io.kabanero;

import java.util.Map;

import io.kabanero.KabaneroCollection;

public class KabaneroInstanceDetails {

    public String username;
    public String dateCreated;
    public String collectionHubURL;
    public String transformationAdvisorURL;
    public String tektonDashboardURL;
    public String clusterName;
    public Map<String, KabaneroCollection> collections;

    public KabaneroInstanceDetails(String username, String date, String collectionHub,String clusterName, Map<String, KabaneroCollection> collections){
        this.username = username;
        this.dateCreated = date;
        this.collectionHubURL = collectionHub;
        this.clusterName = clusterName;
        this.collections = collections;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }

    public String getCollectionHubURL() {
        return collectionHubURL;
    }

    public void setCollectionHubURL(String collectionHubURL) {
        this.collectionHubURL = collectionHubURL;
    }

    public String getCluster() {
        return clusterName;
    }

    public void setCluster(String cluster) {
        this.clusterName = cluster;
    }

    public Map<String, KabaneroCollection> getCollections() {
        return collections;
    }

    public void setCollections(Map<String, KabaneroCollection> collections) {
        this.collections = collections;
    }

    public String getClusterName() {
        return clusterName;
    }

    public void setClusterName(String clusterName) {
        this.clusterName = clusterName;
    }
}