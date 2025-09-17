package conventions
import org.gradle.nativeplatform.platform.internal.DefaultNativePlatform
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion

val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")

plugins {
    java
    kotlin("jvm")
    id("com.github.ben-manes.versions")
}

repositories {
    mavenCentral()
    mavenLocal()
}

java {
    targetCompatibility = JavaVersion.valueOf("VERSION_${libs.findVersion("jvmTarget").get().displayName}")
    sourceCompatibility = JavaVersion.valueOf("VERSION_${libs.findVersion("jvmTarget").get().displayName}")
}

kotlin {
    compilerOptions {
        apiVersion.set(KotlinVersion.valueOf("KOTLIN_${libs.findVersion("kotlinApi").get().displayName.replace(".", "_")}"))
        languageVersion.set(KotlinVersion.valueOf("KOTLIN_${libs.findVersion("kotlinLanguage").get().displayName.replace(".", "_")}"))
        jvmTarget.set(JvmTarget.valueOf("JVM_${libs.findVersion("jvmTarget").get().displayName}"))
    }
}

fun arch(arch: String = System.getProperty("os.arch")): String {
    return when (arch) {
        "x86-64", "x86_64", "amd64" -> "x86-64"
        "arm64", "aarch64" -> "aarch64"
        else -> error("unsupported arch $arch")
    }
}

fun Project.addHostMachineAttributesToRuntimeConfigurations() {
    val currentOperatingSystemName: String = DefaultNativePlatform.getCurrentOperatingSystem().toFamilyName()
    val currentArchitectureName: String = arch()

    configurations.matching {
        it.name.endsWith("runtimeClasspath", ignoreCase = true)
    }.configureEach {
        attributes {
            attribute(OperatingSystemFamily.OPERATING_SYSTEM_ATTRIBUTE, objects.named(currentOperatingSystemName))
            attribute(MachineArchitecture.ARCHITECTURE_ATTRIBUTE, objects.named(currentArchitectureName))
        }
    }
}

addHostMachineAttributesToRuntimeConfigurations()


tasks {
    dependencyUpdates {
        gradleReleaseChannel = "current"

        val nonStableKeywords = listOf("alpha", "beta", "rc")

        fun isNonStable(version: String) = nonStableKeywords.any {
            version.lowercase().contains(it)
        }

        rejectVersionIf {
            isNonStable(candidate.version) && !isNonStable(currentVersion)
        }
    }
}
