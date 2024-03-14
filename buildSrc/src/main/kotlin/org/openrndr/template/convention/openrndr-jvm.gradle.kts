package org.openrndr.template.convention
import org.gradle.accessors.dm.LibrariesForLibs
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
    kotlinOptions.jvmTarget = "11"
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
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
    implementation(libs.openrndr.svg)
    implementation(libs.openrndr.animatable)
    implementation(libs.openrndr.dialogs)
    implementation(libs.openrndr.extensions)
    //implementation(libs.openrndr.filters)
}

