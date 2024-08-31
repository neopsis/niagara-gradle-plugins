package com.neopsis.gradle.tasks;

import com.neopsis.gradle.extensions.NeopsisNiagaraProjectExtension;
import com.neopsis.gradle.model.Module;
import org.gradle.api.DefaultTask;
import org.gradle.api.Project;
import org.gradle.api.provider.Property;
import org.gradle.jvm.tasks.Jar;
import org.gradle.work.DisableCachingByDefault;

import javax.inject.Inject;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Set;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

@DisableCachingByDefault(because = "Not worth caching")
public class NeopsisBuildTask extends DefaultTask {

    Property<NeopsisNiagaraProjectExtension> extension;
    Set<Project>                             projects;

    @Inject
    public NeopsisBuildTask(NeopsisNiagaraProjectExtension ext) {
        projects  = getProject().getSubprojects();
        extension = getProject().getObjects().property(NeopsisNiagaraProjectExtension.class);
        extension.set(ext);

        // rebuild all subprojects from clean state before making bundle
        for (Project project : projects) {
            this.dependsOn(project.getTasksByName("clean", false));
            this.dependsOn(project.getTasksByName("jar", false));
        }

        doLast(task -> {

            System.out.println("Copy release to the local repository");

            String niagaraModuleDir  = ext.getNiagaraModuleDir();
            String neopsisModuleDir  = ext.getRepositoryModuleDir();
            String moduleFullVersion = ext.getModuleFullVersion();
            for (Project p : projects) {
                String source = niagaraModuleDir + "\\" + p.getName() + ".jar";
                String target = neopsisModuleDir + "\\" + p.getName() + "-" + moduleFullVersion + ".jar";

                System.out.println("Copy module " + source + " to " + target);
                try {
                    Files.copy(new File(source).toPath(), new File(target).toPath(), REPLACE_EXISTING);
                } catch (Exception e) {
                    System.out.println("Unable to copy from " + source + " to " + target);
                    throw new RuntimeException(e);
                }
            }
        });
    }
}
