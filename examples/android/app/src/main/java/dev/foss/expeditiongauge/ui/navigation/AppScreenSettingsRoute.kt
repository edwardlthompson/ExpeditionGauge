package dev.foss.expeditiongauge.ui.navigation

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.foss.expeditiongauge.ExpeditionGaugeServices
import dev.foss.expeditiongauge.alerts.AlertThresholds
import dev.foss.expeditiongauge.gauge.AttitudeGaugeMode
import dev.foss.expeditiongauge.presets.DashboardPresetId
import dev.foss.expeditiongauge.settings.HudScreenshotMode
import dev.foss.expeditiongauge.settings.MediaCompressionQuality
import dev.foss.expeditiongauge.settings.PressureUnit
import dev.foss.expeditiongauge.settings.SpeedUnit
import dev.foss.expeditiongauge.settings.TempUnit
import dev.foss.expeditiongauge.ui.AppScreen
import dev.foss.expeditiongauge.ui.theme.BrightnessMode
import dev.foss.expeditiongauge.ui.theme.ThemeMode
import kotlinx.coroutines.CoroutineScope

@Composable
fun AppScreenSettingsRoute(
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
    alertAudioMode: dev.foss.expeditiongauge.alerts.AlertAudioMode =
        dev.foss.expeditiongauge.alerts.AlertAudioMode.BEEP,
    onAlertAudioModeChange: (dev.foss.expeditiongauge.alerts.AlertAudioMode) -> Unit = {},
    alertsMuted: Boolean = false,
    onAlertsMutedChange: (Boolean) -> Unit = {},
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
    val developerModeEnabled by services.settingsPreferences.developerModeEnabled
        .collectAsStateWithLifecycle(initialValue = false)
    val homeMapRegion by services.homeMapRegionPreferences.region
        .collectAsStateWithLifecycle(initialValue = null)
    val mediaCompressionQuality by services.settingsPreferences.mediaCompressionQuality
        .collectAsStateWithLifecycle(initialValue = MediaCompressionQuality.BALANCED)
    var mediaStorageBytes by remember { mutableLongStateOf(0L) }
    var sessionStorageUsed by remember { mutableLongStateOf(0L) }
    var sessionStorageAllowed by remember { mutableLongStateOf(0L) }
    val autoRecordEnabled by services.settingsPreferences.autoRecordEnabled
        .collectAsStateWithLifecycle(initialValue = false)
    val autoRecordDevices by services.settingsPreferences.autoRecordDeviceAddresses
        .collectAsStateWithLifecycle(initialValue = emptySet())
    val sessionStoragePercent by services.settingsPreferences.sessionStorageFreePercent
        .collectAsStateWithLifecycle(initialValue = 25)
    val autoCalibrateWhenStill by services.settingsPreferences.autoCalibrateWhenStill
        .collectAsStateWithLifecycle(initialValue = true)
    val screenshotMode by services.settingsPreferences.hudScreenshotMode
        .collectAsStateWithLifecycle(initialValue = HudScreenshotMode.FULL_SCREEN)
    LaunchedEffect(Unit) {
        mediaStorageBytes = services.sessionMediaRepository.totalStorageBytes()
        sessionStorageUsed = services.sessionStorageBudget.usedBytes()
        sessionStorageAllowed = services.sessionStorageBudget.allowedBytes()
    }
    val obd = rememberAppScreenSettingsObdWiring(context, scope, services, obdAddress)
    AppScreenSettingsForm(
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
        developerModeEnabled = developerModeEnabled,
        homeMapRegion = homeMapRegion,
        mediaCompressionQuality = mediaCompressionQuality,
        mediaStorageBytes = mediaStorageBytes,
        autoRecordEnabled = autoRecordEnabled,
        autoRecordDevices = autoRecordDevices,
        sessionStoragePercent = sessionStoragePercent,
        sessionStorageUsed = sessionStorageUsed,
        sessionStorageAllowed = sessionStorageAllowed,
        onSessionStorageAllowedChange = { sessionStorageAllowed = it },
        autoCalibrateWhenStill = autoCalibrateWhenStill,
        screenshotMode = screenshotMode,
        obd = obd,
    )
}
