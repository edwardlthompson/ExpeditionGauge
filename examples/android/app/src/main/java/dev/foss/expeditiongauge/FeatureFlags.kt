package dev.foss.expeditiongauge

/**
 * Runtime feature toggles synced from [project.config.json] at build time.
 * All optional hardware paths default off for phone-only core v1.
 * Polish wave 1 (Sprints 9–14) flags default on when wave1_polish is enabled.
 */
object FeatureFlags {
    const val wave1PolishEnabled: Boolean = true
    const val wave2PolishEnabled: Boolean = true
    const val tpmsEnabled: Boolean = false
    const val externalGpsEnabled: Boolean = false
    var liveTelemetryEnabled: Boolean = false

    const val sessionMetadataEnabled: Boolean = wave1PolishEnabled
    const val crawlingModeEnabled: Boolean = wave1PolishEnabled
    const val lapTimingEnabled: Boolean = wave1PolishEnabled
    const val telemetryGraphsEnabled: Boolean = wave1PolishEnabled
    const val heatmapOverlayEnabled: Boolean = wave1PolishEnabled
    const val drivingLineEnabled: Boolean = wave1PolishEnabled
    const val ghostLapEnabled: Boolean = wave1PolishEnabled
    const val alertsEnabled: Boolean = wave1PolishEnabled
    const val lapTimerStripEnabled: Boolean = false
}
