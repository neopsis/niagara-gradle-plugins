package com.neopsis.gradle.plugins;

import org.gradle.api.Plugin;
import org.gradle.api.Project;

public class NeopsisNiagaraModule implements Plugin<Project> {

    @Override
    public void apply(Project project) {
        applyPlugins(project);
    }


    /**
     * Apply all module plugins
     */
    private void applyPlugins(Project project) {

        // The Niagara Module plugin configures the "moduleManifest" extension and the
        // "jar" and "moduleTestJar" tasks.
        project.getPluginManager().apply("com.tridium.niagara-module");

        // The signing plugin configures the correct signing of modules. It requires
        // that the plugin also be applied to the root project.
        project.getPluginManager().apply("com.tridium.niagara-signing");

        // The bajadoc plugin configures the generation of Bajadoc for a module.
        project.getPluginManager().apply("com.tridium.bajadoc");

        // The Annotation processors plugin adds default dependencies on ":nre"
        // for the "annotationProcessor" and "moduleTestAnnotationProcessor"
        // configurations by creating a single "niagaraAnnotationProcessor"
        // configuration they extend from. This value can be overridden by explicitly
        // declaring a dependency for the "niagaraAnnotationProcessor" configuration.
        project.getPluginManager().apply("com.tridium.niagara-jacoco");

        // The niagara_home repositories convention plugin configures !bin/ext and
        // !modules as flat-file Maven repositories so that projects in this build can
        // depend on already-installed Niagara modules.
        project.getPluginManager().apply("com.tridium.convention.niagara-home-repositories");

    }

}