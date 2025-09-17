rootProject.name = "buildSrc"

dependencyResolutionManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
        mavenLocal {
            content {
                includeGroup("org.openrndr")
                includeGroup("org.openrndr.extra")
            }
        }
    }

    versionCatalogs {
        create("libs") {
            from(files("../gradle/libs.versions.toml"))
        }
        // We use a regex to get the openrndr/orx versions from the primary catalog as there is no public Gradle API to parse catalogs.
        val orRegEx = Regex("^openrndr[ ]*=[ ]*(?:\\{[ ]*require[ ]*=[ ]*)?\"(.*)\"[ ]*(?:\\})?", RegexOption.MULTILINE)
        val orxRegEx = Regex("^orx[ ]*=[ ]*(?:\\{[ ]*require[ ]*=[ ]*)?\"(.*)\"[ ]*(?:\\})?", RegexOption.MULTILINE)
        val openrndrVersion = orRegEx.find(File(rootDir,"../gradle/libs.versions.toml").readText())?.groupValues?.get(1) ?: error("can't find openrndr version")
        val orxVersion = orxRegEx.find(File(rootDir,"../gradle/libs.versions.toml").readText())?.groupValues?.get(1) ?: error("can't find orx version")

        create("orx") {
            from("org.openrndr.extra:orx-module-catalog:$orxVersion")
        }
        create("openrndr") {
            from("org.openrndr:openrndr-module-catalog:$openrndrVersion")
        }
    }
}

pluginManagement {
    repositories {
        gradlePluginPortal()

    }
}