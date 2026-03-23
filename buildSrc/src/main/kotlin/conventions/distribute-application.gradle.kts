package conventions
import org.gradle.internal.os.OperatingSystem
import kotlin.collections.set
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    id("com.gradleup.shadow")
    id("org.beryx.runtime")
}

val applicationMainClass: String by properties

application {
    mainClass = if (hasProperty("openrndr.application"))
        "${property("openrndr.application")}"
    else
        applicationMainClass
}

tasks {
    named<ShadowJar>("shadowJar") {
        manifest {
            attributes["Main-Class"] = applicationMainClass
            attributes["Implementation-Version"] = project.version
        }
        minimize {
            exclude(dependency("org.openrndr:openrndr-gl3:.*"))
            exclude(dependency("org.openrndr:openrndr-application-glfw"))
            exclude(dependency("org.openrndr:openrndr-application-sdl"))
            exclude(dependency("org.jetbrains.kotlin:kotlin-reflect:.*"))
            exclude(dependency("org.slf4j:slf4j-simple:.*"))
            exclude(dependency("org.apache.logging.log4j:log4j-slf4j2-impl:.*"))
            exclude(dependency("com.fasterxml.jackson.core:jackson-databind:.*"))
            exclude(dependency("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:.*"))
            exclude(dependency("org.bytedeco:.*"))
        }
    }
}

tasks {
    named<org.beryx.runtime.JPackageTask>("jpackage") {
        doLast {
            val destPath = if (OperatingSystem.current().isMacOsX)
                "build/jpackage/openrndr-application.app/Contents/Resources/data"
            else
                "build/jpackage/openrndr-application/data"

            copy {
                from("data") { include("**/*") }
                into(destPath)
            }
        }
    }

    register<Zip>("jpackageZip") {
        archiveFileName = "openrndr-application.zip"
        from("${layout.buildDirectory.get()}/jpackage") {
            include("**/*")
        }
        filesMatching("**/bin/*") {
            permissions {
                unix("0755")
            }
        }
        dependsOn("jpackage")
    }
}

runtime {
    jpackage {
        imageName = "openrndr-application"
        skipInstaller = true
        if (OperatingSystem.current().isMacOsX) {
            jvmArgs.add("-XstartOnFirstThread")
            jvmArgs.add($$"-Duser.dir=$APPDIR/../Resources")
        }
    }
    options = listOf("--strip-debug", "--compress", "zip-6", "--no-header-files", "--no-man-pages")
    modules = listOf("jdk.unsupported", "java.management", "java.desktop")
}
