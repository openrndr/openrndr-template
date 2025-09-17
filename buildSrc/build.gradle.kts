plugins {
    `kotlin-dsl`
}

val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")

dependencies {
    implementation(libs.findLibrary("shadow-gradle-plugin").get())
    implementation(libs.findLibrary("runtime-gradle-plugin").get())
    implementation(libs.findLibrary("kotlin-gradle-plugin").get())
    implementation(libs.findLibrary("xversions-gradle-plugin").get())
}