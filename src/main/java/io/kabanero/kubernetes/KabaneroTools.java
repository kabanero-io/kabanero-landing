
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

package io.kabanero.kubernetes;

import java.util.Collection;
import java.util.HashMap;

import io.icpa.website.Constants;
import io.kabanero.kubernetes.KabaneroClient;

public class KabaneroTools {

    private static KabaneroTools SINGLE_INSTANCE;
    private HashMap<String, KabaneroTool> KABANERO_TOOLS = new HashMap<String, KabaneroTool>();
    private long created = System.currentTimeMillis();

    private KabaneroTools() {
    }

    private boolean isOld() {
        return (System.currentTimeMillis() - SINGLE_INSTANCE.created > 1000 * 60 * 5);
    }
    
    public static synchronized KabaneroTools getInstance() {
        // quick hack: isOld will force refresh every so often - we should be watching for changes instead
        if(SINGLE_INSTANCE == null || SINGLE_INSTANCE.isOld()) {
            SINGLE_INSTANCE = new KabaneroTools();
            
            try {
                KabaneroClient.discoverTools(SINGLE_INSTANCE);        
            } catch (Exception e) {
                e.printStackTrace();
                KabaneroTools.addDefaultTools(SINGLE_INSTANCE);
            }
        }

        return SINGLE_INSTANCE;
    }

    public void addTool(KabaneroTool tool) {
        KABANERO_TOOLS.put(tool.getLabel(), tool);
    }
        
    private static void addDefaultTools(KabaneroTools tools) {
        KabaneroTool transformationAdvisor = new KabaneroTool(Constants.TA_DASHBOARD_LABEL, Constants.TA_DASHBOARD_URL);
        tools.addTool(transformationAdvisor);
        
        KabaneroTool tekton = new KabaneroTool(Constants.TEKTON_DASHBOARD_LABEL, Constants.TEKTON_DASHBOARD_URL);
        tools.addTool(tekton);
    }
    
    public KabaneroTool getTool(String wantedName){
        for (String toolName : KABANERO_TOOLS.keySet()){
            if(toolName.equals(wantedName)){
                return KABANERO_TOOLS.get(toolName);
            }
        }
        return null;
    }

    public Collection<KabaneroTool> getAllTools(){
        return KABANERO_TOOLS.values();
    }
    
}