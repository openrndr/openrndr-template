import org.gradle.api.Project

class OpenrndrDependencies(project: Project) {
    lateinit var orxFeatures: List<String>
    lateinit var ormlFeatures: List<String>
    lateinit var openrndrFeatures: List<String>
    lateinit var orxTensorflowBackend: String

    private val openrndrOs = OS.getOsString(project)

    private fun openrndr(module: String) =
        "org.openrndr:openrndr-$module:${Versions.openrndr}"

    private fun openrndrNatives(module: String) =
        "org.openrndr:openrndr-$module-natives-$openrndrOs:${Versions.openrndr}"

    private fun orxNatives(module: String) =
        "org.openrndr.extra:$module-natives-$openrndrOs:${Versions.orx}"

    fun runtimeOnly() = listOfNotNull(
        openrndr("gl3"),
        openrndrNatives("gl3"),
        openrndrNatives("openal"),

        if ("video" in openrndrFeatures)
            openrndrNatives("ffmpeg")
        else null,

        if ("orx-tensorflow" in orxFeatures)
            orxNatives(orxTensorflowBackend)
        else null,

        if ("orx-kinect-v1" in orxFeatures)
            orxNatives("orx-kinect-v1")
        else null,
    )

    fun implementation() = listOfNotNull(
        openrndr("openal"),
        openrndr("core"),
        openrndr("svg"),
        openrndr("animatable"),
        openrndr("extensions"),
        openrndr("filter"),
        "org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.0",
        "io.github.microutils:kotlin-logging-jvm:2.0.6",

        if ("orx-olive" in orxFeatures)
            "org.jetbrains.kotlin:kotlin-script-runtime:${Versions.kotlin}"
        else null,

        if ("video" in openrndrFeatures)
            openrndr("ffmpeg")
        else null,
    ) +
            orxFeatures.map { "org.openrndr.extra:$it:${Versions.orx}" } +
            ormlFeatures.map { "org.openrndr.orml:$it:${Versions.orml}" }
}
