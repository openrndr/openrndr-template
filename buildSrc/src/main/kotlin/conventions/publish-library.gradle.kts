package conventions

import org.gradle.kotlin.dsl.java
import org.gradle.kotlin.dsl.kotlin

plugins {
    java
    kotlin("jvm")
    `maven-publish`
}

val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")
val openrndr = extensions.getByType<VersionCatalogsExtension>().named("openrndr")

val demo = sourceSets.create("demo")
val main = sourceSets.getByName("main")

demo.compileClasspath += main.compileClasspath
demo.runtimeClasspath += main.runtimeClasspath
demo.compileClasspath += main.output
demo.runtimeClasspath += main.output

dependencies {
    "demoRuntimeOnly"(openrndr.findLibrary("gl3").get())
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
            groupId = property("project.group")?.toString() ?: error("project.group not set")
            artifactId = property("project.name")?.toString() ?: error("project.name not set")
            description = property("project.name")?.toString() ?: error("project.name not set")
            version = property("project.version")?.toString() ?: error("project.version not set")
        }
    }
}