package com.neopsis.gradle.plugins;

import com.neopsis.gradle.extensions.NeopsisNiagaraProjectExtension;
import com.neopsis.gradle.tasks.NeopsisBuildTask;
import com.neopsis.gradle.tasks.NeopsisBundleTask;
import com.tridium.gradle.plugins.vendor.VendorExtension;
import com.tridium.gradle.plugins.vendor.VendorProviderExtension;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.tasks.TaskProvider;

/**
 * Apply Tridium Niagara plugins and add Tridium vendor extension to the Neopsis project extension. It allows
 * to define vendor properties in niagaraParts block.
 */
public class NeopsisNiagaraProject implements Plugin<Project> {

    @Override
    public void apply(Project project) {

        VendorProviderExtension         vpe        = project.getExtensions().create("tridiumVendorProvider", VendorProviderExtension.class);
        VendorExtension                 ve         = project.getExtensions().create("tridiumVendor", VendorExtension.class, project, vpe);
        NeopsisNiagaraProjectExtension  extension  = project.getExtensions().create("bundle", NeopsisNiagaraProjectExtension.class, project, ve);
        TaskProvider<NeopsisBuildTask>  buildTask  = project.getTasks().register("buildRelease", NeopsisBuildTask.class, extension);
        TaskProvider<NeopsisBundleTask> bundleTask = project.getTasks().register("bundleRelease", NeopsisBundleTask.class, extension);
        buildTask.get().setGroup("neopsis");
        bundleTask.get().setGroup("neopsis");

        applyPlugins(project);
    }

    /**
     * Apply Tridium Niagara plugins
     *
     * @param project Niagara project
     */
    private void applyPlugins(Project project) {

        // Base Niagara plugin
        try {
            project.getPluginManager().apply("com.tridium.niagara");
        } catch (Exception e) {
            System.out.println("Error applying niagara plugin");
        }

        // The vendor plugin provides the vendor {} extension to set the default group
        // for Maven publishing; the default vendor attribute for installable
        // manifests; and the default module and dist version for their respective
        // manifests
        try {
            project.getPluginManager().apply("com.tridium.vendor");
        } catch (Exception e) {
            System.out.println("Error applying vendor plugin");
        }

        // The signing plugin configures signing of all executables, modules, and
        // dists. It also registers a factory only on the root project to avoid
        // overhead from managing signing profiles on all subprojects
        try {
            project.getPluginManager().apply("com.tridium.niagara-signing");
        } catch (Exception e) {
            System.out.println("Error applying signing plugin");
        }

        // The niagara_home repositories convention plugin configures !bin/ext and !modules
        // as flat-file Maven repositories to allow modules to compile against Niagara
        try {
            project.getPluginManager().apply("com.tridium.convention.niagara-home-repositories");
        } catch (Exception e) {
            System.out.println("Error applying home-repositories plugin");
        }
    }

}