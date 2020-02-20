package io.kubernetes;

import java.util.Map;

public class Route {

    private String name;
    private Map<String, ?> spec;

    public Route(String name) {
       this.name = name;
    }

    public String getName() {
        return name;
    }

    void setSpec(Map<String, ?> spec) {
        this.spec = spec;
    }

    public String getHost() {
        String host = (String) spec.get("host");
        String path = (String) spec.get("path");
        if(path != null){
            return host + path;
        }
        return host;
    }

    @Override
    public String toString() {
        return "Route [name=" + name + ", spec=" + spec + "]";
    }

}
