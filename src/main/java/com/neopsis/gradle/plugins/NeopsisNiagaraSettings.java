package com.neopsis.gradle.plugins;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.tridium.gradle.plugins.settings.LocalSettingsExtension;
import com.tridium.gradle.plugins.settings.MultiProjectExtension;
import org.gradle.api.Plugin;
import org.gradle.api.artifacts.dsl.RepositoryHandler;
import org.gradle.api.initialization.Settings;
import org.gradle.plugin.management.PluginManagementSpec;

/**
 * Neopsis Niagara settings plugin defines all Tridium plugins via Gradle PluginManagement.
 * Because PluginManagement is called before project settings, Neopsis settings plugin
 * must be still configured in the project settings script. Anyvay, it reduces the content
 * of the settings script
 *
 * <pre>
 *     pluginManagement {
 *         val neopsisPluginVersion: String by System.getProperties()
 *
 *     repositories {
 *         mavenCentral()
 *         maven(url = providers.gradleProperty("niagaraToolsHome").get() + "/gradlePlugins")
 *         gradlePluginPortal()
 *     }
 *
 *     plugins {
 *         id("neopsis-project-plugin")  version (neopsisPluginVersion)
 *         id("neopsis-module-plugin")   version (neopsisPluginVersion)
 *         id("neopsis-settings-plugin") version (neopsisPluginVersion)
 *     }
 * }
 *
 * plugins {
 *
 *     id("neopsis-settings-plugin")
 * }
 * </pre>
 */
public class NeopsisNiagaraSettings implements Plugin<Settings> {

    @Override
    public void apply(Settings settings) {

        String buildRelease = settings.getProviders().gradleProperty("build_release").getOrNull();
        String niagaraHome = settings.getProviders().gradleProperty("niagara_home").getOrNull();
        String niagaraUserHome = settings.getProviders().gradleProperty("niagara_user_home").getOrNull();
        String releases = settings.getProviders().gradleProperty("niagara_releases").getOrNull();

        if ((niagaraHome == null || niagaraUserHome == null) && buildRelease != null && releases != null) {
            JsonObject jsonReleases = JsonParser.parseString(releases).getAsJsonObject();
            String niagaraRelease = jsonReleases.get(buildRelease).getAsString();
            String[] niagara_version_parts = niagaraRelease.split("\\.");
            String niagara_version = niagara_version_parts[0] + "." + niagara_version_parts[1];
            String niagara_home = "C:\\Niagara\\Niagara-" + niagaraRelease;
            String niagara_user_home = System.getProperty("user.home") + "\\Niagara" + niagara_version + "\\tridium";

            System.setProperty("niagara_home", niagara_home);
            System.setProperty("niagara_user_home", niagara_user_home);
        }

        String settingsPluginVersion = System.getProperty("settingsPluginVersion", "7.6.2");
        String gradlePluginVersion = System.getProperty("gradlePluginVersion", "7.6.17");

        PluginManagementSpec pms = settings.getPluginManagement();
        RepositoryHandler rh = getArtifactRepositories(settings, pms);
        rh.gradlePluginPortal();
        rh.mavenCentral();
        pms.getPlugins().id("neopsis-module-plugin").version("1.0.0");
        pms.getPlugins().id("neopsis-project-plugin").version("1.0.0");
        pms.getPlugins().id("com.tridium.settings.multi-project").version(settingsPluginVersion);
        pms.getPlugins().id("com.tridium.settings.local-settings-convention").version(settingsPluginVersion);
        pms.getPlugins().id("com.tridium.niagara").version(gradlePluginVersion);
        pms.getPlugins().id("com.tridium.vendor").version(gradlePluginVersion);
        pms.getPlugins().id("com.tridium.niagara-module").version(gradlePluginVersion);
        pms.getPlugins().id("com.tridium.niagara-signing").version(gradlePluginVersion);
        pms.getPlugins().id("com.tridium.convention.niagara-home-repositories").version(gradlePluginVersion);

        settings.getPluginManager().apply("com.tridium.settings.multi-project");
        settings.getPluginManager().apply("com.tridium.settings.local-settings-convention");

        // add Tridium localSettings and multiProject extensions
        LocalSettingsExtension ext1 = settings.getExtensions().create("neoLocalSettings", LocalSettingsExtension.class);
        ext1.loadLocalSettings(settings);
        MultiProjectExtension ext2 = settings.getExtensions().create("neoMultiProjectSettings", MultiProjectExtension.class);
        ext2.findProjects(settings, null, false);

    }

    /**
     * Return all artifact repositories used in Neopsis plugins. There is a convention - local repositories
     * are located under %NIAGARA_TOOLS_HOME%/gradlePlugins
     * <p>
     * Additional repositories can be added in the Gradle settings script
     *
     * @param settings the settings object
     * @param pms      plugin management object
     * @return
     */
    private static RepositoryHandler getArtifactRepositories(Settings settings, PluginManagementSpec pms) {
        RepositoryHandler rh = pms.getRepositories();
        rh.maven(mar -> mar.setUrl(settings.getProviders().gradleProperty("niagaraToolsHome").get() + "\\gradlePlugins"));
        rh.mavenCentral();
        return rh;
    }
}
