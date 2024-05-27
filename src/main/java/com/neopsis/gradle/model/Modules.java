package com.neopsis.gradle.model;

import java.util.ArrayList;
import java.util.List;

public class Modules {

    List<Module> moduleList = new ArrayList<>();

    public void module(String module, String version) {
        moduleList.add(new Module(module, version));
    }

    public List<Module> getModuleList() {
        return moduleList;
    }
}
