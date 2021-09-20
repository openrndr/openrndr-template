import org.gradle.api.Project
import org.gradle.internal.os.OperatingSystem
import org.gradle.nativeplatform.platform.internal.DefaultNativePlatform

object OS {
    private const val WINDOWS = "windows"
    private const val MACOS = "macos"
    private const val LINUX64 = "linux-x64"
    private const val LINUXARM64 = "linux-arm64"

    private val supportedPlatforms =
        setOf(WINDOWS, MACOS, LINUX64, LINUXARM64)

    fun isMac() = OperatingSystem.current().isMacOsX

    // Returns one of the `supportPlatforms` or throws an exception
    fun getOsString(project: Project) =
        if (project.hasProperty("targetPlatform")) {
            val platform = project.property("targetPlatform") as String
            if (platform !in supportedPlatforms) {
                throw IllegalArgumentException("target platform not supported: $platform")
            } else {
                platform
            }
        } else {
            val curr = OperatingSystem.current()
            when {
                curr.isWindows -> WINDOWS
                curr.isMacOsX -> MACOS
                curr.isLinux -> when (val arch =
                    DefaultNativePlatform("current").architecture.name) {
                    "x86-64" -> LINUX64
                    "aarch64" -> LINUXARM64
                    else -> throw IllegalArgumentException("architecture not supported: $arch")
                }
                else -> throw IllegalArgumentException("os not supported")
            }
        }
}