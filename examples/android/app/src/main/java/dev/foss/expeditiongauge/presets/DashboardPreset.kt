package dev.foss.expeditiongauge.presets

import dev.foss.expeditiongauge.recording.RecordingMode

enum class DashboardPresetId {
    Default,
    Drift,
    Offroad,
    Track,
    Minimal,
}

data class PanelWeights(
    val attitude: Float = 1f,
    val center: Float = 1f,
    val side: Float = 1f,
)

data class DashboardPreset(
    val id: DashboardPresetId,
    val recordingMode: RecordingMode = RecordingMode.NORMAL,
    val weights: PanelWeights = PanelWeights(),
    val showAttitude: Boolean = true,
    val showSpeed: Boolean = true,
    val showHeading: Boolean = true,
    val showGps: Boolean = true,
    val showTirePressure: Boolean = true,
    val showDriftAngle: Boolean = false,
    val emphasizeDrift: Boolean = false,
) {
    companion object {
        val Default = DashboardPreset(
            id = DashboardPresetId.Default,
            recordingMode = RecordingMode.NORMAL,
            weights = PanelWeights(1f, 1f, 1f),
            showDriftAngle = true,
        )

        val Drift = DashboardPreset(
            id = DashboardPresetId.Drift,
            recordingMode = RecordingMode.NORMAL,
            weights = PanelWeights(0.6f, 1.4f, 0.8f),
            showDriftAngle = true,
            emphasizeDrift = true,
        )

        val Offroad = DashboardPreset(
            id = DashboardPresetId.Offroad,
            recordingMode = RecordingMode.CRAWLING,
            weights = PanelWeights(2f, 0.8f, 0.5f),
            showDriftAngle = false,
        )

        val Track = DashboardPreset(
            id = DashboardPresetId.Track,
            recordingMode = RecordingMode.NORMAL,
            weights = PanelWeights(0.8f, 1.5f, 0.8f),
            showDriftAngle = true,
        )

        val Minimal = DashboardPreset(
            id = DashboardPresetId.Minimal,
            recordingMode = RecordingMode.NORMAL,
            weights = PanelWeights(0f, 2f, 0f),
            showAttitude = false,
            showHeading = false,
            showGps = false,
            showTirePressure = false,
            showDriftAngle = false,
        )

        fun fromId(id: DashboardPresetId): DashboardPreset = when (id) {
            DashboardPresetId.Default -> Default
            DashboardPresetId.Drift -> Drift
            DashboardPresetId.Offroad -> Offroad
            DashboardPresetId.Track -> Track
            DashboardPresetId.Minimal -> Minimal
        }

        val all: List<DashboardPreset> = listOf(Default, Drift, Offroad, Track, Minimal)
    }
}
