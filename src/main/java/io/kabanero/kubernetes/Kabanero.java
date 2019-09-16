package io.kabanero.kubernetes;

import java.util.List;
import java.util.Map;

public class Kabanero {

    private String name;
    private List<Map<String, ?>> repositories;
    private String creationTimestamp;
    
    public Kabanero(String name, String creationTimestamp) {
        this.name = name;
        this.creationTimestamp = creationTimestamp;
    }

    void setRepositories(List<Map<String, ?>> repositories) {
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
        return "Kabanero [name=" + name + ", repositories=" + repositories + ", creationTimestamp=" + creationTimestamp
                + "]";
    }

}
