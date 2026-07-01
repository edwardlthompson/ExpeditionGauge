package dev.foss.expeditiongauge.flyover

data class FlyoverVideoExportSettings(
    val clipDurationMs: Long = 30_000L,
    val frameRate: Int = 10,
    val width: Int = MapLibreFlyoverRenderer.DEFAULT_WIDTH,
    val height: Int = MapLibreFlyoverRenderer.DEFAULT_HEIGHT,
    val enhancedOverlay: Boolean = true,
) {
    companion object {
        val PRESET_15S = FlyoverVideoExportSettings(clipDurationMs = 15_000L)
        val PRESET_30S = FlyoverVideoExportSettings(clipDurationMs = 30_000L)
        val PRESET_60S = FlyoverVideoExportSettings(clipDurationMs = 60_000L)
    }
}
