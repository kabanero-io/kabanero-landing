package io.kabanero.instance;

public class KabaneroStack {

    private String name;
    private String version;

    public KabaneroStack(String name, String version) {
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
        return "Stack [name=" + name + ", version=" + version + "]";
    }

}
