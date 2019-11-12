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
package io.kubernetes;

import io.website.Constants;
import io.kabanero.instance.KabaneroCollection;
import io.kabanero.instance.KabaneroInstance;
import io.kabanero.instance.KabaneroRepository;
import io.kabanero.instance.KabaneroTool;
import io.kabanero.instance.KabaneroToolManager;
import io.kubernetes.KubeKabanero;
import io.kubernetes.client.ApiClient;
import io.kubernetes.client.ApiException;
import io.kubernetes.client.Configuration;
import io.kubernetes.client.apis.CustomObjectsApi;
import io.kubernetes.client.util.ClientBuilder;
import io.kubernetes.client.util.KubeConfig;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import com.squareup.okhttp.ConnectionSpec;

public class KabaneroClient {
    private final static Logger LOGGER = Logger.getLogger(KabaneroClient.class.getName());

    // routes from kabanero namespace
    private static String getLabeledRoute(String Label, Map<String, Route> routes) {
        Route route = routes.get(Label);
        if (route == null) {
            return null;
        }
        return "https://" + route.getURL();
    }

    // routes from ta namespace
    private static String getTransformationAdvisorURL(Map<String, Route> routes) {
        for (Route route : routes.values()) {
            if (route.getName().endsWith("ta-rh-ui-route")) {
                return "https://" + route.getURL();
            }
        }
        return null;
    }

    private static ApiClient getApiClient() throws IOException, GeneralSecurityException {
        ApiClient client = null;
        String value = System.getenv("KUBERNETES_SERVICE_HOST");
        if (value != null) {
            // running in cluster
            client = ClientBuilder.cluster().build();
        } else {
            // running outside of cluster
            File kubeConfig = new File(System.getProperty("user.home"), ".kube/config");
            client = ClientBuilder.kubeconfig(KubeConfig.loadKubeConfig(new FileReader(kubeConfig))).build();
        }

        SSLContext sc = SSLContext.getInstance("TLSv1.2");

        // use the same key manager as kube client
        sc.init(client.getKeyManagers(), KabaneroClient.getTrustManager(), new SecureRandom());

        client.getHttpClient().setSslSocketFactory(sc.getSocketFactory());

        ConnectionSpec spec = new ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS).allEnabledCipherSuites().build();
        client.getHttpClient().setConnectionSpecs(Collections.singletonList((spec)));

        Configuration.setDefaultApiClient(client);
        return client;
    }

    public static List<KabaneroInstance> getInstances() throws IOException, ApiException, GeneralSecurityException {
        ApiClient client = KabaneroClient.getApiClient();

        String namespace = "kabanero";

        List<KubeKabanero> instances = KabaneroClient.listKabaneroInstances(client, namespace);
        LOGGER.log(Level.FINE, "Found {0} Kabanero Instances", instances.size());

        List<KabaneroInstance> kabaneroInstances = new ArrayList<>();

        for (KubeKabanero instance : instances) {

            String username = null;
            String instanceName = instance.getName();
            String date = instance.getCreationTimestamp();
            String cliURL = KabaneroClient.getCLI(client, namespace);

            List<KabaneroRepository> kabaneroRepositories = instance.getRepositories();
            List<KabaneroCollection> kabaneroCollections = KabaneroClient.listKabaneroCollections(client, namespace);

            String clusterName = null;

            KabaneroInstance kabInst = new KabaneroInstance(username, instanceName, date, kabaneroRepositories, clusterName, kabaneroCollections, cliURL);
            LOGGER.log(Level.FINE, "Kabanero Instance: {0}: {1}", new Object[]{ kabInst.getInstanceName(), kabInst});

            kabaneroInstances.add(kabInst);
        }
        return kabaneroInstances;
    }

    public static String getCLI(ApiClient client, String namespace) throws ApiException {
        Map<String, Route> routes = KabaneroClient.listRoutes(client, namespace);
        if (routes != null) {
            return KabaneroClient.getLabeledRoute("kabanero-cli", routes);
        }
        return null;
    }

    public static void discoverTools(KabaneroToolManager tools) throws IOException, ApiException, GeneralSecurityException {
        ApiClient client = KabaneroClient.getApiClient();

        Map<String, Route> routes = null;

        routes = KabaneroClient.listRoutes(client, "tekton-pipelines");
        if (routes != null) {
            String url = KabaneroClient.getLabeledRoute("tekton-dashboard", routes);
            tools.addTool(new KabaneroTool(Constants.TEKTON_DASHBOARD_LABEL, url));
        }

        routes = KabaneroClient.listRoutes(client, "ta");
        if (routes != null) {
            String url = KabaneroClient.getTransformationAdvisorURL(routes);
            tools.addTool(new KabaneroTool(Constants.TA_DASHBOARD_LABEL, url));
        }

        routes = KabaneroClient.listRoutes(client, "kappnav");
        if (routes != null) {
            String url = KabaneroClient.getLabeledRoute("kappnav-ui-service", routes);
            tools.addTool(new KabaneroTool(Constants.KAPPNAV_LABEL, url));
        }
    }

    private static List<KabaneroCollection> listKabaneroCollections(ApiClient apiClient, String namespace) throws ApiException {
        CustomObjectsApi customApi = new CustomObjectsApi(apiClient);
        String group = "kabanero.io";
        String version = "v1alpha1";
        String plural = "collections";

        List<KabaneroCollection> collections = new ArrayList<KabaneroCollection>();

        Object obj = customApi.listNamespacedCustomObject(group, version, namespace, plural, "true", "", "", 60, false);
        Map<String, ?> map = (Map<String, ?>) obj;
        List<Map<String, ?>> items = (List<Map<String, ?>>) map.get("items");

        for (Map<String, ?> item : items) {
            Map<String, ?> spec = (Map<String, ?>) item.get("spec");
            String collectionName = (String) spec.get("name");
            String collectionVersion = (String) spec.get("version");

            KabaneroCollection kabaneroCollection = new KabaneroCollection(collectionName, collectionVersion);
            collections.add(kabaneroCollection);
        }
        return collections;
    }

    private static List<KubeKabanero> listKabaneroInstances(ApiClient apiClient, String namespace) throws ApiException {
        CustomObjectsApi customApi = new CustomObjectsApi(apiClient);
        String group = "kabanero.io";
        String version = "v1alpha1";
        String plural = "kabaneros";

        List<KubeKabanero> instances = new ArrayList<KubeKabanero>();

        Object obj = customApi.listNamespacedCustomObject(group, version, namespace, plural, "true", "", "", 60, false);
        Map<String, ?> map = (Map<String, ?>) obj;
        List<Map<String, ?>> items = (List<Map<String, ?>>) map.get("items");
        for (Map<String, ?> item : items) {
            Map<String, ?> metadata = (Map<String, ?>) item.get("metadata");
            String name = (String) metadata.get("name");
            String creationTime = (String) metadata.get("creationTimestamp");

            KubeKabanero instance = new KubeKabanero(name, creationTime);

            Map<String, ?> spec = (Map<String, ?>) item.get("spec");
            if (spec != null) {
                Map<String, ?> collections = (Map<String, ?>) spec.get("collections");
                if (collections != null) {
                    List<Map<String, ?>> repositories = (List<Map<String, ?>>) collections.get("repositories");
                    instance.setRepositories(repositories);
                }
            }
            instances.add(instance);
        }
        return instances;
    }

    private static Map<String, Route> listRoutes(ApiClient apiClient, String namespace) throws ApiException {
        CustomObjectsApi customApi = new CustomObjectsApi(apiClient);
        String group = "route.openshift.io";
        String version = "v1";
        String plural = "routes";

        Map<String, Route> routes = new HashMap<String, Route>();

        Object obj = customApi.listNamespacedCustomObject(group, version, namespace, plural, "true", "", "", 60, false);
        Map<String, ?> map = (Map<String, ?>) obj;
        List<Map<String, ?>> items = (List<Map<String, ?>>) map.get("items");
        for (Map<String, ?> item : items) {
            Map<String, ?> metadata = (Map<String, ?>) item.get("metadata");
            String name = (String) metadata.get("name");

            Route route = new Route(name);
            routes.put(name, route);

            Map<String, ?> spec = (Map<String, ?>) item.get("spec");
            route.setSpec(spec);
        }

        LOGGER.log(Level.FINE, namespace + " namespace has {0} routes: {1}", new Object[]{routes.size(), routes});
        return routes;
    }

    private static TrustManager[] getTrustManager() {
        TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }

            public void checkClientTrusted(X509Certificate[] certs, String authType) {
            }

            public void checkServerTrusted(X509Certificate[] certs, String authType) {
            }
        } };
        return trustAllCerts;
    }
}
