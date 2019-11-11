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

import javax.enterprise.context.RequestScoped;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;

import io.kabanero.KabaneroInstallation;

/*
    Endpoints related to the Kabanero installation as a whole
*/
@ApplicationPath("api")
@Path("/kabanero")
@RequestScoped
public class KabaneroEndpoints extends Application {

    @GET
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    public KabaneroInstallation getInstallDetails() {
        return new KabaneroInstallation();
    }
}
