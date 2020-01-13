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

import io.kabanero.instance.KabaneroTool;
import io.kabanero.instance.KabaneroToolManager;
import io.kubernetes.client.ApiClient;
import io.kubernetes.client.ApiException;
import io.kubernetes.client.Configuration;
import io.kubernetes.client.apis.CustomObjectsApi;
import io.kubernetes.client.util.ClientBuilder;
import io.kubernetes.client.util.KubeConfig;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.json.JSONArray;
import org.json.JSONObject;

import com.squareup.okhttp.ConnectionSpec;

import org.apache.commons.io.IOUtils;

import io.kabanero.v1alpha1.client.apis.KabaneroApi;
import io.kabanero.v1alpha1.client.apis.CollectionApi;
import io.kabanero.v1alpha1.models.Collection;
import io.kabanero.v1alpha1.models.CollectionList;
import io.kabanero.v1alpha1.models.CollectionStatus;
import io.kabanero.v1alpha1.models.Kabanero;
import io.kabanero.v1alpha1.models.KabaneroList;
import io.kabanero.v1alpha1.models.KabaneroSpecCollections;
import io.kabanero.v1alpha1.models.KabaneroSpecCollectionsRepositories;

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

    public static KabaneroList getInstances() throws IOException, ApiException, GeneralSecurityException {
        ApiClient client = KabaneroClient.getApiClient();
        String namespace = "kabanero";
        try{
            KabaneroApi api = new KabaneroApi(client);
            KabaneroList kabaneros = api.listKabaneros(namespace, null, null, null);
            List<Kabanero> kabaneroList = kabaneros.getItems();

			if (kabaneroList.size() > 0) {
                System.out.println("!!!Got instances data from api!!!");
                System.out.println(kabaneros.toString());
                return kabaneros;
            }
        }catch (Exception e) {
			e.printStackTrace();
		}
        return null;
    }

    public static Kabanero getAnInstance(String instanceName) throws IOException, ApiException, GeneralSecurityException {
        ApiClient client = KabaneroClient.getApiClient();
        String namespace = "kabanero";
        try{
            KabaneroApi api = new KabaneroApi(client);
            return api.getKabanero(namespace, instanceName);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public static CollectionList getCollections() throws IOException, GeneralSecurityException {
        System.out.println("!!!!Called getCollections!!!!!!");
        ApiClient client = KabaneroClient.getApiClient();

        String namespace = "kabanero";
        try{
            CollectionApi api = new CollectionApi(client);
            CollectionList collections = api.listCollections(namespace, null, null, null);
            List<Collection> collectionList = collections.getItems();
            if(collectionList.size() > 0){
                System.out.println("!!!Got collections data from api!!!");
                System.out.println(collections.toString());
                return collections;
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public static Kabanero createNewKabanero(Kabanero newInstance) throws IOException, ApiException, GeneralSecurityException{
        ApiClient client = KabaneroClient.getApiClient();

        String namespace = "kabanero";
        try{
            KabaneroApi api = new KabaneroApi(client);
            return api.createKabanero(namespace, newInstance);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;

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

        InputStream inputStream = KabaneroClient.class.getClassLoader().getResourceAsStream("tools.json");

        try {
            JSONArray toolsList = new JSONArray(IOUtils.toString(inputStream, StandardCharsets.UTF_8));

            Map<String, Route> routes = null;

            Iterator<Object> iterator = toolsList.iterator();
            while (iterator.hasNext()) {
                JSONObject tool = (JSONObject) iterator.next();
                
                String toolName = tool.get("toolName").toString();
                String namespace = tool.get("namespace").toString();
                String route = tool.get("route").toString();

                routes = KabaneroClient.listRoutes(client, namespace);

                if (routes != null) {
                    String url = KabaneroClient.getLabeledRoute(route, routes); 
                    tools.addTool(new KabaneroTool(toolName, url));
                }
            }
        } finally {
            inputStream.close();
        }
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
