package conventions

// Helper functions are in TemplateTasksSupport.kt. Files are resolved while the task is
// configured, so the doLast actions reference neither `project` nor this script,
// which keeps these tasks compatible with the configuration cache.

// Print the active OPENRNDR and ORX versions at the end of every build (see VersionPrinter.kt)
apply<VersionPrinterPlugin>()

tasks.register("openrndrSonatypeSnapshot") {
    group = "openrndr template"
    description = "Switch to OPENRNDR and ORX Sonatype snapshot versions"

    val libsVersionsFile = layout.settingsDirectory.file("gradle/libs.versions.toml").asFile
    val gradlePropsFile = layout.settingsDirectory.file("gradle.properties").asFile

    doLast {
        val snapshotVersion = getLatestVersion() + "-SNAPSHOT"
        setOpenrndrVersion(libsVersionsFile, snapshotVersion)
        setGradleProperties(gradlePropsFile, allowSonatypeSnapshots = true, allowLocalSnapshots = false)
    }
}

tasks.register("openrndrRelease") {
    group = "openrndr template"
    description = "Switch to OPENRNDR and ORX release versions"

    val libsVersionsFile = layout.settingsDirectory.file("gradle/libs.versions.toml").asFile
    val gradlePropsFile = layout.settingsDirectory.file("gradle.properties").asFile

    doLast {
        val releaseVersion = getLatestVersion(
            Regex("""refs/tags/v(\d+\.\d+\.\d+)$"""),
            Regex("""(\d+\.\d+\.\d+)$""")
        )
        setOpenrndrVersion(libsVersionsFile, releaseVersion)
        setGradleProperties(gradlePropsFile, allowSonatypeSnapshots = false, allowLocalSnapshots = false)
    }
}

tasks.register("openrndrLocalSnapshot") {
    group = "openrndr template"
    description = "Switch to OPENRNDR and ORX local snapshot versions"

    val libsVersionsFile = layout.settingsDirectory.file("gradle/libs.versions.toml").asFile
    val gradlePropsFile = layout.settingsDirectory.file("gradle.properties").asFile

    doLast {
        val snapshotVersion = getLatestVersion() + "-SNAPSHOT"
        setOpenrndrVersion(libsVersionsFile, snapshotVersion)
        setGradleProperties(gradlePropsFile, allowSonatypeSnapshots = false, allowLocalSnapshots = true)
    }
}
