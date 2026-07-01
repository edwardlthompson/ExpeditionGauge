package dev.foss.expeditiongauge

/**
 * Runtime feature toggles synced from [project.config.json] at build time.
 * Core v1 (Sprint 8): optional hardware paths default off — phone-only works without BLE/OBD/TPMS/external GPS.
 * Live telemetry remains off until Sprint 19.
 */
object FeatureFlags {
    const val wave1PolishEnabled: Boolean = true
    const val wave2PolishEnabled: Boolean = true
    var tpmsEnabled: Boolean = false
    var externalGpsEnabled: Boolean = false
    var liveTelemetryEnabled: Boolean = false
    var androidAutoEnabled: Boolean = false
    const val androidAutoCapable: Boolean = true

    const val sessionMetadataEnabled: Boolean = wave1PolishEnabled
    const val crawlingModeEnabled: Boolean = wave1PolishEnabled
    const val lapTimingEnabled: Boolean = wave1PolishEnabled
    const val telemetryGraphsEnabled: Boolean = wave1PolishEnabled
    const val heatmapOverlayEnabled: Boolean = wave1PolishEnabled
    const val drivingLineEnabled: Boolean = wave1PolishEnabled
    const val ghostLapEnabled: Boolean = wave1PolishEnabled
    const val alertsEnabled: Boolean = wave1PolishEnabled
    const val lapTimerStripEnabled: Boolean = false
    const val dashboardPresetsEnabled: Boolean = wave2PolishEnabled
    const val playbackLayoutEnabled: Boolean = wave2PolishEnabled
    const val sessionStatsEnabled: Boolean = wave2PolishEnabled
    const val markEventEnabled: Boolean = wave2PolishEnabled
    const val onboardingEnabled: Boolean = wave2PolishEnabled
    const val accessibilityPackEnabled: Boolean = wave2PolishEnabled
    const val videoSyncEnabled: Boolean = true
    const val mediaAttachmentsEnabled: Boolean = true
    const val elevationProfileEnabled: Boolean = true
    const val activityLibraryEnabled: Boolean = true
    const val playbackVideoExportEnabled: Boolean = true
    const val flyover3dEnabled: Boolean = true
    const val sharingPolishEnabled: Boolean = true
    var developerModeEnabled: Boolean = false
}
