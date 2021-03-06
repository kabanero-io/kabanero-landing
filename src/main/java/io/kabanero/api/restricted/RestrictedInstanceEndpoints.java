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
package io.kabanero.api.restricted;

import java.io.IOException;
import java.security.GeneralSecurityException;

import javax.enterprise.context.RequestScoped;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import io.kabanero.Admin;
import io.kabanero.v1alpha2.models.Kabanero;
import io.kubernetes.KabaneroClient;
import io.kubernetes.client.ApiException;
import io.website.ResponseMessage;

@ApplicationPath("api")
@Path("/auth/kabanero/{instanceName}")
@RequestScoped
public class RestrictedInstanceEndpoints extends Application{
    @PathParam("instanceName")
    String INSTANCE_NAME;

    @PUT
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateInstance(Kabanero newInstance)
            throws IOException, ApiException, GeneralSecurityException {
        if(!Admin.isAdmin(INSTANCE_NAME)){
            return Response.status(401).entity(new ResponseMessage("User is not authorized to perform update on instance: " + INSTANCE_NAME)).build();
        }
        if(newInstance == null || newInstance.getMetadata() == null){
            return Response.status(500).entity(new ResponseMessage("Kabanero object passed to update endpoint is null")).build();
        }

        KabaneroClient.updateInstance(newInstance);
        return Response.accepted().build();
    }
}