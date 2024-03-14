package org.openrndr.template

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.ProjectEvaluationListener
import org.gradle.api.ProjectState
import org.gradle.api.artifacts.DependencyResolutionListener
import org.gradle.api.artifacts.MinimalExternalModuleDependency
import org.gradle.api.artifacts.ResolvableDependencies
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.api.provider.SetProperty
import org.gradle.internal.os.OperatingSystem
import org.gradle.nativeplatform.platform.internal.DefaultNativePlatform
import java.io.File

interface OpenrndrPluginExtension {
    val openrndrVersion: Property<String>
    val orxVersion: Property<String>
    val openrndrFeatures: SetProperty<Provider<MinimalExternalModuleDependency>>
    val orxFeatures: SetProperty<Provider<MinimalExternalModuleDependency>>
    val mainClass: Property<String>
}

class OpenrndrPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        val extension = target.extensions.create("openrndr", OpenrndrPluginExtension::class.java)

        // Install the convention plugins
        run {
            target.plugins.apply("org.openrndr.template.convention.openrndr-jvm")
            target.plugins.apply("org.openrndr.template.convention.distribution-jvm")
        }

        // Configure the application mainClass
        run {
            val app =
                (target as org.gradle.api.plugins.ExtensionAware).extensions.getByName("application") as org.gradle.api.plugins.JavaApplication
            app.mainClass.set(extension.mainClass)
        }

        // Install the OPENRNDR tasks
        run {
            target.gradle.addListener(object : ProjectEvaluationListener {
                override fun beforeEvaluate(project: Project) {
                }

                override fun afterEvaluate(project: Project, state: ProjectState) {
                    if (target.properties["openrndr.tasks"] == "true") {
                        target.task("create executable jar for ${extension.mainClass.get()}") {
                            group = " \uD83E\uDD8C OPENRNDR"
                            dependsOn("shadowJar")
                        }

                        target.task("run $${extension.mainClass.get()}") {
                            group = " \uD83E\uDD8C OPENRNDR"
                            dependsOn("run")
                        }

                        target.task("create standalone executable for ${extension.mainClass.get()}") {
                            group = " \uD83E\uDD8C OPENRNDR"
                            dependsOn("jpackageZip")
                        }

                        target.task("add IDE file scopes") {
                            group = " \uD83E\uDD8C OPENRNDR"
                            val scopesFolder = File("${project.projectDir}/.idea/scopes")
                            scopesFolder.mkdirs()

                            val files = listOf(
                                "Code" to "file:*.kt||file:*.frag||file:*.vert||file:*.glsl",
                                "Text" to "file:*.txt||file:*.md||file:*.xml||file:*.json",
                                "Gradle" to "file[*buildSrc*]:*/||file:*gradle.*||file:*.gradle||file:*/gradle-wrapper.properties||file:*.toml",
                                "Media" to "file:*.png||file:*.jpg||file:*.dds||file:*.exr||file:*.mp3||file:*.wav||file:*.mp4||file:*.mov||file:*.svg"
                            )
                            files.forEach { (name, pattern) ->
                                val file = File(scopesFolder, "__$name.xml")
                                if (!file.exists()) {
                                    file.writeText(
                                        """
                    <component name="DependencyValidationManager">
                      <scope name=" ★ $name" pattern="$pattern" />
                    </component>
                    """.trimIndent()
                                    )
                                }
                            }
                        }
                    }
                }
            })
        }

        // Dynamically configure dependencies from openrndrFeatures and orxFeatures
        run {
            val currArch = DefaultNativePlatform("current").architecture.name
            val currOs = OperatingSystem.current()
            val os = if (target.hasProperty("targetPlatform")) {
                val supportedPlatforms = setOf("windows", "macos", "macos-arm64", "linux-x64", "linux-arm64")
                val platform: String = target.property("targetPlatform") as String
                if (platform !in supportedPlatforms) {
                    throw IllegalArgumentException("target platform not supported: $platform")
                } else {
                    platform
                }
            } else when {
                currOs.isWindows -> "windows"
                currOs.isMacOsX -> when (currArch) {
                    "aarch64", "arm-v8" -> "macos-arm64"
                    else -> "macos"
                }
                currOs.isLinux -> when (currArch) {
                    "x86-64" -> "linux-x64"
                    "aarch64" -> "linux-arm64"
                    else -> throw IllegalArgumentException("architecture not supported: $currArch")
                }
                else -> throw IllegalArgumentException("os not supported: ${currOs.name}")
            }


            target.gradle.addListener(object : DependencyResolutionListener {
                override fun beforeResolve(dependencies: ResolvableDependencies) {
                    for (feature in extension.orxFeatures.get()) {
                        target.dependencies.add("implementation", feature)
                    }

                    for (feature in extension.openrndrFeatures.get()) {
                        target.dependencies.add("implementation", feature)
                        val dep = feature.get()
                        when (dep.name) {
                            "openrndr-ffmpeg" -> target.dependencies.add(
                                "runtimeOnly",
                                "${dep.group}:${dep.name}-natives-$os:${dep.version}"
                            )

                            "openrndr-gl3" -> target.dependencies.add(
                                "runtimeOnly",
                                "${dep.group}:${dep.name}-natives-$os:${dep.version}"
                            )
                        }
                    }

                    for (feature in extension.orxFeatures.get()) {
                        target.dependencies.add("implementation", feature)
                        val dep = feature.get()
                        when (dep.name) {
                            "orx-kinect-v1" -> target.dependencies.add(
                                "runtimeOnly",
                                "${dep.group}:${dep.name}-natives-$os:${dep.version}"
                            )
                        }
                    }

                    target.gradle.removeListener(this)
                }

                override fun afterResolve(dependencies: ResolvableDependencies) {
                }

            })
        }
    }
}