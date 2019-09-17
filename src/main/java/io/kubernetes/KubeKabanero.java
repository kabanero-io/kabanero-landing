package io.kubernetes;

import java.util.List;
import java.util.Map;

// class to organize data returned from the Kubernetes API
public class KubeKabanero {

    private String name;
    private List<Map<String, ?>> repositories;
    private String creationTimestamp;
    
    public KubeKabanero(String name, String creationTimestamp) {
        this.name = name;
        this.creationTimestamp = creationTimestamp;
    }

    public void setRepositories(List<Map<String, ?>> repositories) {
        this.repositories = repositories;
    }

    public String getName() {
        return name;
    }

    public List<Map<String, ?>> getRepositories() {
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
