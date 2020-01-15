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

package io.kabanero.api;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collection;
import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.CookieParam;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.http.client.ClientProtocolException;

import io.website.ResponseMessage;
import io.kabanero.v1alpha1.models.CollectionList;
import io.kabanero.v1alpha1.models.Kabanero;
import io.kabanero.v1alpha1.models.KabaneroList;
import io.kubernetes.client.ApiException;
import io.kubernetes.KabaneroClient;

@ApplicationPath("api")
@Path("/kabanero")
@RequestScoped
public class InstanceEndpoints extends Application {

    @GET
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    public KabaneroList getAllInstances() throws IOException, ApiException, GeneralSecurityException {
        KabaneroList kabaneros = KabaneroClient.getInstances();
        List<Kabanero> kabaneroList = kabaneros.getItems();
        if (kabaneroList.size() == 0){
            return null;
        }
        return kabaneros;
    }

    @GET
    @Path("/{instanceName}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAInstance(@PathParam("instanceName") String instanceName)
            throws IOException, ApiException, GeneralSecurityException {
        Kabanero wantedInstance = KabaneroClient.getAnInstance(instanceName);
        if (wantedInstance == null) {
            return Response.status(404).entity(new ResponseMessage(instanceName + " not found")).build();
        }
        
        return Response.ok().entity(wantedInstance).build();
    }

    @GET
    @Path("/{instanceName}/collections")
    @Produces(MediaType.APPLICATION_JSON)
    public CollectionList listCollectionsKube(@PathParam("instanceName") String instanceName) throws ClientProtocolException, IOException, ApiException, GeneralSecurityException {
        CollectionList collections = KabaneroClient.getCollections(instanceName);
        return collections;
    }


}
