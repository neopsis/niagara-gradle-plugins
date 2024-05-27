/*
 * Neopsis Gradle plugins for Niagara projects
 */

plugins {
    id("java")
    id("java-gradle-plugin")
    id("maven-publish")
}

group   = "com.neopsis"
version = "1.0.0"

gradlePlugin {
    plugins {
        create("neopsisNiagaraSettings") {
            id                  = "neopsis-settings-plugin"
            implementationClass = "com.neopsis.gradle.plugins.NeopsisNiagaraSettings"
        }
        create("neopsisNiagaraModule") {
            id                  = "neopsis-module-plugin"
            implementationClass = "com.neopsis.gradle.plugins.NeopsisNiagaraModule"
        }
        create("neopsisNiagaraProject") {
            id                  = "neopsis-project-plugin"
            implementationClass = "com.neopsis.gradle.plugins.NeopsisNiagaraProject"
        }
    }
}

repositories {
    maven(
        url = providers.gradleProperty("niagaraToolsHome").get() + "/gradlePlugins"
    )
    mavenCentral()
}

dependencies {
    // implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:1.8.20")
    implementation(gradleApi())
    implementation("com.tridium.niagara:com.tridium.niagara.gradle.plugin:7.6.17")
    implementation("com.tridium.vendor:com.tridium.vendor.gradle.plugin:7.6.17")
    implementation("com.tridium.convention.niagara-home-repositories:com.tridium.convention.niagara-home-repositories.gradle.plugin:7.6.17")
    implementation("com.tridium.convention.niagara-module:com.tridium.convention.niagara-module.gradle.plugin:7.6.17")
    implementation("com.tridium.convention.settings.niagara:com.tridium.convention.settings.niagara.gradle.plugin:7.6.17")
    implementation("com.tridium.niagara-module:com.tridium.niagara-module.gradle.plugin:7.6.17")
    implementation("com.tridium.niagara-signing:com.tridium.niagara-signing.gradle.plugin:7.6.17")
    implementation("com.tridium.settings.multi-project:com.tridium.settings.multi-project.gradle.plugin:7.6.3")
    implementation("com.tridium.settings.local-settings-convention:com.tridium.settings.local-settings-convention.gradle.plugin:7.6.3")
    implementation("com.tridium.tools:gradle-settings-plugins:7.6.3")
}

publishing {
    repositories {
        maven {
            url = uri(providers.gradleProperty("niagaraToolsHome").get() + "/gradlePlugins")
        }
    }
}

tasks.test {
    useJUnitPlatform()
}
