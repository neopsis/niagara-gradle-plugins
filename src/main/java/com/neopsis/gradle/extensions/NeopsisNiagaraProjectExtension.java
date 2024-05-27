package com.neopsis.gradle.extensions;

import com.neopsis.gradle.model.Modules;
import com.tridium.gradle.plugins.vendor.VendorExtension;
import org.gradle.api.Action;
import org.gradle.api.Project;

/**
 * Niagara project plugin extension
 */
public class NeopsisNiagaraProjectExtension {

    public String  description;
    private String moduleVersion;
    private String vendor;
    public Boolean followNiagaraNumbering;

    private final String niagaraMajorVersion;
    private final String niagaraMinorVersion;
    private final String niagaraFullVersion;
    private final String niagaraVersion;
    private final String niagaraHome;
    private final String niagaraUserHome;
    private final String niagaraRepositoryHome;
    private final String niagaraModuleDir;
    private final String repositoryModuleDir;
    private final Modules modules;
    private final VendorExtension vendorExtension;

    /**
     * In constructor we can parse values from gradle.properties
     *
     * @param prj
     */
    public NeopsisNiagaraProjectExtension(Project prj, VendorExtension ve) {

        String[] versionParts;

        vendorExtension = ve;
        modules         = prj.getObjects().newInstance(Modules.class);

        // defaults
        vendor = "Neopsis";
        followNiagaraNumbering = true;

        // get gradle.properties and parse dependant values
        niagaraHome = prj.getProviders().gradleProperty("niagara_home").get();
        niagaraUserHome = prj.getProviders().gradleProperty("niagara_user_home").get();
        niagaraRepositoryHome = prj.getProviders().gradleProperty("niagaraRepositoryHome").get();
        niagaraModuleDir = niagaraHome + "/modules";
        niagaraFullVersion = niagaraHome.split("-")[1];
        versionParts = niagaraFullVersion.split("\\.");
        niagaraMajorVersion = versionParts[0];
        niagaraMinorVersion = versionParts[1];
        niagaraVersion = niagaraMajorVersion + "." + niagaraMinorVersion;
        repositoryModuleDir = niagaraRepositoryHome + "/modules-" + niagaraVersion;

    }

    public String getModuleVersion() {
        return moduleVersion;
    }

    public void setModuleVersion(String moduleVersion) {
        this.moduleVersion = moduleVersion;
        vendorExtension.defaultModuleVersion(getModuleFullVersion());
    }

    public String getVendor() {
        return vendor;
    }

    public void setVendor(String vendor) {
        this.vendor = vendor;
        vendorExtension.defaultVendor(vendor);
    }

    /**
     * Module full version depends on the property followNiagaraNumbering, evaluated after the configuration run
     *
     * @return module full version number
     */
    public String getModuleFullVersion() {
        if (followNiagaraNumbering) {
            return niagaraMajorVersion + "." + niagaraMinorVersion + "." + moduleVersion;
        }
        return moduleVersion;
    }

    /*
     * Getters for private fields
     */

    /**
     * Niagara home
     *
     * @return niagara home as string
     */
    public String getNiagaraHome() {
        return niagaraHome;
    }

    /**
     * Niagara user home
     *
     * @return niagara user home as string
     */
    public String getNiagaraUserHome() {
        return niagaraUserHome;
    }

    /**
     * Neopsis repository home
     *
     * @return neopsis repository home as string
     */
    public String getNiagaraRepositoryHome() {
        return niagaraRepositoryHome;
    }

    /**
     * Niagara module directory, e.g.  ${NIAGARA_HOME}/modules
     *
     * @return
     */
    public String getNiagaraModuleDir() {
        return niagaraModuleDir;
    }

    /**
     * Neopsis module directory, e.g. ${REPOSITORY_HOME}/modules-${NIAGARA_VERSION}
     *
     * @return
     */
    public String getRepositoryModuleDir() {
        return repositoryModuleDir;
    }

    /**
     * Niagara major version number
     *
     * @return
     */
    public String getNiagaraMajorVersion() {
        return niagaraMajorVersion;
    }

    /**
     * Niagara minor version number
     *
     * @return
     */
    public String getNiagaraMinorVersion() {
        return niagaraMinorVersion;
    }

    /**
     * Niagara version number, e.g. ${MAJOR}.${MINOR}
     *
     * @return
     */
    public String getNiagaraVersion() {
        return niagaraVersion;
    }

    /**
     * Niagara full version number, e.g. 4.13.110.7
     *
     * @return
     */
    public String getNiagaraFullVersion() {
        return niagaraFullVersion;
    }

    /**
     * Returs list of bundeled modules
     *
     * @return
     */
    public Modules getModules() {
        return modules;
    }

    /**
     * Sets a list of bundeled modules
     *
     * @param action
     */
    public void modules(Action<Modules> action) {
        action.execute(modules);
    }
}


