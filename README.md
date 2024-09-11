[Niagara](https://www.tridium.com/us/en/Products/niagara) is an open framework for creating applications in building automation. At the time of creating this plugin, 
there are more than 1.3 million installations of this framework worldwide.

The standard build system, Gradle, has been updated and since Niagara 4.13  Gradle 7.6 is used instead of Gradle 4.0.2. 
The most important change is the support of hardware tokens for signing jar files.  Unfortunately, the default Gradle
configuration has become somewhat complicated and does not allow, for example, easy switching between different 
versions of Niagara.

Another disadvantage of the standard Tridium Gradle plugins is that Tridium does not distinguish between the process `build` and `deploy`.
The tasks `clean` and `jar` manipulate artifacts directly in the installation directory! This leads to some side effects, such as the fact 
that the `clean` task deletes an existing module from the `%niagara_home%\modules` installation directory. If you haven't made a backup, 
your old module is irretrievably lost and you can't switch back to the old code. This is especially annoying if your Niagara installation 
is simultaneously used for site engineering.

The Neopsis plugin tries to solve some of these problems. Firstly, it simplifies configuration because it activates Tridium plugins 
internally without having to put a lot of statements in Gradle files. Neopsis plugin also introduces a local repository where modules 
created by standard Tridium jar task are copied. The module names in the repository are historized so, that the module names are extended 
with a module version suffix. The Neopsis plugin also allows instant switching between the Niagara versions against which 
the module is compiled.

The plugin is hosted on the great free repository service `https://repsy.io`, see `https://repo.repsy.io/mvn/neopsis/niagara/`.


### Prepare `${USER_HOME}/.gradle/gradle.properties`

Before you start using the plugin, you have to define some Gradle properties in your user gradle.properties file.
In the time of writing this README the Neopsis plugin version is 1.0.1. 

```
# Set the Neopsis and Niagara plugin versions
systemProp.gradlePluginVersion=7.6.17
systemProp.settingsPluginVersion=7.6.3
systemProp.neopsisPluginVersion=1.0.1

# Map with installed Niagara releases. 
niagara_releases = {      \
       7 : "4.7.110.32", \
       8 : "4.8.0.110",  \
       9 : "4.9.0.198",  \
      10 : "4.10.6.18",  \
      11 : "4.11.2.18",  \
      12 : "4.12.2.16",  \
      13 : "4.13.0.186" } 


# Location of signing profile
signingProfileFile=C:\\Users\\username\\.tridium\\security\\your_signing_profile.properties
signingCertificateAlias=your-code-sign

# Location of Tridium Gradle tools
niagaraToolsHome=C:\\Niagara\\Development\\Tools

# Location of modules and bundles repository
niagaraRepositoryHome=C:\\Niagara\\Development\\Repository

# Niagara version against which the project is compiled, see the property niagara_releases
build_release = 13
```

* the property `niagaraToolsHome` defines the location of Niagara Gradle tools. You can download the tools
  [here](https://www.niagara-community.com/s/article/Code-Signing-using-Hardware-Security-Modules)
* the property `niagaraRepositoryHome` defines the location of all modules except Tridium modules (see the task `Bundle release` below)
* the property `niagara_releases` defines a list of all installed Niagara versions. It assumes the installation path is `C:/Niagara/Niagara-${niagara_release}`
* the property `signingProfileFile` defines a path to the file with your signing profile.
* the property `build_release` defines the Niagara version against which the project is being compiled. Use the number
  from the map `niagara_releases`. I recommend defining this property in the `gradle.properties` file that is part of the project.

The property `build_release` replaces the original Niagara `niagara_home` and `niagara_user_home` properties, that are
managed internally by the Neopsis plugin. It allows us to easily switch the version of Niagara we are compiling against.


### Example of `settings.gradle.kts`

The only thing you need to change is the project name. 

```
/*
 * Copyright 2024 Neopsis GmbH. All Rights Reserved.
 *
 */

pluginManagement {

    val neopsisPluginVersion: String by System.getProperties()

    repositories {
        maven(url = providers.gradleProperty("niagaraToolsHome").get() + "/gradlePlugins")
        maven(url = uri("https://repo.repsy.io/mvn/neopsis/niagara"))
        mavenCentral()
        gradlePluginPortal()
    }

    plugins {
        id("com.neopsis.niagara-settings-plugin") version(neopsisPluginVersion)
        id("com.neopsis.niagara-project-plugin") version (neopsisPluginVersion)
        id("com.neopsis.niagara-module-plugin") version (neopsisPluginVersion)
    }
}

plugins {
    id("com.neopsis.niagara-settings-plugin")
}

/*
 * Change the project name here
 */
rootProject.name = "MyProject"
```

### Example of `build.gradle.kts`

The standard build configuration of Niagara does not distinguish between task build and deploy and the generated 
modules are always copied to %NIAGARA_HOME%/modules. At the same time, Niagara lacks module dependency management,
i.e. information about the dependent modules and their versions. The Neopsis plugin allows to manage modules 
in a local repository, independent of the Niagara framework version. The plugin also allows to create ZIP `bundles`
containing all hanging modules with the correct version.

The `bundle` section is responsible for configuring the bundles. It has two functions:

* defines the version of the module. If you use the Neopsis plugin, you do not need to define the Niagara plugin
  properties `vendorVersion` and `vendorDescription`. These properties are defined internally by Neopsis plugin,
  based on the entries in the bundle section.
* defines the contents of the ZIP file that is created with the task `bundleModule`. The bundle will be stored in
  the repository defined by the property `niagaraRepositoryHome` (see `gradle.properies` above). 
 
Default module version numbering is as follows: 

   `[niagara-major-version].[niagara-minor-version].[module-version].[module-patch-version]`,

for example `4.10.2.1`. In the `bundle`section we define only the module version and the patch version.
The Neopsis plugin adds the Niagara major and minor versions depending on the version of Niagara
the module is compiled against. If we don't want the Neopsis plugin to prefix the module version with 
the Niagara version, we can define the property `followNiagaraNumbering = false` (default is true). 
In this case, the version of the module in the bundle section will be final. In the `bundle` section we can 
define additional modules, that will be packed together with the main module in a ZIP file for distribution. 
See the chapter `Bundle release` how to create a bundle.

##### Bundle section examples

* Module version will be <niagara-major>.<niagara-minor>.3.2, i.e. 4.10.3.2, 4.11.3.2, 4.12.3.2, 4.13.32. etc.
  depending on the Niagara version you are compiling against.

```
bundle {
    ...
    moduleVersion = "3.2"
    ...
}
```

* Module version will be 5.3.17.1, independent on the Niagara version you are compiling against.

```
bundle {
   ...
   followNiagaraNumbering = false
   moduleVersion = "5.3.17.1"
   ...
}
```

* Module version will be 1.<niagara-minor>.24. i.e 1.10.24, 1.11.24, 1.12.24, 1.13.24, etc. depending on the Niagara
  version you are compiling against.

```
bundle {
  ...
  followNiagaraNumbering = false
  moduleVersion = "1.${niagaraMinorVersion}.17.1"
  ...
}
```

Same rules are valid for the dependent modules included in the bundle.

Full `build.gradle.kts` example

```
import org.gradle.api.internal.artifacts.dsl.dependencies.DependencyAdderExtensionModule.module

/*
 * Copyright 2024 Neopsis GmbH. All Rights Reserved.
 */

repositories {
     maven(url = providers.gradleProperty("niagaraToolsHome").get() + "/gradlePlugins")
     maven(url = uri("https://repo.repsy.io/mvn/neopsis/niagara"))
     mavenCentral()
     gradlePluginPortal()
}

plugins {
    id("com.neopsis.niagara-project-plugin")
}

bundle {
    description = "My Project"
    moduleName = "MyProject"
    moduleVersion = "3.2"
    modules {
         module("thirdPartyModule", "0.4")
         module("anotherModule", "5.${niagaraMinorVersion}.2.1", false)
    }
}

niagaraSigning {
    aliases.set(listOf(providers.gradleProperty("signingCertificateAlias").get())
    signingProfileFile.set(file(providers.gradleProperty("signingProfileFile").get())
}


////////////////////////////////////////////////////////////////
// Dependencies and configurations... configuration
////////////////////////////////////////////////////////////////

subprojects {
    repositories {
        mavenCentral()
    }
}
```

### Example `moduleName-xx.gradle.kts`

Example of a minimal module Gradle file (replace xx with rt/wb/ux, etc.). 

```
import com.tridium.gradle.plugins.module.util.ModulePart.RuntimeProfile.xx

# import if using the project layout that follows the Maven standard (`/src/main/java/packages),
# ignore for Niagara default (/src/packages)
import com.tridium.gradle.plugins.niagara.NiagaraProjectLayout

plugins {
    id("com.neopsis.niagara-module-plugin")
}

description = "Anything about your module ..."

moduleManifest {
    preferredSymbol.set("pref")
    moduleName.set("moduleName")
    runtimeProfile.set(xx)
}

# Use this definition for Maven source code layout 
niagaraProject {
    niagaraProjectLayout.set(NiagaraProjectLayout.NIAGARA_MAVEN)
}

dependencies {
    nre(":nre")
    api(":baja")
    // add your additional dependencies
}
```

### Task `buildRelease`

The `buildRelease` task extends the standard `jar` task by copying generated artifacts from the `%niagara_home%\modules` 
directory `niagaraRepositoryHome/modules-4.x`. In doing so, the module name in the repository is expanded by its version, 
so that the old version is not irreversibly overwritten. The task depends on the tasks `clean` and `jar`. The `buildRelease`
task always compiles against the Niagara version defined in the property `build_release`.

### Task `bundleRelease`

The `bundleRelease` creates the final module bundle that is ready for distribution. The task depends on the task `buildRelease`
and it creates a ZIP file with all module parts and optional additional modules defined in `build.gradle.kts`, section `bundle modules{}`. 
The ZIP files ar saved in `niagaraRepositoryHome` directory. You can use the batch file `buildAllReleases.cmd` to create all bundles 
for all Niagara releases in one shot. 
