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
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.enterprise.context.RequestScoped;
import javax.net.ssl.SSLContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.CookieParam;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.ibm.websphere.security.social.UserProfile;
import com.ibm.websphere.security.social.UserProfileManager;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.TrustStrategy;
import org.apache.http.util.EntityUtils;

import io.website.ResponseMessage;
import io.kabanero.v1alpha1.models.Kabanero;
import io.kubernetes.client.ApiException;
import io.kubernetes.KabaneroClient;

@ApplicationPath("api")
@Path("/auth/kabanero/{instanceName}/stacks")
@RequestScoped
public class StacksEndpoints extends Application {
    private final static String JWT_COOKIE_KEY = "kabjwt";
    private final static Logger LOGGER = Logger.getLogger(StacksEndpoints.class.getName());
    private String CLI_URL;

    @PathParam("instanceName")
    String INSTANCE_NAME;

    @GET
    @Path("/list")
    @Produces(MediaType.APPLICATION_JSON)
    public Response listStacks(@CookieParam(JWT_COOKIE_KEY) String jwt) throws ClientProtocolException, IOException, ApiException, GeneralSecurityException {
        CloseableHttpClient client = createHttpClient();

        String cliServerURL =  CLI_URL == null ? setCLIURL(INSTANCE_NAME) : CLI_URL;
        HttpGet httpGet = new HttpGet(cliServerURL + "/v1/collections");
        httpGet.setHeader(HttpHeaders.AUTHORIZATION, jwt);
        CloseableHttpResponse response = client.execute(httpGet);
        try {
            // Check if CLI server returns a bad code (like 401) which will tell our
            // frontend to trigger a login
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != 200) {
                LOGGER.log(Level.WARNING, "non 200 status code returned from cli server: " + statusCode);
                return Response.status(statusCode).build();
            }

            HttpEntity entity2 = response.getEntity();
            String body = EntityUtils.toString(entity2);

            EntityUtils.consume(entity2);
            return Response.ok().entity(body).build();
        } catch (JsonParseException e) {
            LOGGER.log(Level.SEVERE, "Failed parsing Collections JSON returned from cli server", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        } finally {
            response.close();
        }
    }

    @GET
    @Path("/version")
    @Produces(MediaType.APPLICATION_JSON)
    public Response cliVersion(@CookieParam(JWT_COOKIE_KEY) String jwt) throws ClientProtocolException, IOException, ApiException, GeneralSecurityException {
        CloseableHttpClient client = createHttpClient();

        String cliServerURL = CLI_URL == null ? setCLIURL(INSTANCE_NAME) : CLI_URL;

        HttpGet httpGet = new HttpGet(cliServerURL + "/v1/image");
        httpGet.setHeader(HttpHeaders.AUTHORIZATION, jwt);
        CloseableHttpResponse response = client.execute(httpGet);

        try {

            // Check if CLI server returns a bad code (like 401) which will tell our
            // frontend to trigger a login
            int statusCode = response.getStatusLine().getStatusCode();

            if (statusCode != 200) {
                LOGGER.log(Level.WARNING, "Error getting Kabanero CLI version: " + statusCode);
                return Response.status(statusCode).build();
            }

            HttpEntity entity2 = response.getEntity();
            String body = EntityUtils.toString(entity2);

            EntityUtils.consume(entity2);
            return Response.ok().entity(body).build();
        } finally {
            response.close();
        }
    }

  

    @GET
    @Path("/deactivate/{collectionName}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deactivateCollection(@CookieParam(JWT_COOKIE_KEY) String jwt, @PathParam("collectionName") final String collectionName) throws ClientProtocolException, IOException, ApiException, GeneralSecurityException {
        CloseableHttpClient client = createHttpClient();

        String cliServerURL = CLI_URL == null ? setCLIURL(INSTANCE_NAME) : CLI_URL;

        HttpDelete httpDelete = new HttpDelete(cliServerURL + "/v1/collections/" + collectionName);
        httpDelete.setHeader(HttpHeaders.AUTHORIZATION, jwt);

        CloseableHttpResponse response = client.execute(httpDelete);

        try {
            // Check if CLI server returns a bad code (like 401) which will tell our
            // frontend to trigger a login
            int statusCode = response.getStatusLine().getStatusCode();

            if (statusCode != 200) {
                LOGGER.log(Level.WARNING, "non 200 status code returned from cli server: " + statusCode);
                return Response.status(statusCode).build();
            }

            HttpEntity entity2 = response.getEntity();
            String body = EntityUtils.toString(entity2);

            EntityUtils.consume(entity2);
            return Response.ok(body).build();
        } finally {
            response.close();
        }
    }

    @GET
    @Path("/sync")
    @Produces(MediaType.APPLICATION_JSON)
    public Response syncCollections(@CookieParam(JWT_COOKIE_KEY) String jwt) throws ClientProtocolException, IOException, ApiException, GeneralSecurityException {
        CloseableHttpClient client = createHttpClient();

        String cliServerURL = CLI_URL == null ? setCLIURL(INSTANCE_NAME) : CLI_URL;

        HttpPut httpPut = new HttpPut(cliServerURL + "/v1/collections");
        httpPut.setHeader(HttpHeaders.AUTHORIZATION, jwt);

        CloseableHttpResponse response = client.execute(httpPut);

        try {
            // Check if CLI server returns a bad code (like 401) which will tell our
            // frontend to trigger a login
            int statusCode = response.getStatusLine().getStatusCode();

            if (statusCode != 200) {
                LOGGER.log(Level.WARNING, "non 200 status code returned from cli server: " + statusCode);
                return Response.status(statusCode).build();
            }

            HttpEntity entity2 = response.getEntity();
            String body = EntityUtils.toString(entity2);

            EntityUtils.consume(entity2);
            return Response.ok(body).build();
        } finally {
            response.close();
        }
    }

    @GET
    @Path("/login")
    @Produces(MediaType.APPLICATION_JSON)
    public Response login(@Context HttpServletRequest httpServletRequest) throws ClientProtocolException, IOException, ApiException, GeneralSecurityException {
        String user = httpServletRequest.getUserPrincipal().getName();
        UserProfile userProfile = UserProfileManager.getUserProfile();
        String token = userProfile.getAccessToken();

        String jwt = getJWTFromLogin(user, token, INSTANCE_NAME);
        if (jwt != null) {
            NewCookie cookie = new NewCookie(JWT_COOKIE_KEY, jwt);
            return Response.ok().cookie(cookie).build();
        }
        return Response.status(500)
                .entity(new ResponseMessage("Failed to login to CLI server of instance: " + INSTANCE_NAME)).build();
    }

    /*
     * Send a request to the CLI server with the already authenticated GitHub user's
     * username and OAuth token to get back a JWT we will use to authenticate with
     * the CLI server
     * 
     * @param instanceName the name of the Kabanero instance you wish to login to
     * 
     * @return a JWT associated with the logged in GitHub user
     */
    private String getJWTFromLogin(String user, String token, String instanceName)
            throws ClientProtocolException, IOException, ApiException, GeneralSecurityException {
        CloseableHttpClient client = createHttpClient();
        // TODO protect against null client
        String cliServerURL = CLI_URL == null ? setCLIURL(INSTANCE_NAME) : CLI_URL;
        HttpPost httpPost = new HttpPost(cliServerURL + "/login");

        JsonObject gitCreds = new JsonObject();
        gitCreds.addProperty("gituser", user);
        gitCreds.addProperty("gitpat", token);

        HttpEntity stringEntity = new StringEntity(gitCreds.toString(), ContentType.APPLICATION_JSON);
        httpPost.setEntity(stringEntity);
        httpPost.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON);
        CloseableHttpResponse response2 = client.execute(httpPost);

        try {
            HttpEntity entity2 = response2.getEntity();
            String body = EntityUtils.toString(entity2);

            Map<?, ?> jwtProperties = new Gson().fromJson(body, Map.class);

            String jwt = (String) jwtProperties.get("jwt");
            String responseMessage = (String) jwtProperties.get("message");

            if (jwt == null || responseMessage == null || !"ok".equals(responseMessage)) {
                LOGGER.log(Level.SEVERE, "Failed to login with the CLI server: " + responseMessage);
                return null;
            }

            EntityUtils.consume(entity2);
            return jwt;
        } catch (JsonParseException e) {
            LOGGER.log(Level.SEVERE, "Failed parsing JSON returned from cli server", e);
            return null;
        } finally {
            response2.close();
        }
    }

    private String setCLIURL(String instanceName) throws IOException, ApiException, GeneralSecurityException {
        Kabanero wantedInstance = KabaneroClient.getAnInstance(instanceName);
        CLI_URL = wantedInstance.getStatus().getCli().getHostnames().get(0);
        return "https://" + CLI_URL;
    }

    private static CloseableHttpClient createHttpClient(){
        try {
            TrustStrategy trustAllStrategy = new TrustStrategy(){
				@Override
				public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
					return true;
				}
            };

            SSLContext sslContext = SSLContextBuilder.create().loadTrustMaterial(trustAllStrategy).build();
            
            return HttpClients.custom()
            .setSSLContext(sslContext)
            .setSSLHostnameVerifier(new NoopHostnameVerifier())
            .build();
        }
        catch (KeyManagementException | NoSuchAlgorithmException | KeyStoreException e) {
            LOGGER.log(Level.SEVERE, "Failed to create http client", e);
            return null;
        }
      }
}
