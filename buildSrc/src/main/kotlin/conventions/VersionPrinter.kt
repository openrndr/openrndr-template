package conventions

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.flow.FlowAction
import org.gradle.api.flow.FlowParameters
import org.gradle.api.flow.FlowScope
import org.gradle.api.logging.Logging
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import java.io.File
import java.util.Properties
import javax.inject.Inject

/**
 * Prints the active OPENRNDR and ORX versions at the end of every build.
 *
 * FlowScope.always() is the configuration-cache-compatible replacement for
 * the deprecated `gradle.buildFinished` hook: it fires once, at the very end
 * of every build (cache hit or not), regardless of build outcome.
 *
 * The files are read when the action runs, not when the build is configured,
 * so after one of the openrndr* version switching tasks the new versions are shown.
 */
abstract class VersionPrinterPlugin : Plugin<Project> {
    @get:Inject
    abstract val flowScope: FlowScope

    override fun apply(project: Project) {
        val settingsDirectory = project.layout.settingsDirectory
        flowScope.always(VersionPrinterFlowAction::class.java) {
            parameters.libsVersionsPath.set(settingsDirectory.file("gradle/libs.versions.toml").asFile.absolutePath)
            parameters.gradlePropertiesPath.set(settingsDirectory.file("gradle.properties").asFile.absolutePath)
        }
    }
}

abstract class VersionPrinterFlowAction : FlowAction<VersionPrinterFlowAction.Parameters> {
    // Plain paths: the files are only read when the action runs, their contents are not inputs
    interface Parameters : FlowParameters {
        @get:Input
        val libsVersionsPath: Property<String>

        @get:Input
        val gradlePropertiesPath: Property<String>
    }

    override fun execute(parameters: Parameters) {
        val catalog = File(parameters.libsVersionsPath.get()).readText()
        val properties = Properties().apply {
            File(parameters.gradlePropertiesPath.get()).reader().use { load(it) }
        }

        fun version(name: String) =
            Regex("""(?m)^\s*$name\s*=\s*"([^"]*)"""").find(catalog)?.groupValues?.get(1) ?: "unknown"

        fun checkbox(property: String) =
            if (properties.getProperty(property) == "true") "x" else " "

        Logging.getLogger(VersionPrinterFlowAction::class.java).lifecycle("""

            openrndr = "${version("openrndr")}"
            orx = "${version("orx")}"

            [${checkbox("openrndr.allowLocalSnapshots")}] Use local snapshots
            [${checkbox("openrndr.allowSonatypeSnapshots")}] Use Sonatype snapshots
            """.trimIndent())
    }
}
