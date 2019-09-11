package io.kabanero.kubernetes;

public class Collection {

    private String name;
    private String version;

    public Collection(String name, String version) {
       this.name = name;
       this.version = version;
    }

    public String getName() {
        return name;
    }

    public String getVersion() {
        return version;
    }

    @Override
    public String toString() {
        return "Collection [name=" + name + ", version=" + version + "]";
    }

}
