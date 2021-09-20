object Versions {
    const val kotlin = "1.5.30"

    private const val snapshotVersion = "0.5.1-SNAPSHOT"

    var openrndr = "0.3.58"
    var orx = "0.3.58"
    var orml = "0.3.0-rc.5"

    var openrndrUseSnapshot: Boolean = false
        set(value) {
            field = value
            if (value) openrndr = snapshotVersion
        }

    var orxUseSnapshot: Boolean = false
        set(value) {
            field = value
            if (value) orx = snapshotVersion
        }

    var ormlUseSnapshot: Boolean = false
        set(value) {
            field = value
            if (value) orml = snapshotVersion
        }

    fun usesSnapshot() = openrndrUseSnapshot || orxUseSnapshot || ormlUseSnapshot
}