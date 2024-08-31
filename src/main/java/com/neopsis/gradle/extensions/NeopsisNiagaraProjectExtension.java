package com.neopsis.gradle.extensions;

import com.neopsis.gradle.model.Modules;
import com.tridium.gradle.plugins.vendor.VendorExtension;
import org.gradle.api.Action;
import org.gradle.api.Project;

/**
 * Niagara project plugin extension
 */
public class NeopsisNiagaraProjectExtension {

    private String  description;
    private String  moduleVersion;
    private String  moduleName;
    private String  vendor;
    private Boolean followNiagaraNumbering;

    private final String          niagaraMajorVersion;
    private final String          niagaraMinorVersion;
    private final String          niagaraFullVersion;
    private final String          niagaraVersion;
    private final String          niagaraHome;
    private final String          niagaraUserHome;
    private final String          niagaraRepositoryHome;
    private final String          niagaraModuleDir;
    private final String          repositoryModuleDir;
    private final Modules         modules;
    private final VendorExtension vendorExtension;

    /**
     * In constructor we can parse values from gradle.properties
     *
     * @param prj project
     * @param ve  vendor
     */
    public NeopsisNiagaraProjectExtension(Project prj, VendorExtension ve) {

        String[] versionParts;

        vendorExtension = ve;
        modules         = prj.getObjects().newInstance(Modules.class);

        // defaults
        vendor                 = "Neopsis";
        followNiagaraNumbering = true;

        // get properties and parse dependant values
        String nh  = prj.getProviders().gradleProperty("niagara_home").getOrNull();
        String nuh = prj.getProviders().gradleProperty("niagara_user_home").getOrNull();

        if ((nh == null || nuh == null)) {
            try {
                nh  = System.getProperty("niagara_home");
                nuh = System.getProperty("niagara_user_home");
            } catch (Exception e) {
                System.out.println("ERROR: " + e.getMessage());
            }
        }

        niagaraHome           = nh;
        niagaraUserHome       = nuh;
        niagaraRepositoryHome = prj.getProviders().gradleProperty("niagaraRepositoryHome").get();
        niagaraModuleDir      = niagaraHome + "\\modules";
        niagaraFullVersion    = niagaraHome.split("-")[1];
        versionParts          = niagaraFullVersion.split("\\.");
        niagaraMajorVersion   = versionParts[0];
        niagaraMinorVersion   = versionParts[1];
        niagaraVersion        = niagaraMajorVersion + "." + niagaraMinorVersion;
        repositoryModuleDir   = niagaraRepositoryHome + "\\modules-" + niagaraVersion;


        System.out.println("Niagara project extension configured:");
        System.out.println("Niagara version:        " + getNiagaraFullVersion());
        System.out.println("Niagara modules dir:    " + getNiagaraModuleDir());
        System.out.println("Repository modules dir: " + getRepositoryModuleDir());
        System.out.println("Bundle target dir:      " + getNiagaraRepositoryHome());
        System.out.println("niagara_home:           " + nh);
        System.out.println("niagara_user_home:      " + nuh);

        ve.defaultVendor(vendor);
    }

    public String getModuleVersion() {
        return moduleVersion;
    }

    public void setModuleVersion(String moduleVersion) {
        this.moduleVersion = moduleVersion;
        vendorExtension.defaultModuleVersion(getModuleFullVersion());
    }

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getVendor() {
        return vendor;
    }

    public void setVendor(String vendor) {
        this.vendor = vendor;
        vendorExtension.defaultVendor(vendor);
    }

    public Boolean getFollowNiagaraNumbering() {
        return followNiagaraNumbering;
    }

    public void setFollowNiagaraNumbering(Boolean fnn) {
        this.followNiagaraNumbering = fnn;
        if (moduleVersion != null) {
            setModuleVersion(this.moduleVersion);
        }
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
     * @return Niagara modules directory in ${NIAGARA_HOME}/modules
     */
    public String getNiagaraModuleDir() {
        return niagaraModuleDir;
    }

    /**
     * Neopsis module directory, e.g. ${REPOSITORY_HOME}/modules-${NIAGARA_VERSION}
     *
     * @return Repository module directory
     */
    public String getRepositoryModuleDir() {
        return repositoryModuleDir;
    }

    /**
     * Niagara major version number
     *
     * @return Niagara minor version, ex. 13
     */
    public String getNiagaraMajorVersion() {
        return niagaraMajorVersion;
    }

    /**
     * Niagara minor version number
     *
     * @return Niagara major version, currently always 4
     */
    public String getNiagaraMinorVersion() {
        return niagaraMinorVersion;
    }

    /**
     * Niagara version number, e.g. ${MAJOR}.${MINOR}
     *
     * @return Main nNiagara version number like 4.13
     */
    public String getNiagaraVersion() {
        return niagaraVersion;
    }

    /**
     * Niagara full version number, e.g. 4.13.110.7
     *
     * @return Full Niagara version like 4.13.110.7
     */
    public String getNiagaraFullVersion() {
        return niagaraFullVersion;
    }

    /**
     * Returs list of bundeled modules
     *
     * @return All modules
     */
    public Modules getModules() {
        return modules;
    }

    /**
     * Sets a list of bundeled modules
     *
     * @param action Action to be performed
     */
    public void modules(Action<Modules> action) {
        action.execute(modules);
    }
}


