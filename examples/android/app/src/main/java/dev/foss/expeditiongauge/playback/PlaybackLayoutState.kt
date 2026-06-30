package dev.foss.expeditiongauge.playback

data class PlaybackLayoutState(
    val mapWeight: Float = 0.6f,
    val gaugesWeight: Float = 0.4f,
    val graphsExpanded: Boolean = false,
    val graphsWeight: Float = 0.25f,
) {
    fun withMapWeight(weight: Float): PlaybackLayoutState = copy(
        mapWeight = weight.coerceIn(0.2f, 0.8f),
        gaugesWeight = (1f - weight.coerceIn(0.2f, 0.8f)),
    )

    companion object {
        const val PREFS_KEY = "playback_layout_weights"
    }
}
