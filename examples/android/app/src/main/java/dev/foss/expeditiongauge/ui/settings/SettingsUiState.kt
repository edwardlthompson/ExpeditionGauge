package dev.foss.expeditiongauge.ui.settings

import dev.foss.expeditiongauge.alerts.AlertAudioMode
import dev.foss.expeditiongauge.alerts.AlertThresholds
import dev.foss.expeditiongauge.gauge.AttitudeGaugeMode
import dev.foss.expeditiongauge.live.LivePairingManager
import dev.foss.expeditiongauge.map.HomeMapRegion
import dev.foss.expeditiongauge.presets.DashboardPresetId
import dev.foss.expeditiongauge.recording.RecordingMode
import dev.foss.expeditiongauge.settings.HudScreenshotMode
import dev.foss.expeditiongauge.settings.MediaCompressionQuality
import dev.foss.expeditiongauge.settings.ObdPidConfig
import dev.foss.expeditiongauge.settings.PressureUnit
import dev.foss.expeditiongauge.settings.SpeedUnit
import dev.foss.expeditiongauge.settings.TempUnit
import dev.foss.expeditiongauge.ui.theme.BrightnessMode
import dev.foss.expeditiongauge.ui.theme.ThemeMode

data class SettingsUiState(
    val themeMode: ThemeMode = ThemeMode.System,
    val updateCheckEnabled: Boolean = false,
    val speedUnit: SpeedUnit = SpeedUnit.METRIC,
    val logIntervalMs: Long = 20L,
    val obdDevices: List<Pair<String, String>> = emptyList(),
    val externalGpsDevices: List<Pair<String, String>> = emptyList(),
    val selectedObdAddress: String? = null,
    val selectedExternalGpsAddress: String? = null,
    val obdConnectionStatus: String? = null,
    val autoCalibrateWhenStill: Boolean = true,
    val developerModeEnabled: Boolean = false,
    val obdPidConfig: ObdPidConfig = ObdPidConfig(),
    val externalGpsEnabled: Boolean = false,
    val tpmsEnabled: Boolean = false,
    val pressureUnit: PressureUnit = PressureUnit.PSI,
    val tempUnit: TempUnit = TempUnit.CELSIUS,
    val highContrastEnabled: Boolean = false,
    val largeTextEnabled: Boolean = false,
    val ttsReadoutEnabled: Boolean = false,
    val liveTelemetryEnabled: Boolean = false,
    val liveSignalWssUrl: String = LivePairingManager.DEFAULT_SIGNAL_WSS,
    val audibleTonesEnabled: Boolean = false,
    val homeMapRegion: HomeMapRegion? = null,
    val mediaCompressionQuality: MediaCompressionQuality = MediaCompressionQuality.BALANCED,
    val mediaStorageBytes: Long = 0L,
    val autoRecordEnabled: Boolean = false,
    val autoRecordDeviceAddresses: Set<String> = emptySet(),
    val sessionStoragePercent: Int = 25,
    val sessionStorageUsedBytes: Long = 0L,
    val sessionStorageAllowedBytes: Long = 0L,
    val brightnessMode: BrightnessMode = BrightnessMode.Auto,
    val keepScreenAwake: Boolean = true,
    val recordingMode: RecordingMode = RecordingMode.NORMAL,
    val lapTimingEnabled: Boolean = false,
    val attitudeGaugeMode: AttitudeGaugeMode = AttitudeGaugeMode.G_FORCE,
    val alertThresholds: AlertThresholds = AlertThresholds(),
    val alertAudioMode: AlertAudioMode = AlertAudioMode.BEEP,
    val alertsMuted: Boolean = false,
    val activePresetId: DashboardPresetId = DashboardPresetId.Default,
    val screenshotMode: HudScreenshotMode = HudScreenshotMode.FULL_SCREEN,
)
