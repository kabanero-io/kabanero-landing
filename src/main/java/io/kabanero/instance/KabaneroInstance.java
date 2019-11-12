
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

public class KabaneroInstance {
    public String instanceName;
    public KabaneroInstanceDetails details;
    
    public KabaneroInstance(String username, String instanceName, String date, List<KabaneroRepository> repos, String clusterName, List<KabaneroCollection> collections, String cliURL){
        this.instanceName = instanceName;
        this.details = new KabaneroInstanceDetails(username, date, repos, clusterName, collections, cliURL);
    }

    public KabaneroInstanceDetails getDetails() {
        return details;
    }

    public void setDetails(KabaneroInstanceDetails details) {
        this.details = details;
    }

    public String getInstanceName() {
        return instanceName;
    }

    public void setInstanceName(String instanceName) {
        this.instanceName = instanceName;
    }

    @Override
    public String toString() {
        return "KabaneroInstance [details=" + details + ", instanceName=" + instanceName + "]";
    }
}