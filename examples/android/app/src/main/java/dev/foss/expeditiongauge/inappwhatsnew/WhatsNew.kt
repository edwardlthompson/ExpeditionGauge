package dev.foss.expeditiongauge.inappwhatsnew

/** In-app What’s new after a version bump. */
object WhatsNew {
    const val CURRENT = "2.18.12"

    fun shouldShow(seen: String?): Boolean = seen != CURRENT

    fun lines(): List<String> = listOf(
        "Sanitized privacy-report export",
        "Settings backup and local QR transfer",
        "F-Droid reproducible listing",
    )

    fun body(): String = lines().joinToString("\n") { "• $it" }
}
