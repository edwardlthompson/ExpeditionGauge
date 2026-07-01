package dev.foss.expeditiongauge.export

data class PlaybackVideoExportSettings(
    val clipDurationMs: Long = 120_000L,
    val frameRate: Int = 10,
    val width: Int = VideoFrameCapturer.DEFAULT_WIDTH,
    val height: Int = VideoFrameCapturer.DEFAULT_HEIGHT,
) {
    init {
        require(clipDurationMs in 5_000L..300_000L)
        require(frameRate in 5..15)
    }

    companion object {
        val PRESET_30S = PlaybackVideoExportSettings(clipDurationMs = 30_000L)
        val PRESET_60S = PlaybackVideoExportSettings(clipDurationMs = 60_000L)
        val PRESET_120S = PlaybackVideoExportSettings(clipDurationMs = 120_000L)
    }
}
