package dev.foss.expeditiongauge.predictiveback

/** Routes that still needed a system back callback. */
object PredictiveBack {
    val remaining = listOf(
        "feedback",
        "permissions",
        "session_comparison",
        "session_metadata_edit",
        "playback",
    )

    fun covers(route: String): Boolean = route in remaining
}
