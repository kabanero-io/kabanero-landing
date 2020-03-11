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

    public String getPath() {
        return (String) spec.get("path");
    }

    public String getHost() {
        return (String) spec.get("host");
    }

    public String getHostPath() {
        String host = getHost();
        String path = getPath();
        return path != null ? host + path : host;
    }

    @Override
    public String toString() {
        return "Route [name=" + name + ", spec=" + spec + "]";
    }

}
