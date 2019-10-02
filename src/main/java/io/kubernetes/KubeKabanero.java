package io.kubernetes;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.kabanero.KabaneroRepository;

// class to organize data returned from the Kubernetes API
public class KubeKabanero {

    private String name;
    private List<KabaneroRepository> repositories;
    private String creationTimestamp;
    
    public KubeKabanero(String name, String creationTimestamp) {
        this.name = name;
        this.creationTimestamp = creationTimestamp;
        this.repositories = new ArrayList<>();
    }

    public void setRepositories(List<Map<String, ?>> repositories) {        
        for(Map<String, ?> repo : repositories){
            String name = (String) repo.get("name");
            String appsodyURL = (String) repo.get("url");
            Boolean activateDefaultCollections = (Boolean) repo.get("activateDefaultCollections");
            
            KabaneroRepository KabRepo = new KabaneroRepository(name, appsodyURL, activateDefaultCollections);
            this.repositories.add(KabRepo);
        }
    }

    public String getName() {
        return name;
    }

    public List<KabaneroRepository> getRepositories() {
        return repositories;
    }

    public String getCreationTimestamp() {
        return creationTimestamp;
    }

    @Override
    public String toString() {
        return "KubeKabanero [name=" + name + ", repositories=" + repositories + ", creationTimestamp=" + creationTimestamp + "]";
    }

}
