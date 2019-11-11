
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

import java.util.List;

public class KabaneroInstanceDetails {

    public String username;
    public String dateCreated;
    public List<KabaneroRepository> repos;
    public String transformationAdvisorURL;
    public String tektonDashboardURL;
    public String clusterName;
    public List<KabaneroCollection> collections;
    public String cliURL;

    public KabaneroInstanceDetails(String username, String date, List<KabaneroRepository> repos, String clusterName, List<KabaneroCollection> collections, String cliURL){
        this.username = username;
        this.dateCreated = date;
        this.repos = repos;
        this.clusterName = clusterName;
        this.collections = collections;
        this.cliURL = cliURL;
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

    public List<KabaneroRepository> getRepos() {
        return this.repos;
    }

    public void setRepos(List<KabaneroRepository> repos) {
        this.repos = repos;
    }

    public String getCluster() {
        return clusterName;
    }

    public void setCluster(String cluster) {
        this.clusterName = cluster;
    }

    public List<KabaneroCollection> getCollections() {
        return collections;
    }

    public void setCollections(List<KabaneroCollection> collections) {
        this.collections = collections;
    }

    public String getClusterName() {
        return clusterName;
    }

    public void setClusterName(String clusterName) {
        this.clusterName = clusterName;
    }

    public String getTransformationAdvisorURL() {
        return transformationAdvisorURL;
    }

    public void setTransformationAdvisorURL(String transformationAdvisorURL) {
        this.transformationAdvisorURL = transformationAdvisorURL;
    }

    public String getTektonDashboardURL() {
        return tektonDashboardURL;
    }

    public void setTektonDashboardURL(String tektonDashboardURL) {
        this.tektonDashboardURL = tektonDashboardURL;
    }

    public String getCliURL() {
        return cliURL;
    }

    public void setCliURL(String cliURL) {
        this.cliURL = cliURL;
    }

    @Override
    public String toString() {
        return "KabaneroInstanceDetails [cliURL=" + cliURL + ", clusterName=" + clusterName + ", collections="
                + collections + ", dateCreated=" + dateCreated + ", repos=" + repos + ", tektonDashboardURL="
                + tektonDashboardURL + ", transformationAdvisorURL=" + transformationAdvisorURL + ", username="
                + username + "]";
    }
}