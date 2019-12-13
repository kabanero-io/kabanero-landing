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
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.enterprise.context.RequestScoped;
import javax.net.ssl.SSLContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;

import com.ibm.websphere.security.social.UserProfile;
import com.ibm.websphere.security.social.UserProfileManager;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import io.kabanero.instance.KabaneroInstance;
import io.kabanero.instance.KabaneroManager;
import io.website.ResponseMessage;

@ApplicationPath("api")
@Path("/kabanero/{instanceName}/collections")
@RequestScoped
public class CollectionsEndpoints extends Application {
    private final static String JWT_COOKIE_KEY = "kabjwt";
    private final static Logger LOGGER = Logger.getLogger(CollectionsEndpoints.class.getName());
    private String CLI_URL;
    
    @PathParam("instanceName") String INSTANCE_NAME;

    @GET
    @Path("/list")
    @Produces(MediaType.APPLICATION_JSON)
    public Response listCollections() throws ClientProtocolException, IOException {
        return null;
    }

    @GET
    @Path("/login")
    @Produces(MediaType.APPLICATION_JSON)
    public Response login(@Context HttpServletRequest httpServletRequest) throws ClientProtocolException, IOException {
        String user = httpServletRequest.getUserPrincipal().getName();
        UserProfile userProfile = UserProfileManager.getUserProfile();
        String token = userProfile.getAccessToken();

        String jwt = getJWTFromLogin(user, token, INSTANCE_NAME);
        if(jwt != null){
            NewCookie cookie = new NewCookie(JWT_COOKIE_KEY, jwt);
            return Response.ok().cookie(cookie).build();
        }
        return Response.status(500).entity(new ResponseMessage("Failed to login to CLI server of instance: " + INSTANCE_NAME)).build();
    }

    /*
    * Send a request to the CLI server with the already authenticated GitHub user's username and OAuth token
    * to get back a JWT we will use to authenticate with the CLI server
    * 
    * @param    instanceName  the name of the Kabanero instance you wish to login to
    * @return   a JWT associated with the logged in GitHub user
    */
    private String getJWTFromLogin(String user, String token, String instanceName) throws ClientProtocolException, IOException {       
        CloseableHttpClient client = createTrustAllHttpClientBuilder();
        // TODO protect against null client

        String cliServerURL = CLI_URL == null ? getAndSetCliUrl(instanceName) : CLI_URL;

        HttpPost httpPost = new HttpPost(cliServerURL + "/login");
        JSONObject gitCreds = new JSONObject();
        gitCreds.put("gituser", user);
        gitCreds.put("gitpat", token);

        HttpEntity stringEntity = new StringEntity(gitCreds.toString(), ContentType.APPLICATION_JSON);
        httpPost.setEntity(stringEntity);
        httpPost.setHeader("Content-Type", "application/json");
        CloseableHttpResponse response2 = client.execute(httpPost);
        
        try {
            HttpEntity entity2 = response2.getEntity();

            String body = EntityUtils.toString(entity2);
            System.out.println(body);
//[WARNING ] Invalid cookie header: "Set-Cookie: has_recent_activity=1; path=/; expires=Wed, 11 Dec 2019 23:40:40 -0000". Invalid 'expires' attribute: Wed, 11 Dec 2019 23:40:40 -0000
//[ERROR   ] Failed parsing JSON returned from cli server
//A JSONObject text must begin with '{' at 0 [character 1 line 1]

            JSONObject jwtJSON = new JSONObject(body);

            String jwt = jwtJSON.getString("jwt");
            String responseMessage = jwtJSON.getString("message");
            if(jwt == null || responseMessage == null || !"ok".equals(responseMessage)){
                LOGGER.log(Level.SEVERE, "Failed to login with the CLI server: " + responseMessage);
                return null;
            }

            EntityUtils.consume(entity2);
            return jwt;
        } 
        catch (JSONException e){
            LOGGER.log(Level.SEVERE, "Failed parsing JSON returned from cli server", e);
            return null;
        }
        finally {
            response2.close();
        }
    }

    private String getAndSetCliUrl(String instanceName){
        KabaneroInstance wantedInstance = KabaneroManager.getKabaneroManagerInstance().getKabaneroInstance(instanceName);
        CLI_URL = wantedInstance.getDetails().getCliURL() + "/login";
        return CLI_URL;
    }

    private static CloseableHttpClient createTrustAllHttpClientBuilder(){
        SSLContext sslContext;
        try {
            sslContext = SSLContextBuilder.create().loadTrustMaterial(new TrustSelfSignedStrategy()).build();
            
            return HttpClients.custom()
            .setDefaultRequestConfig(RequestConfig.custom().setCookieSpec(CookieSpecs.STANDARD).build())
            .setSSLContext(sslContext)
            .setSSLHostnameVerifier(new NoopHostnameVerifier())
            .build();
        } catch (KeyManagementException | NoSuchAlgorithmException | KeyStoreException e) {
            LOGGER.log(Level.SEVERE, "Failed to create http client", e);
            return null;
        }
      }
    
}
