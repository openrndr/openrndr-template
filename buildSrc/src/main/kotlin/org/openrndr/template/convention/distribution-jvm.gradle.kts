package org.openrndr.template.convention
import org.gradle.accessors.dm.LibrariesForLibs
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.gradle.internal.os.OperatingSystem

val libs = the<LibrariesForLibs>()

var applicationMainClass: String = "setme"

plugins {
    kotlin("jvm")
    id("com.github.johnrengelman.shadow")
    id("org.beryx.runtime")
    application
}

tasks {
    named<ShadowJar>("shadowJar") {
        manifest {
            attributes["Main-Class"] = application.mainClass.get()
            attributes["Implementation-Version"] = project.version
        }
        minimize {
            exclude(dependency("org.openrndr:openrndr-gl3:.*"))
            exclude(dependency("org.jetbrains.kotlin:kotlin-reflect:.*"))
            exclude(dependency("org.slf4j:slf4j-simple:.*"))
            exclude(dependency("org.apache.logging.log4j:log4j-slf4j2-impl:.*"))
            exclude(dependency("com.fasterxml.jackson.core:jackson-databind:.*"))
            exclude(dependency("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:.*"))
        }
    }
    named<org.beryx.runtime.JPackageTask>("jpackage") {
        doLast {
            val destPath = if(OperatingSystem.current().isMacOsX)
                "build/jpackage/openrndr-application.app/Contents/Resources/data"
            else
                "build/jpackage/openrndr-application/data"

            copy {
                from("data") {
                    include("**/*")
                }
                into(destPath)
            }
        }
    }
}

tasks.register<Zip>("jpackageZip") {
    archiveFileName.set("openrndr-application.zip")
    from("${layout.buildDirectory.get()}/jpackage") {
        include("**/*")
    }
}
tasks.findByName("jpackageZip")?.dependsOn("jpackage")

runtime {
    jpackage {
        imageName = "openrndr-application"
        skipInstaller = true
        if (OperatingSystem.current().isMacOsX) {
            jvmArgs.add("-XstartOnFirstThread")
            jvmArgs.add("-Duser.dir=${"$"}APPDIR/../Resources")
        }
    }
    options.set(listOf("--strip-debug", "--compress", "1", "--no-header-files", "--no-man-pages"))
    modules.set(listOf("jdk.unsupported", "java.management", "java.desktop"))
}
