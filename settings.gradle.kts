enableFeaturePreview("VERSION_CATALOGS")
rootProject.name = "openrndr-template"

val openrndrUseSnapshot = false
val orxUseSnapshot = false
val ormlUseSnapshot = true

dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            version("kotlin", "1.6.10")
            version("openrndr", if (openrndrUseSnapshot) "0.5.1-SNAPSHOT" else "0.4.0-rc.6")
            version("orx", if (orxUseSnapshot) "0.5.1-SNAPSHOT" else "0.4.0-rc.7")
            version("orml", if (ormlUseSnapshot) "0.5.1-SNAPSHOT" else "0.4.0")

            alias("kotlin-jvm").toPluginId("org.jetbrains.kotlin.jvm").version("1.6.10")
            alias("shadow").toPluginId("com.github.johnrengelman.shadow").version("7.1.0")
            alias("runtime").toPluginId("org.beryx.runtime").version("1.12.7")
            alias("gitarchive-tomarkdown").toPluginId("org.openrndr.extra.gitarchiver.tomarkdown").versionRef("orx")

            alias("kotlin-script-runtime").to("org.jetbrains.kotlin", "kotlin-script-runtime").versionRef("kotlin")

            version("slf4j", "1.7.32")
            alias("slf4j-nop").to("org.sl4j","slf4j-nop").versionRef("slf4j")
            alias("slf4j-simple").to("org.sl4j","slf4j-simple").versionRef("slf4j")

            version("jackson", "2.11.1")
            alias("jackson-databind").to("com.fasterxml.jackson.core", "jackson-databind").versionRef("jackson")
            alias("jackson-json").to("com.fasterxml.jackson.dataformat", "jackson-dataformat-yaml").versionRef("jackson")

            version("log4j", "2.17.0")
            alias("log4j-slf4j").to("org.apache.logging.log4j", "log4j-slf4j-impl").versionRef("log4j")

            version("kotlinx-coroutines", "1.5.2")
            alias("kotlinx-coroutines-core").to("org.jetbrains.kotlinx", "kotlinx-coroutines-core").versionRef("kotlinx-coroutines")

            alias("kotlin-logging").to("io.github.microutils:kotlin-logging-jvm:2.0.10")
            alias("junit").to("junit:junit:4.13.2")

            alias("jsoup").to("org.jsoup:jsoup:1.14.3")
            alias("gson").to("com.google.code.gson:gson:2.8.7")
            alias("csv").to("com.github.doyaaaaaken:kotlin-csv-jvm:1.2.0")
        }
    }
}

pluginManagement {
    repositories {
        mavenLocal()
        gradlePluginPortal()
    }
}
