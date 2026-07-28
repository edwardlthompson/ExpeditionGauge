package dev.foss.expeditiongauge.ui.navigation

import android.content.Context
import androidx.compose.runtime.Composable
import dev.foss.expeditiongauge.ExpeditionGaugeServices
import dev.foss.expeditiongauge.alerts.AlertAudioMode
import dev.foss.expeditiongauge.alerts.AlertThresholds
import dev.foss.expeditiongauge.gauge.AttitudeGaugeMode
import dev.foss.expeditiongauge.presets.DashboardPresetId
import dev.foss.expeditiongauge.settings.PressureUnit
import dev.foss.expeditiongauge.settings.SpeedUnit
import dev.foss.expeditiongauge.settings.TempUnit
import dev.foss.expeditiongauge.ui.AppScreen
import dev.foss.expeditiongauge.ui.theme.BrightnessMode
import dev.foss.expeditiongauge.ui.theme.ThemeMode
import kotlinx.coroutines.CoroutineScope

@Composable
internal fun AppScreenRouterSettings(
    context: Context,
    onScreenChange: (AppScreen) -> Unit,
    scope: CoroutineScope,
    services: ExpeditionGaugeServices,
    themeMode: ThemeMode,
    checkInterval: String,
    highContrast: Boolean,
    largeTextEnabled: Boolean,
    ttsReadoutEnabled: Boolean,
    liveTelemetryEnabled: Boolean,
    audibleTonesEnabled: Boolean,
    speedUnit: SpeedUnit,
    logInterval: Long,
    obdAddress: String?,
    obdPidConfig: dev.foss.expeditiongauge.settings.ObdPidConfig,
    externalGpsAddress: String?,
    externalGpsEnabled: Boolean,
    tpmsEnabled: Boolean,
    pressureUnit: PressureUnit,
    tempUnit: TempUnit,
    brightnessMode: BrightnessMode,
    liveSignalWssUrl: String,
    recordingMode: dev.foss.expeditiongauge.recording.RecordingMode,
    lapTimingEnabled: Boolean,
    attitudeGaugeMode: AttitudeGaugeMode,
    alertThresholds: AlertThresholds,
    onAlertThresholdsChange: (AlertThresholds) -> Unit,
    alertAudioMode: AlertAudioMode,
    onAlertAudioModeChange: (AlertAudioMode) -> Unit,
    alertsMuted: Boolean,
    onAlertsMutedChange: (Boolean) -> Unit,
    activePresetId: DashboardPresetId,
    onPresetSelected: (DashboardPresetId) -> Unit,
    onThemeModeSelect: (ThemeMode) -> Unit,
    onUpdateCheckChange: (Boolean) -> Unit,
    onHighContrastChange: (Boolean) -> Unit,
    onLargeTextChange: (Boolean) -> Unit,
    onTtsReadoutChange: (Boolean) -> Unit,
    onLiveTelemetryChange: (Boolean) -> Unit,
    onLiveSignalWssUrlChange: (String) -> Unit,
    onAudibleTonesChange: (Boolean) -> Unit,
    onBrightnessModeSelect: (BrightnessMode) -> Unit,
    keepScreenAwake: Boolean,
    onKeepScreenAwakeChange: (Boolean) -> Unit,
) {
    AppScreenSettingsRoute(
        context = context,
        onScreenChange = onScreenChange,
        scope = scope,
        services = services,
        themeMode = themeMode,
        checkInterval = checkInterval,
        highContrast = highContrast,
        largeTextEnabled = largeTextEnabled,
        ttsReadoutEnabled = ttsReadoutEnabled,
        liveTelemetryEnabled = liveTelemetryEnabled,
        audibleTonesEnabled = audibleTonesEnabled,
        speedUnit = speedUnit,
        logInterval = logInterval,
        obdAddress = obdAddress,
        obdPidConfig = obdPidConfig,
        externalGpsAddress = externalGpsAddress,
        externalGpsEnabled = externalGpsEnabled,
        tpmsEnabled = tpmsEnabled,
        pressureUnit = pressureUnit,
        tempUnit = tempUnit,
        brightnessMode = brightnessMode,
        liveSignalWssUrl = liveSignalWssUrl,
        recordingMode = recordingMode,
        lapTimingEnabled = lapTimingEnabled,
        attitudeGaugeMode = attitudeGaugeMode,
        alertThresholds = alertThresholds,
        onAlertThresholdsChange = onAlertThresholdsChange,
        alertAudioMode = alertAudioMode,
        onAlertAudioModeChange = onAlertAudioModeChange,
        alertsMuted = alertsMuted,
        onAlertsMutedChange = onAlertsMutedChange,
        activePresetId = activePresetId,
        onPresetSelected = onPresetSelected,
        onThemeModeSelect = onThemeModeSelect,
        onUpdateCheckChange = onUpdateCheckChange,
        onHighContrastChange = onHighContrastChange,
        onLargeTextChange = onLargeTextChange,
        onTtsReadoutChange = onTtsReadoutChange,
        onLiveTelemetryChange = onLiveTelemetryChange,
        onLiveSignalWssUrlChange = onLiveSignalWssUrlChange,
        onAudibleTonesChange = onAudibleTonesChange,
        onBrightnessModeSelect = onBrightnessModeSelect,
        keepScreenAwake = keepScreenAwake,
        onKeepScreenAwakeChange = onKeepScreenAwakeChange,
    )
}
