rootProject.name = extra["project.name"]?.toString() ?: error("project.name not set")

pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenLocal()
    }
}

val allowSonatypeSnapshots = providers.gradleProperty("openrndr.allowSonatypeSnapshots")
val allowLocalSnapshots = providers.gradleProperty("openrndr.allowLocalSnapshots")

dependencyResolutionManagement {
    repositories {
        mavenCentral()
        if (allowLocalSnapshots.get() == "true") {
            mavenLocal {
                content {
                    includeGroup("org.openrndr")
                    includeGroup("org.openrndr.extra")
                }
            }
        }

        if (allowSonatypeSnapshots.get() == "true") {
            maven("https://central.sonatype.com/repository/maven-snapshots/") {
                name = "Central Portal Snapshots"

                content {
                    includeGroup("org.openrndr")
                    includeGroup("org.openrndr.extra")
                }
            }
        }
    }


    versionCatalogs {
        // We use a regex to get the openrndr/orx versions from the primary catalog as there is no public Gradle API to parse catalogs.
        val orRegEx = Regex("^openrndr[ ]*=[ ]*(?:\\{[ ]*require[ ]*=[ ]*)?\"(.*)\"[ ]*(?:\\})?", RegexOption.MULTILINE)
        val orxRegEx = Regex("^orx[ ]*=[ ]*(?:\\{[ ]*require[ ]*=[ ]*)?\"(.*)\"[ ]*(?:\\})?", RegexOption.MULTILINE)
        val openrndrVersion = orRegEx.find(File(rootDir,"gradle/libs.versions.toml").readText())?.groupValues?.get(1) ?: error("can't find openrndr version")
        val orxVersion = orxRegEx.find(File(rootDir,"gradle/libs.versions.toml").readText())?.groupValues?.get(1) ?: error("can't find orx version")

        create("orx") {
            from("org.openrndr.extra:orx-module-catalog:$orxVersion")
        }
        create("openrndr") {
            from("org.openrndr:openrndr-module-catalog:$openrndrVersion")
        }
    }
}
