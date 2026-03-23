group = property("project.group") ?: error("project.group not set")
version = property("project.version") ?: error("project.version not set")

plugins {
    alias(libs.plugins.kotlin.serialization)
    id("conventions.kotlin-jvm")
    id("conventions.openrndr-tasks")
    id("conventions.distribute-application")
}

dependencies {
    implementation(openrndr.bundles.basic)
    implementation(openrndr.bundles.video)
//    runtimeOnly(openrndr.bundles.runtime.sdl)
    runtimeOnly(openrndr.bundles.runtime.glfw)
    runtimeOnly(openrndr.gl3)
    implementation(openrndr.dialogs)
    implementation(openrndr.orextensions)

    implementation(orx.bundles.basic)
    implementation(orx.olive)

    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.serialization.core)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.slf4j.api)
    implementation(libs.kotlin.logging)
    runtimeOnly(libs.bundles.logging.simple)
    testImplementation(libs.junit)
}