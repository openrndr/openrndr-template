enableFeaturePreview("VERSION_CATALOGS")
rootProject.name = "openrndr-template"

val openrndrUseSnapshot = false
val orxUseSnapshot = false
val ormlUseSnapshot = true

dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            version("kotlin", "1.7.21")
            version("openrndr", if (openrndrUseSnapshot) "0.5.1-SNAPSHOT" else "0.4.1")
            version("orx", if (orxUseSnapshot) "0.5.1-SNAPSHOT" else "0.4.1")
            version("orml", if (ormlUseSnapshot) "0.5.1-SNAPSHOT" else "0.4.1")

            plugin("kotlin-jvm", "org.jetbrains.kotlin.jvm").versionRef("kotlin")
            plugin("shadow", "com.github.johnrengelman.shadow").version("7.1.2")
            plugin("runtime", "org.beryx.runtime").version("1.12.7")

            plugin("gitarchive-tomarkdown", "org.openrndr.extra.gitarchiver.tomarkdown").versionRef("orx")

            library("kotlin-script-runtime", "org.jetbrains.kotlin", "kotlin-script-runtime").versionRef("kotlin")

            version("slf4j", "1.7.36")
            library("slf4j-api", "org.slf4j", "slf4j-api").versionRef("slf4j")
            library("slf4j-nop", "org.slf4j", "slf4j-nop").versionRef("slf4j")
            library("slf4j-simple", "org.slf4j", "slf4j-simple").versionRef("slf4j")

            version("jackson", "2.14.1")
            library("jackson-databind", "com.fasterxml.jackson.core", "jackson-databind").versionRef("jackson")
            library("jackson-json", "com.fasterxml.jackson.dataformat", "jackson-dataformat-yaml").versionRef("jackson")

            version("log4j", "2.19.0")
            library("log4j-slf4j", "org.apache.logging.log4j", "log4j-slf4j-impl").versionRef("log4j")

            version("kotlinx-coroutines", "1.6.4")
            library(
                "kotlinx-coroutines-core",
                "org.jetbrains.kotlinx",
                "kotlinx-coroutines-core"
            ).versionRef("kotlinx-coroutines")

            library("kotlin-logging", "io.github.microutils:kotlin-logging-jvm:2.1.23")
            library("junit", "junit:junit:4.13.2")

            library("jsoup", "org.jsoup:jsoup:1.15.3")
            library("gson", "com.google.code.gson:gson:2.9.1")
            library("csv", "com.github.doyaaaaaken:kotlin-csv-jvm:1.7.0")
        }
    }
}

pluginManagement {
    repositories {
        mavenLocal()
        gradlePluginPortal()
    }
}
