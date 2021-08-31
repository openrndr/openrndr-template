import org.gradle.api.Project
import org.gradle.internal.os.OperatingSystem
import org.gradle.nativeplatform.platform.internal.DefaultNativePlatform

object OS {
    private val supportedPlatforms = setOf("windows", "macos", "linux-x64", "linux-arm64")

    fun isMac() = OperatingSystem.current() == OperatingSystem.MAC_OS

    // Returns one of the `supportPlatforms` or throws an exception
    fun getOsString(project: Project) = if (project.hasProperty("targetPlatform")) {
        val platform: String = project.property("targetPlatform") as String
        if (platform !in supportedPlatforms) {
            throw IllegalArgumentException("target platform not supported: $platform")
        } else {
            platform
        }
    } else when (OperatingSystem.current()) {
        OperatingSystem.WINDOWS -> "windows"
        OperatingSystem.MAC_OS -> "macos"
        OperatingSystem.LINUX -> when (val arch =
            DefaultNativePlatform("current").architecture.name) {
            "x86-64" -> "linux-x64"
            "aarch64" -> "linux-arm64"
            else -> throw IllegalArgumentException("architecture not supported: $arch")
        }
        else -> throw IllegalArgumentException("os not supported")
    }
}