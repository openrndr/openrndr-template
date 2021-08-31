import org.gradle.api.Project

class Dependency(
    project: Project,
    private val openrndrVersion: String,
    private val orxVersion: String,
    private val ormlVersion: String
) {
    private val openrndrOs = OS.getOsString(project)

    fun orx(module: String) =
        "org.openrndr.extra:$module:$orxVersion"

    fun orml(module: String) =
        "org.openrndr.orml:$module:$ormlVersion"

    fun openrndr(module: String) =
        "org.openrndr:openrndr-$module:$openrndrVersion"

    fun openrndrNatives(module: String) =
        "org.openrndr:openrndr-$module-natives-$openrndrOs:$openrndrVersion"

    fun orxNatives(module: String) =
        "org.openrndr.extra:$module-natives-$openrndrOs:$orxVersion"
}
