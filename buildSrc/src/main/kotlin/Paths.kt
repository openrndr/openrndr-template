object Paths {
    fun jpackageData() = if (OS.isMac())
        "build/jpackage/openrndr-application.app/data" else
        "build/jpackage/openrndr-application/data"
}