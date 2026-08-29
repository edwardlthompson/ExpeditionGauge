package dev.foss.expeditiongauge.ui.settings

import dev.foss.expeditiongauge.alerts.AlertAudioMode
import dev.foss.expeditiongauge.alerts.AlertThresholds
import dev.foss.expeditiongauge.gauge.AttitudeGaugeMode
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

data class SettingsUiActions(
    val onBack: () -> Unit,
    val onThemeModeSelect: (ThemeMode) -> Unit = {},
    val onUpdateCheckChange: (Boolean) -> Unit = {},
    val onSpeedUnitSelect: (SpeedUnit) -> Unit = {},
    val onLogIntervalSelect: (Long) -> Unit = {},
    val onObdDeviceSelect: (String) -> Unit = {},
    val onObdRetry: () -> Unit = {},
    val onForgetObd: () -> Unit = {},
    val onObdPairNew: () -> Unit = {},
    val onExternalGpsSelect: (String) -> Unit = {},
    val onImuManage: () -> Unit = {},
    val onCalibrationReset: () -> Unit = {},
    val onCalibrationTips: () -> Unit = {},
    val onCalibrationWizard: () -> Unit = {},
    val onAutoCalibrateWhenStillChange: (Boolean) -> Unit = {},
    val onDeveloperModeChange: (Boolean) -> Unit = {},
    val onDeveloperModeOpen: () -> Unit = {},
    val onObdPidConfigChange: (ObdPidConfig) -> Unit = {},
    val onPidDiscover: () -> Unit = {},
    val onExternalGpsEnabledChange: (Boolean) -> Unit = {},
    val onForgetExternalGps: () -> Unit = {},
    val onTpmsEnabledChange: (Boolean) -> Unit = {},
    val onTpmsManage: () -> Unit = {},
    val onPressureUnitSelect: (PressureUnit) -> Unit = {},
    val onTempUnitSelect: (TempUnit) -> Unit = {},
    val onHighContrastChange: (Boolean) -> Unit = {},
    val onLargeTextChange: (Boolean) -> Unit = {},
    val onTtsReadoutChange: (Boolean) -> Unit = {},
    val onLiveTelemetryChange: (Boolean) -> Unit = {},
    val onLiveSignalWssUrlChange: (String) -> Unit = {},
    val onLiveReceiverOpen: () -> Unit = {},
    val onAudibleTonesChange: (Boolean) -> Unit = {},
    val onUseCurrentLocationAsHomeRegion: () -> Unit = {},
    val onMediaCompressionSelect: (MediaCompressionQuality) -> Unit = {},
    val onAutoRecordEnabledChange: (Boolean) -> Unit = {},
    val onAutoRecordDeviceToggle: (String, Boolean) -> Unit = { _, _ -> },
    val onSessionStoragePercentChange: (Int) -> Unit = {},
    val onBrightnessModeSelect: (BrightnessMode) -> Unit = {},
    val onKeepScreenAwakeChange: (Boolean) -> Unit = {},
    val onRecordingModeSelect: (RecordingMode) -> Unit = {},
    val onLapTimingEnabledChange: (Boolean) -> Unit = {},
    val onTrackSetup: () -> Unit = {},
    val onAttitudeGaugeModeSelect: (AttitudeGaugeMode) -> Unit = {},
    val onAlertAudioModeChange: (AlertAudioMode) -> Unit = {},
    val onAlertsMutedChange: (Boolean) -> Unit = {},
    val onAlertThresholdsChange: (AlertThresholds) -> Unit = {},
    val onPresetSelected: (DashboardPresetId) -> Unit = {},
    val onScreenshotModeSelected: (HudScreenshotMode) -> Unit = {},
    val onAboutOpen: () -> Unit = {},
)
