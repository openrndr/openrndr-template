package conventions

import org.gradle.internal.os.OperatingSystem
import kotlin.collections.set
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.panteleyev.jpackage.ImageType

plugins {
    application
    id("com.gradleup.shadow")
    id("org.panteleyev.jpackageplugin")
}

val applicationMainClass = providers.gradleProperty("applicationMainClass").get()

// packaging properties, override in gradle.properties
val packageAppName = providers.gradleProperty("package.appName").getOrElse("openrndr-application")
val packageAppVersion = providers.gradleProperty("package.appVersion").getOrElse("1.0.0")
val packageVendor = providers.gradleProperty("package.vendor").getOrElse("OPENRNDR")

application {
    mainClass = if (hasProperty("openrndr.application"))
        "${property("openrndr.application")}"
    else
        applicationMainClass
}

tasks.shadowJar {
    isZip64 = true
    manifest {
        attributes["Main-Class"] = applicationMainClass
        attributes["Implementation-Version"] = project.version
    }
}

tasks.register<Copy>("copyShadowJar") {
    dependsOn("shadowJar")
    from(tasks.findByName("shadowJar")).into("${layout.buildDirectory.get()}/jars")
}

tasks.jpackage {
    dependsOn("copyShadowJar")
    addModules = listOf("jdk.unsupported", "java.management", "java.logging")

    input = layout.buildDirectory.dir("jars")
    destination = layout.buildDirectory.dir("dist")
    appName = packageAppName
    appVersion = packageAppVersion
    vendor = packageVendor

    mac {
        javaOptions = listOf(
            "-Dfile.encoding=UTF-8",
            "-XX:+UseCompactObjectHeaders",
            "-XX:+UseZGC",
            "-XstartOnFirstThread",
            "--enable-native-access=ALL-UNNAMED",
            "-Duser.dir=${'$'}APPDIR/../Resources"
        )
    }

    windows {
        javaOptions = listOf(
            "-Dfile.encoding=UTF-8",
            "-XX:+UseCompactObjectHeaders",
            "-XX:+UseZGC",
            "--enable-native-access=ALL-UNNAMED",
            "-Duser.dir=${'$'}APPDIR\\.."
        )
    }

    linux {
        javaOptions = listOf(
            "-Dfile.encoding=UTF-8",
            "-XX:+UseCompactObjectHeaders",
            "-XX:+UseZGC",
            "--enable-native-access=ALL-UNNAMED",
            "-Duser.dir=${'$'}APPDIR/../.."
        )
    }

    mainJar = tasks.named<ShadowJar>("shadowJar").get().archiveFileName.get()
    mainClass = applicationMainClass

    jLinkOptions = listOf(
        "--strip-debug",
        "--compress=2",
        "--no-header-files",
        "--no-man-pages",
    )

    type = ImageType.APP_IMAGE
    finalizedBy("copyAssetsToAppImage")
}

// copies data/ next to the directory that -Duser.dir points to
tasks.register<Copy>("copyAssetsToAppImage") {
    val destinationPath = if (OperatingSystem.current().isMacOsX) {
        layout.buildDirectory.dir("dist/$packageAppName.app/Contents/Resources")
    } else {
        layout.buildDirectory.dir("dist/$packageAppName")
    }
    from(layout.projectDirectory) {
        include("data/**")
    }
    into(destinationPath)
}

tasks.register<Zip>("zipAppImage") {
    group = "distribution"

    dependsOn("jpackage")
    dependsOn("copyAssetsToAppImage")

    from(layout.buildDirectory.dir("dist")) {
        if (OperatingSystem.current().isMacOsX) {
            include("$packageAppName.app/**")
        } else {
            include("$packageAppName/**")
        }
    }

    archiveFileName = "$packageAppName-$packageAppVersion.zip"
    destinationDirectory = layout.buildDirectory.dir("dist")
}
