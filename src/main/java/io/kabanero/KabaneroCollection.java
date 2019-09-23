package io.kabanero;

public class KabaneroCollection {

    private String name;
    private String version;

    public KabaneroCollection(String name, String version) {
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
