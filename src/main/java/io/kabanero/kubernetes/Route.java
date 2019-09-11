package io.kabanero.kubernetes;

import java.util.List;
import java.util.Map;

public class Route {

    private String name;
    private List<Map<String, ?>> ingress;

    public Route(String name) {
       this.name = name;
    }

    public String getName() {
        return name;
    }

    void setIngress(List<Map<String, ?>> ingress) {
        this.ingress = ingress;
    }

    public String getHost() {
        if (ingress != null && ingress.size() > 0) {
            return (String) ingress.get(0).get("host");
        } else {
            return null;
        }
    }

    @Override
    public String toString() {
        return "Route [name=" + name + ", ingress=" + ingress + "]";
    }

}
