import java.util.Properties

rootProject.name = "buildSrc"

val rootProps = Properties().apply {
    file("../gradle.properties").inputStream().use { load(it) }
}

val allowSonatypeSnapshots = rootProps.getProperty("openrndr.allowSonatypeSnapshots") == "true"
val allowLocalSnapshots = rootProps.getProperty("openrndr.allowLocalSnapshots") == "true"

dependencyResolutionManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()

        if (allowLocalSnapshots) {
            mavenLocal {
                content {
                    includeGroup("org.openrndr")
                    includeGroup("org.openrndr.extra")
                }
            }
        }

        if (allowSonatypeSnapshots) {
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
        val versionsTomlFile = settingsDir.parentFile.resolve("gradle/libs.versions.toml")
        create("libs") {
            from(files(versionsTomlFile))
        }
        // We use a regex to get the openrndr/orx versions from the primary catalog as there is no public Gradle API to parse catalogs.
        val orRegEx = Regex("^openrndr[ ]*=[ ]*(?:\\{[ ]*require[ ]*=[ ]*)?\"(.*)\"[ ]*(?:\\})?", RegexOption.MULTILINE)
        val orxRegEx = Regex("^orx[ ]*=[ ]*(?:\\{[ ]*require[ ]*=[ ]*)?\"(.*)\"[ ]*(?:\\})?", RegexOption.MULTILINE)
        val openrndrVersion =
            orRegEx.find(versionsTomlFile.readText())?.groupValues?.get(1) ?: error("can't find openrndr version")
        val orxVersion =
            orxRegEx.find(versionsTomlFile.readText())?.groupValues?.get(1) ?: error("can't find orx version")

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
