
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
    public String tektonDashboardURL;
    public String clusterName;
    public List<KabaneroStack> stacks;
    public String cliURL;

    public KabaneroInstanceDetails(String username, String date, List<KabaneroRepository> repos, String clusterName, List<KabaneroStack> stacks, String cliURL){
        this.username = username;
        this.dateCreated = date;
        this.repos = repos;
        this.clusterName = clusterName;
        this.stacks = stacks;
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

    public List<KabaneroStack> getStacks() {
        return stacks;
    }

    public void setStacks(List<KabaneroStack> stacks) {
        this.stacks = stacks;
    }

    public String getClusterName() {
        return clusterName;
    }

    public void setClusterName(String clusterName) {
        this.clusterName = clusterName;
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
        return "KabaneroInstanceDetails [cliURL=" + cliURL + ", clusterName=" + clusterName + ", stacks="
                + stacks + ", dateCreated=" + dateCreated + ", repos=" + repos + ", tektonDashboardURL="
                + tektonDashboardURL + ", username=" + username + "]";
    }
}