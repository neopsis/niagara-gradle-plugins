import org.gradle.internal.impldep.org.bouncycastle.asn1.x500.style.RFC4519Style.o
import org.gradle.internal.impldep.org.bouncycastle.cms.RecipientId.password

/*
 * Neopsis Gradle plugins for Niagara projects
 */

plugins {
    id("java")
    id("java-gradle-plugin")
    id("maven-publish")
    // id("com.gradle.plugin-publish") version "1.2.1"

}

group   = "com.neopsis"
version = "1.0.1"

gradlePlugin {
    website.set("https://github.com/neopsis")
    vcsUrl.set("https://github.com/neopsis/niagara-gradle-plugins")

    plugins {
        create("neopsisNiagaraSettings") {
            id                  = "com.neopsis.niagara-settings-plugin"
            implementationClass = "com.neopsis.gradle.plugins.NeopsisNiagaraSettings"
            displayName         = "Neopsis Niagara settings plugin"
            description         = "Neopsis Gradle plugin for Niagara settings"
            tags.set(listOf("neopsis", "niagara"))

        }
        create("neopsisNiagaraModule") {
            id                  = "com.neopsis.niagara-module-plugin"
            implementationClass = "com.neopsis.gradle.plugins.NeopsisNiagaraModule"
            displayName         = "Neopsis Niagara module plugin"
            description         = "Neopsis Gradle plugin for Niagara modules build"
            tags.set(listOf("neopsis", "niagara"))
        }
        create("neopsisNiagaraProject") {
            id                  = "com.neopsis.niagara-project-plugin"
            implementationClass = "com.neopsis.gradle.plugins.NeopsisNiagaraProject"
            displayName         = "Neopsis Niagara project plugin"
            description         = "Neopsis Gradle plugin for Niagara project build"
            tags.set(listOf("neopsis", "niagara"))
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
    publications {

        create<MavenPublication>("maven") {
            from(components["java"])
        }

        repositories {
            maven {
                url = uri(providers.gradleProperty("repsyUrl").get())
                credentials {
                    username = providers.gradleProperty("repsyUsername").get()
                    password = providers.gradleProperty("repsyPassword").get()
                }
            }
        }
    }
}

tasks.test {
    useJUnitPlatform()
}
