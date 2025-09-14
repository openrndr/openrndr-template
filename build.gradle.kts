group = "org.openrndr.template"
version = "1.0.0"

plugins {
    alias(libs.plugins.kotlin.serialization)
    id("conventions.kotlin-jvm")
    id("conventions.openrndr-tasks")
    id("conventions.distribution")
}

dependencies {
    implementation(openrndr.application)
    implementation(openrndr.draw)
    runtimeOnly(openrndr.gl3)

    implementation(openrndr.ffmpeg)
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