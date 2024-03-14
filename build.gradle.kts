group = "org.openrndr.template"
version = "1.0.0"

plugins {
    openrndr
}

openrndr {
    openrndrVersion = libs.versions.openrndr.get()
    orxVersion = libs.versions.orx.get()
    orxFeatures = setOf(
        libs.orx.olive,
        libs.orx.color,
        libs.orx.gui
    )
    openrndrFeatures = setOf(
        libs.openrndr.ffmpeg,
        libs.openrndr.gl3
    )
    mainClass = "TemplateProgramKt"
}

dependencies {
    implementation(libs.csv)
}

