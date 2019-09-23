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

import javax.enterprise.context.RequestScoped;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Response;

@ApplicationPath("appnav")
@Path("/")
@RequestScoped
public class AppNavEndpoints extends Application {

    @GET
    @Path("openshift/featuredApp.js")
    @Produces({ "application/javascript" })
    public Response featuredApp() {
        return Response.ok().entity(Constants.FEATURED_APP_JS).build();
    }

    @GET
    @Path("openshift/appLauncher.js")
    @Produces({ "application/javascript" })
    public Response appLauncher() {
        return Response.ok().entity(Constants.APP_LAUNCHER_JS).build();
    }

    @GET
    @Path("openshift/projectNavigation.js")
    @Produces({ "application/javascript" })
    public Response projectNavigation() {
        return Response.ok().entity(Constants.PROJECT_NAVIGATION_2_JS).build();
    }

    @GET
    @Path("openshift/appNavIcon.css")
    @Produces({ "text/css" })
    public Response appNavIcon() {
        return Response.ok(Constants.APP_NAV_ICON_CSS).build();
    }

}
