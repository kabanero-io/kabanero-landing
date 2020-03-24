package io.kabanero.api;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.egit.github.core.client.GitHubClient;

import io.kabanero.v1alpha2.client.apis.KabaneroApi;
import io.kabanero.v1alpha2.models.Kabanero;
import io.kubernetes.KabaneroClient;
import io.kubernetes.client.ApiClient;
import io.kubernetes.client.ApiException;

public class GitHubClientInitilizer {
    private final static String DEFAULT_NAMESPACE = "kabanero";

    public static GitHubClient getClient(String instanceName) throws IOException, GeneralSecurityException, ApiException {
        String apiUrl = getApiURL(instanceName);
        apiUrl = apiUrl.replace("https://", "");
        return new GitHubClient(apiUrl);
    }

    private static String getApiURL(String instanceName) throws IOException, GeneralSecurityException, ApiException {
        ApiClient client = KabaneroClient.getApiClient();
        KabaneroApi kabApi = new KabaneroApi(client);
        Kabanero kabaneroInstance = kabApi.getKabanero(DEFAULT_NAMESPACE, instanceName);
        return kabaneroInstance.getSpec().getGithub().getApiUrl();
    }
}