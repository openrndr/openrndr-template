package org.openrndr.template.convention
import org.gradle.accessors.dm.LibrariesForLibs
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val libs = the<LibrariesForLibs>()

plugins {
    kotlin("jvm")
    application
}

//application {
//    if (hasProperty("openrndr.application")) {
//        mainClass.set("${property("openrndr.application")}")
//    }
//}


tasks.withType<KotlinCompile> {
    kotlinOptions {
        jvmTarget = "17"
    }
    compilerOptions {
        jvmTarget.set(JvmTarget.valueOf("JVM_${libs.versions.jvmTarget.get()}"))
        freeCompilerArgs.add("-Xjdk-release=${libs.versions.jvmTarget.get()}")
    }
}

java {
    sourceCompatibility = JavaVersion.valueOf("VERSION_${libs.versions.jvmTarget.get()}")
    targetCompatibility = JavaVersion.valueOf("VERSION_${libs.versions.jvmTarget.get()}")
}

repositories {
    mavenCentral()
    mavenLocal()
}

dependencies {
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.slf4j.api)
    implementation(libs.kotlin.logging)

    implementation(libs.openrndr.application)
    implementation(libs.openrndr.animatable)
    implementation(libs.openrndr.dialogs)
    implementation(libs.openrndr.extensions)
}
