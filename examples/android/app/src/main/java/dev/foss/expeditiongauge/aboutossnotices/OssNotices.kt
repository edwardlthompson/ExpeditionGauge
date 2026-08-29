package dev.foss.expeditiongauge.aboutossnotices

/** Required third-party notices shown on About. */
object OssNotices {
    val required = listOf("MIT", "MapLibre", "AndroidX")

    fun complete(listed: List<String>): Boolean =
        required.all { req -> listed.any { it.contains(req, ignoreCase = true) } }

    fun summary(): String = required.joinToString(" · ")
}
