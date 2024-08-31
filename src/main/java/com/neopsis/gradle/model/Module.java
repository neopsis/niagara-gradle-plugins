package com.neopsis.gradle.model;

public class Module {

    String name;
    String version;
    Boolean followNiagaraNumbering;

    public Module(String mod, String ver) {
        name    = mod;
        version = ver;
        followNiagaraNumbering = true ;
    }

    public Module(String mod, String ver, Boolean num) {
        name    = mod;
        version = ver;
        followNiagaraNumbering = num ;
    }

    public String getName() {
        return name;
    }

    public String getVersion() {
        return version;
    }

    public Boolean getFollowNiagaraNumbering() {
        return followNiagaraNumbering;
    }
}
