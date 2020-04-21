package io.kabanero;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

import javax.ws.rs.core.Response;

import com.google.gson.JsonObject;
import com.ibm.websphere.security.social.UserProfile;
import com.ibm.websphere.security.social.UserProfileManager;

import org.eclipse.egit.github.core.Team;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.service.TeamService;
import org.eclipse.egit.github.core.service.UserService;

import io.kabanero.api.GitHubClientInitilizer;
import io.kabanero.v1alpha2.models.Kabanero;
import io.kubernetes.KabaneroClient;
import io.kubernetes.client.ApiException;

public class Admin {

    public static boolean isAdmin(String instanceName) throws IOException, GeneralSecurityException, ApiException {
        UserProfile userProfile = UserProfileManager.getUserProfile();
        
        if(userProfile == null){
            JsonObject body = new JsonObject();
            body.addProperty("isAdmin", false);
            return false;
        }

        String token = userProfile.getAccessToken();
        GitHubClient client = GitHubClientInitilizer.getClient(instanceName);
        client.setOAuth2Token(token);

        Kabanero instance = KabaneroClient.getAnInstance(instanceName);
        if (instance == null) {
            return false;
        }

        String instanceGithubOrg = instance.getSpec().getGithub().getOrganization();
        List<String> instanceGithubTeams = instance.getSpec().getGithub().getTeams();

        Boolean isAdmin = false;

        TeamService teamService = new TeamService(client);
        List<Team> teams = teamService.getTeams(instanceGithubOrg);

        for (Team orgTeam : teams) {
            for (String kabaneroAdminTeam : instanceGithubTeams) {
                if (kabaneroAdminTeam.equals(orgTeam.getName()) && !isAdmin) {
                    isAdmin = teamService.isMember(orgTeam.getId(), new UserService(client).getUser().getLogin());
                }
            }
        }

        return isAdmin;
    }
}