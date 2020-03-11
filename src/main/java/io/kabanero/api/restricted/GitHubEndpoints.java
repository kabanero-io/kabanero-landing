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
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.enterprise.context.RequestScoped;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.ibm.websphere.security.social.UserProfile;
import com.ibm.websphere.security.social.UserProfileManager;

import org.eclipse.egit.github.core.User;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.service.TeamService;
import org.eclipse.egit.github.core.service.UserService;

import io.kubernetes.client.ApiException;
import io.website.ResponseMessage;

@ApplicationPath("api")
@Path("/auth/git")
@RequestScoped
public class GitHubEndpoints extends Application {
    private final static Logger LOGGER = Logger.getLogger(StacksEndpoints.class.getName());

    @GET
    @Path("/user")
    @Produces(MediaType.APPLICATION_JSON)
    public User getUserInfo() throws IOException {
        UserProfile userProfile = UserProfileManager.getUserProfile();
        String token = userProfile.getAccessToken();
        GitHubClient client = new GitHubClient();
        client.setOAuth2Token(token);
        return new UserService(client).getUser();
    }

    @GET
    @Path("/user/{github_username}")
    @Produces(MediaType.APPLICATION_JSON)
    public User getUserDetails(@PathParam("instanceName") String instanceName, @PathParam("github_username") String githubUsername) throws IOException, ApiException, GeneralSecurityException {
        UserProfile userProfile = UserProfileManager.getUserProfile();
        String token = userProfile.getAccessToken();
        GitHubClient client = new GitHubClient();
        client.setOAuth2Token(token);
        return new UserService(client).getUser(githubUsername);
    }

    @POST
    @Path("/team/{teamId}/member/{github_username}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response addTeamMember(@PathParam("teamId") int teamId, @PathParam("github_username") String githubUsername) throws IOException, ApiException, GeneralSecurityException {
        try{
            UserProfile userProfile = UserProfileManager.getUserProfile();
            String token = userProfile.getAccessToken();
            GitHubClient client = new GitHubClient();
            client.setOAuth2Token(token);
            TeamService teamService = new TeamService(client);      
            teamService.addMember(teamId, githubUsername);
            //teamService.addMember is a void method so we don't know if the user was added to the team so return 202 instead of 200
            return Response.status(202).build();
        }catch(Exception e){
            LOGGER.log(Level.SEVERE, "A problem occured attempting to add " + githubUsername + " using " + " team ID " + teamId, e);
            return Response.status(500).entity(new ResponseMessage(e.getMessage())).build();
        }
    }

    @DELETE
    @Path("/team/{teamId}/member/{github_username}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response removeTeamMember(@PathParam("teamId") int teamId, @PathParam("github_username") String githubUsername) throws IOException, ApiException, GeneralSecurityException {
        try{
            UserProfile userProfile = UserProfileManager.getUserProfile();
            String token = userProfile.getAccessToken();
            GitHubClient client = new GitHubClient();
            client.setOAuth2Token(token);
            TeamService teamService = new TeamService(client);      
            teamService.removeMember(teamId, githubUsername);
            //teamService.removeMember is a void method so we don't know if the user was removed from the team so return 202 instead of 200
            return Response.status(202).build();
        }catch(Exception e){
            LOGGER.log(Level.SEVERE, "A problem occured attempting to delete " + githubUsername + " using " + " team ID " + teamId, e);
            return Response.status(500).entity(new ResponseMessage(e.getMessage())).build();
        }
    }


}
