package conventions

import java.io.BufferedReader
import java.io.File

// These helpers live in a plain Kotlin file rather than in template-tasks.gradle.kts:
// top-level functions in a precompiled script plugin are members of the script class,
// so calling them from a task action captures the script object, which the
// configuration cache cannot serialize.

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

internal fun getLatestVersion(
    tagPattern: Regex = Regex("""refs/tags/v(\d+\.\d+\.\d+-SNAPSHOT\.\d+)"""),
    releasePattern: Regex = Regex("""(\d+\.\d+\.\d+)-SNAPSHOT\.\d+""")
): String {
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
}

/**
 * Update gradle/libs.versions.toml
 */
internal fun setOpenrndrVersion(libsVersionsFile: File, version: String) {
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
internal fun setGradleProperties(gradlePropsFile: File, allowSonatypeSnapshots: Boolean, allowLocalSnapshots: Boolean) {
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
