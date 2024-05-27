package com.neopsis.gradle.model;

public class Module {

    String name;
    String version;

    public Module(String mod, String ver) {
        name    = mod;
        version = ver;
    }

    public String getName() {
        return name;
    }

    public String getVersion() {
        return version;
    }

}
