package io.kubernetes;

import io.website.Constants;
import io.kabanero.KabaneroCollection;
import io.kabanero.KabaneroInstance;
import io.kabanero.KabaneroTool;
import io.kabanero.KabaneroToolManager;
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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import com.squareup.okhttp.ConnectionSpec;


public class KabaneroClient {
   
    
  public static void main(String[] args) throws IOException, ApiException, GeneralSecurityException {
    ApiClient client = KabaneroClient.getApiClient();

    String namespace = "kabanero";

    Map<String, Route> routes = KabaneroClient.listRoutes(client, namespace);
    System.out.println(routes);
    
    String tektonDashboard = KabaneroClient.getTektonDashboardURL(routes);
    System.out.println(tektonDashboard);

    Map<String, KabaneroCollection> collections = KabaneroClient.listKabaneroCollections(client, namespace);
    System.out.println(collections);
    
    Map<String, KubeKabanero> instances = KabaneroClient.listKabaneroInstances(client, namespace);
    System.out.println(instances);    
  }
  
  // routes from kabanero namespace
  private static String getTektonDashboardURL(Map<String, Route> routes) {
      Route route = routes.get("tekton-dashboard");
      if (route == null) {
          return null;
      }
      return "https://" + route.getHost();
  }

  // routes from ta namespace
  private static String getTransformationAdvisorURL(Map<String, Route> routes) {
      for (Route route: routes.values()) {
          if (route.getName().endsWith("ta-rh-ui-route")) {
              return "https://" + route.getHost();
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
  
  public static KabaneroInstance getInstance() throws IOException, ApiException, GeneralSecurityException {
      ApiClient client = KabaneroClient.getApiClient();
      
      String namespace = "kabanero";
      
      Map<String, KubeKabanero> instances = KabaneroClient.listKabaneroInstances(client, namespace);
      System.out.println(instances);
      if (instances.size() == 0) {
          return null;
      }
      
      // pick first instance - could be multiple
      KubeKabanero instance = instances.values().iterator().next();

      String username = null;
      String instanceName = instance.getName();
      String date = instance.getCreationTimestamp();
      
      String collectionHub = null;
      
      List<Map<String, ?>> repositories = instance.getRepositories();

      if (repositories != null && repositories.size() > 0) {
          // get first repository - could be muliple
          Map<String, ?> repository = repositories.get(0);
          collectionHub = (String) repository.get("url");
      }
      
      
      Map<String, KabaneroCollection> activeCollections = KabaneroClient.listKabaneroCollections(client, namespace);      
      
      String clusterName = null;
      
      KabaneroInstance Instance = new KabaneroInstance(username, instanceName, date, collectionHub, clusterName, activeCollections);
      return Instance;
  }

  public static void discoverTools(KabaneroToolManager tools) throws IOException, ApiException, GeneralSecurityException {
      ApiClient client = KabaneroClient.getApiClient();

      Map<String, Route> routes = null;
      
      routes = KabaneroClient.listRoutes(client, "kabanero");
      if (routes != null) {
          String url = KabaneroClient.getTektonDashboardURL(routes);
          tools.addTool(new KabaneroTool(Constants.TEKTON_DASHBOARD_LABEL, url));
      }
      
      routes = KabaneroClient.listRoutes(client, "ta");
      if (routes != null) {
          String url = KabaneroClient.getTransformationAdvisorURL(routes);
          tools.addTool(new KabaneroTool(Constants.TA_DASHBOARD_LABEL, url));
      }
  }

  private static Map<String, KabaneroCollection> listKabaneroCollections(ApiClient apiClient, String namespace) throws ApiException {
      CustomObjectsApi customApi = new CustomObjectsApi(apiClient);
      String group = "kabanero.io";
      String version = "v1alpha1";      
      String plural = "collections";

      Map<String, KabaneroCollection> instances = new HashMap<String, KabaneroCollection>();
      
      Object obj = customApi.listNamespacedCustomObject(group, version, namespace, plural, "true", "", "", 60, false);
      Map<String, ?> map = (Map<String, ?>) obj;
      List<Map<String, ?>> items = (List<Map<String, ?>>)map.get("items");
      for (Map<String ,?> item: items) {                  
          Map<String ,?> spec = (Map<String ,?>) item.get("spec");
          String collectionName = (String) spec.get("name");
          String collectionVersion = (String) spec.get("version");
          
          KabaneroCollection KabaneroCollection = new KabaneroCollection(collectionName, collectionVersion);
          instances.put(collectionName, KabaneroCollection);
      }
      
      return instances;
  }
  
  private static Map<String, KubeKabanero> listKabaneroInstances(ApiClient apiClient, String namespace) throws ApiException {
    CustomObjectsApi customApi = new CustomObjectsApi(apiClient);
    String group = "kabanero.io";
    String version = "v1alpha1";
    String plural = "kabaneros";

    Map<String, KubeKabanero> instances = new HashMap<String, KubeKabanero>();
    
    Object obj = customApi.listNamespacedCustomObject(group, version, namespace, plural, "true", "", "", 60, false);
    Map<String, ?> map = (Map<String, ?>) obj;
    List<Map<String, ?>> items = (List<Map<String, ?>>)map.get("items");
    for (Map<String ,?> item: items) {
        Map<String ,?> metadata = (Map<String ,?>) item.get("metadata");
        String name = (String) metadata.get("name");
        String creationTime = (String) metadata.get("creationTimestamp");
        
        KubeKabanero instance = new KubeKabanero(name, creationTime);
        instances.put(name, instance);
        
        Map<String ,?> spec = (Map<String ,?>) item.get("spec");
        if (spec != null) {
            Map<String ,?> collections = (Map<String ,?>) spec.get("collections");
            if (collections != null) {        
                List<Map<String, ?>> repositories = (List<Map<String, ?>>)collections.get("repositories");       
                instance.setRepositories(repositories);
            }
        }
    }
    
    return instances;
  }
  
  private static Map<String, Route> listRoutes(ApiClient apiClient, String namespace) throws ApiException {
      CustomObjectsApi customApi = new CustomObjectsApi(apiClient);
      String group = "route.openshift.io";
      String version = "v1";      
      String plural = "routes";

      Map<String, Route> instances = new HashMap<String, Route>();
      
      Object obj = customApi.listNamespacedCustomObject(group, version, namespace, plural, "true", "", "", 60, false);
      Map<String, ?> map = (Map<String, ?>) obj;
      List<Map<String, ?>> items = (List<Map<String, ?>>)map.get("items");
      for (Map<String ,?> item: items) {            
          Map<String ,?> metadata = (Map<String ,?>) item.get("metadata");
          String name = (String) metadata.get("name");
                    
          Route route = new Route(name);
          instances.put(name, route);
          
          Map<String ,?> spec = (Map<String ,?>) item.get("status");   
          if (spec != null) {
              List<Map<String ,?>> ingress = (List<Map<String ,?>>) spec.get("ingress");          
              route.setIngress(ingress);
          }
      }
      
      return instances;
  }
  
  private static TrustManager[] getTrustManager() {
      TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
          @Override
          public X509Certificate[] getAcceptedIssuers() {
              return null;
          }
          public void checkClientTrusted(X509Certificate[] certs, String authType) {}
          public void checkServerTrusted(X509Certificate[] certs, String authType) {}
      } };
      return trustAllCerts;
  }
}
