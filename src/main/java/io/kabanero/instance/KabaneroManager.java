
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

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import io.website.Constants;
import io.kubernetes.KabaneroClient;

// Singleton class to manage the various kabanero instances associated with Kabanero
public class KabaneroManager {
    private final static Logger LOGGER = Logger.getLogger(KabaneroManager.class.getName());

    private static KabaneroManager SINGLE_KABANERO_MANAGER_INSTANCE;
    private HashMap<String, KabaneroInstance> KABANERO_INSTANCES = new HashMap<String, KabaneroInstance>();
    private long created = System.currentTimeMillis();

    private KabaneroManager() {
    }

    private boolean isOld() {
        return (System.currentTimeMillis() - SINGLE_KABANERO_MANAGER_INSTANCE.created > 1000 * 60 * 5);
    }
    
    public static synchronized KabaneroManager getKabaneroManagerInstance() {
        // quick hack: isOld will force refresh every so often - we should be watching for changes instead
        if(SINGLE_KABANERO_MANAGER_INSTANCE == null || SINGLE_KABANERO_MANAGER_INSTANCE.isOld()) {
            SINGLE_KABANERO_MANAGER_INSTANCE = new KabaneroManager();

            try {
                List<KabaneroInstance> instances = KabaneroClient.getInstances();
                
                for(KabaneroInstance kabInst : instances){
                    SINGLE_KABANERO_MANAGER_INSTANCE.addInstance(kabInst);
                }
            } catch (Exception e) {
                LOGGER.log(Level.WARNING, "Exception while getting Kabanero instances", e);
                SINGLE_KABANERO_MANAGER_INSTANCE.addInstance(KabaneroManager.createDefaultInstance());
            }
        }

        return SINGLE_KABANERO_MANAGER_INSTANCE;
    }

    public void addInstance(KabaneroInstance newInstance) {
        LOGGER.log(Level.FINE, "Adding new instance to manage: {0}", newInstance.getInstanceName());
        KABANERO_INSTANCES.put(newInstance.getInstanceName(), newInstance);
    }
        
    public static KabaneroInstance createDefaultInstance() {
        KabaneroInstance instance = new KabaneroInstance(Constants.DEFAULT_USER_NAME, Constants.DEFAULT_INSTANCE_NAME, Constants.DEFAULT_DATE_CREATED, 
        Constants.DEFAULT_COLLECTION_HUB_URL, Constants.DEFAULT_CLUSTER_NAME, Constants.DEFAULT_COLLECTIONS, Constants.CLI_URL);
        return instance;
    }
    
    public KabaneroInstance getKabaneroInstance(String wantedName){
        LOGGER.log(Level.FINE, "Looking to get instance: {0}", wantedName);

        for (String instanceName : KABANERO_INSTANCES.keySet()){
            if(instanceName.equals(wantedName)){
                LOGGER.log(Level.FINE, "Found instance: {0}", wantedName);
                return KABANERO_INSTANCES.get(instanceName);
            }
        }
        
        LOGGER.log(Level.FINE, "Instance: {0} was not found in the managed Kabanero instances list.", wantedName);
        return null;
    }

    public Collection<KabaneroInstance> getAllKabaneroInstances(){
        return KABANERO_INSTANCES.values();
    }
    
}