package dev.foss.expeditiongauge.aaosstandalone

/** Sideload identity for the AAOS-only APK (`-PaaosStandalone=true`). */
object AaosStandalone {
    const val ID_SUFFIX = ".aaos"
    const val VERSION_SUFFIX = "-aaos"
    const val GRADLE_PROPERTY = "aaosStandalone"

    fun applicationId(base: String, standalone: Boolean): String =
        if (standalone) "$base$ID_SUFFIX" else base

    fun versionName(base: String, standalone: Boolean): String =
        if (standalone) "$base$VERSION_SUFFIX" else base

    fun automotiveRequired(standalone: Boolean): Boolean = standalone
}
