plugins {
    `kotlin-dsl`
}

repositories {
    mavenCentral()
    mavenLocal()
    gradlePluginPortal()
}

dependencies {
    implementation(libs.kotlin.gradle.plugin)

    implementation(libs.shadow.gradle.plugin)
    implementation(libs.runtime.gradle.plugin)

    //implementation(libs.kotlin.serialization.gradle.plugin)
    // https://github.com/gradle/gradle/issues/15383#issuecomment-779893192
    implementation(files(libs.javaClass.superclass.protectionDomain.codeSource.location))
}

gradlePlugin {
    plugins {
        register("openrndr-plugin") {
            id = "openrndr"
            implementationClass = "org.openrndr.template.OpenrndrPlugin"
        }
    }
}
