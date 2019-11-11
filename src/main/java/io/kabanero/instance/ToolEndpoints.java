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

import javax.enterprise.context.RequestScoped;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import io.website.ResponseMessage;

@ApplicationPath("api")
@Path("/tools")
@RequestScoped
public class ToolEndpoints extends Application {

    @GET
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    public Collection<KabaneroTool> getAllTools() {
        return KabaneroToolManager.getKabaneroToolManagerInstance().getAllTools();
    }

    @GET
    @Path("/{toolName}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getATool(@PathParam("toolName") String toolName) {
        KabaneroTool wantedInstance = KabaneroToolManager.getKabaneroToolManagerInstance().getTool(toolName);
        if(wantedInstance == null){
            return Response.status(404).entity(new ResponseMessage(toolName + " not found")).build();
        }
        return Response.ok().entity(wantedInstance).build();
    }
}
