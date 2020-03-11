
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

package io.kabanero.tools;

public class KabaneroTool {
    public String name;
    public String namespace;
    public String route;
    public String location;
    public String path;
    public String description;
    public String actionText;
    public boolean https;
    
    
    public KabaneroTool(String name, String location, String description, String actionText, boolean https){
        this.name = name;
        this.location = location;
        this.description = description;
        this.actionText = actionText;
        this.https = https;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public String getRoute() {
        return route;
    }

    // Some routes have random generated names, we also allow setting the path here
    // so we can find a route by its path
    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setRoute(String route) {
        this.route = route;
    }

    public String getDescription(){
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getActionText(){
        return actionText;
    }

    public void setActionText(String actionText) {
        this.actionText = actionText;
    }

    public boolean isHttps() {
        return https;
    }

    public void setHttps(boolean https) {
        this.https = https;
    }

    @Override
    public String toString() {
        return "KabaneroTool [actionText=" + actionText + ", description=" + description + ", https=" + https
                + ", location=" + location + ", name=" + name + ", namespace=" + namespace + ", route=" + route + "]";
    }
}