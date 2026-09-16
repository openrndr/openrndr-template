package conventions

import java.io.BufferedReader

private fun isWindows(): Boolean {
    return System.getProperty("os.name").lowercase().contains("windows")
}

private fun executeCommand(vararg command: String): String {
    val processBuilder = if (isWindows()) {
        ProcessBuilder("cmd", "/c", *command)
    } else {
        ProcessBuilder(*command)
    }

    val process = processBuilder
        .redirectErrorStream(true)
        .start()

    val output = process.inputStream.bufferedReader().use(BufferedReader::readText)
    process.waitFor()
    return output.trim()
}

private fun getLatestVersion(
    tagPattern: Regex = Regex("""refs/tags/v(\d+\.\d+\.\d+-SNAPSHOT\.\d+)"""),
    releasePattern: Regex = Regex("""(\d+\.\d+\.\d+)-SNAPSHOT\.\d+""")
): String {
    try {
        // Fetch tags from the repository
        val tagsOutput = executeCommand("git", "ls-remote", "--tags", "https://github.com/openrndr/openrndr")

        val versions: List<String> = tagsOutput.lines()
            .mapNotNull { line -> tagPattern.find(line)?.groupValues?.get(1) }

        val sortedVersions: List<String> =
            versions.sortedByDescending { version ->
                val parts: List<String> = version.replace("-SNAPSHOT.", ".").split(".")
                val intParts: List<Int> = parts.map { it.toIntOrNull() ?: 0 }
                val sum =
                    (intParts.getOrNull(0) ?: 0) * 1000000 + (intParts.getOrNull(1) ?: 0) * 10000 + (intParts.getOrNull(
                        2
                    ) ?: 0) * 100 + (intParts.getOrNull(3) ?: 0)
                sum
            }

        val candidate = sortedVersions.firstOrNull()

        val extractedVersion = candidate?.let {
            releasePattern.find(it)?.groupValues?.get(1)
        }

        return extractedVersion ?: error("no version found matching pattern $releasePattern")
    } finally {

    }
}

/**
 * Update gradle/libs.versions.toml
 */
private fun setVersion(version: String) {
    val libsVersionsFile = project.rootProject.file("gradle/libs.versions.toml")
    if (libsVersionsFile.exists()) {
        var content = libsVersionsFile.readText()
        content = content.replace(
            Regex("""(openrndr\s*=\s*")[^"]*""""),
            "$1$version\""
        )
        content = content.replace(
            Regex("""(orx\s*=\s*")[^"]*""""),
            "$1$version\""
        )
        libsVersionsFile.writeText(content)
    }
}

/**
 * Update gradle.properties
 */
private fun setGradleProperties(allowSonatypeSnapshots: Boolean, allowLocalSnapshots: Boolean) {
    // Update gradle.properties
    val gradlePropsFile = project.rootProject.file("gradle.properties")
    if (gradlePropsFile.exists()) {
        var content = gradlePropsFile.readText()
        content = content.replace(
            Regex("""openrndr\.allowSonatypeSnapshots\s*=\s*\w+"""),
            "openrndr.allowSonatypeSnapshots=$allowSonatypeSnapshots"
        )
        content = content.replace(
            Regex("""openrndr\.allowLocalSnapshots\s*=\s*\w+"""),
            "openrndr.allowLocalSnapshots=$allowLocalSnapshots"
        )
        gradlePropsFile.writeText(content)
    }
}


tasks.register("openrndrSonatypeSnapshot") {
    group = "openrndr template"
    description = "Switch to OPENRNDR and ORX Sonatype snapshot versions"

    doLast {
        val snapshotVersion = getLatestVersion()
        setVersion(snapshotVersion)
        setGradleProperties(true, false)
    }
}

tasks.register("openrndrRelease") {
    group = "openrndr template"
    description = "Switch to OPENRNDR and ORX Sonatype snapshot versions"

    doLast {
        val releaseVersion = getLatestVersion(
            Regex("""refs/tags/v(\d+\.\d+\.\d+)"""),
            Regex("""(\d+\.\d+\.\d+)""")
        )
        setVersion(releaseVersion)
        setGradleProperties(false, false)
    }
}

tasks.register("openrndrLocalSnapshot") {
    group = "openrndr template"
    description = "Switch to OPENRNDR and ORX Sonatype snapshot versions"

    doLast {
        val snapshotVersion = getLatestVersion()

        setVersion(snapshotVersion)
        setGradleProperties(false, true)
    }
}