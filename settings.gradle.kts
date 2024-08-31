pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
        maven(url = providers.gradleProperty("niagaraToolsHome").get() + "/gradlePlugins")
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.5.0"
}

rootProject.name = "niagara-gradle-plugins"

