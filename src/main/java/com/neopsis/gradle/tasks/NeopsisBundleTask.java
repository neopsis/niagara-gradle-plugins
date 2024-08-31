package com.neopsis.gradle.tasks;

import com.neopsis.gradle.extensions.NeopsisNiagaraProjectExtension;
import com.neopsis.gradle.model.Module;
import org.gradle.api.Project;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.bundling.Zip;
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
public class NeopsisBundleTask extends Zip {

    static final String README_FILE_NAME = "README.txt";

    Property<NeopsisNiagaraProjectExtension> extension;
    Set<Project>                             projects;
    File                                     readmeFile;

    @Inject
    public NeopsisBundleTask(NeopsisNiagaraProjectExtension ext) {

        projects  = getProject().getSubprojects();
        extension = getProject().getObjects().property(NeopsisNiagaraProjectExtension.class);
        extension.set(ext);

        // we need set source and target here to avoid Zip task NO SOURCE error
        from(new File(ext.getRepositoryModuleDir()).toPath());
        getDestinationDirectory().set(new File(ext.getNiagaraRepositoryHome()));

        // rebuild all subprojects from clean state before making bundle
        // for (Project project : projects) {
//            this.dependsOn(project.getTasksByName("clean", false));
//            this.dependsOn(project.getTasksByName("jar", false));
        // }

        this.dependsOn(getProject().getTasksByName("buildRelease", false));

        doLast(task -> {
            if (readmeFile != null) {
                try {
                    Files.delete(readmeFile.toPath());
                } catch (IOException e) {
                    // let it be ...
                }
            }
        });
    }

    @Override
    protected void copy() {

        NeopsisNiagaraProjectExtension ext = extension.get();

        // local copy from extension
        String       niagaraModuleDir  = ext.getNiagaraModuleDir();
        String       neopsisModuleDir  = ext.getRepositoryModuleDir();
        String       moduleFullVersion = ext.getModuleFullVersion();
        String       niagaraVersion    = ext.getNiagaraVersion();
        List<Module> moduleList        = ext.getModules().getModuleList();
        String       moduleName        = getProject().getName();

        try {

            System.out.println("Creating bundle zip file " + moduleName + "-" + moduleFullVersion + ".zip");

            readmeFile = new File(neopsisModuleDir + "/" + README_FILE_NAME);
            FileWriter writer = new FileWriter(readmeFile); //overwrites file

            writer.write("Installation bundle for module " + ext.getModuleName() + " version " + moduleFullVersion);
            writer.write("\nDescription: " + ext.getDescription());
            writer.write("\nVendor     : " + ext.getVendor());

            // copy new artefact from niagara modules to the repository
//            String source = null;
//            String target = null;
//            for (Project p : projects) {
//                source = niagaraModuleDir + "\\" + p.getName() + ".jar";
//                target = neopsisModuleDir + "\\" + p.getName() + "-" + moduleFullVersion + ".jar";
//
//                System.out.println("Copy module " + source + " to " + target);
//
//                try {
//                    Files.copy(new File(source).toPath(), new File(target).toPath(), REPLACE_EXISTING);
//                } catch (Exception e) {
//                    System.out.println("Unable to copy from " + source + " to " + target);
//                    throw new RuntimeException(e);
//                }
//            }

            // create module bundle
            include(moduleName + "-*-" + moduleFullVersion + ".jar");
            rename(moduleName + "-(.+)-" + moduleFullVersion + ".jar", moduleName + "-$1.jar");
            writer.write("\n" + moduleName + ": " + moduleFullVersion);
            System.out.println("Adding to bundle: " + moduleName + "-" + moduleFullVersion);

            for (Module module : moduleList) {

                String modVer = "";
                if (module.getFollowNiagaraNumbering()) {
                    modVer = ext.getNiagaraVersion() + ".";
                }
                modVer = modVer + module.getVersion();


                String src        = module.getName() + "-*-" + modVer + ".jar";
                String renameFrom = module.getName() + "-(.+)-" + modVer + ".jar";
                String renameTo   = module.getName() + "-$1.jar";
                include(src);
                rename(renameFrom, renameTo);
                writer.write("\n" + module.getName() + ": " + modVer);
                System.out.println("Adding to bundle: " + module.getName() + "-" + modVer);
            }

            writer.close();
            include(README_FILE_NAME);


        } catch (IOException e) {
            System.out.println("ERROR writing bundle zip file: " + e.getMessage());
        }

        getArchiveFileName().set(moduleName + "-" + moduleFullVersion + ".zip");


        // call the Zip task action!
        super.copy();
    }
}
