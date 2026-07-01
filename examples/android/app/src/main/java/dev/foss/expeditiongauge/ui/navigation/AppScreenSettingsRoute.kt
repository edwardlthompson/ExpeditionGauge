package dev.foss.expeditiongauge.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.foss.expeditiongauge.ExpeditionGaugeServices
import dev.foss.expeditiongauge.FeatureFlags
import dev.foss.expeditiongauge.alerts.AlertThresholds
import dev.foss.expeditiongauge.gauge.AttitudeGaugeMode
import dev.foss.expeditiongauge.presets.DashboardPresetId
import dev.foss.expeditiongauge.settings.MediaCompressionQuality
import dev.foss.expeditiongauge.settings.PressureUnit
import dev.foss.expeditiongauge.settings.SettingsLogic
import dev.foss.expeditiongauge.settings.SpeedUnit
import dev.foss.expeditiongauge.settings.TempUnit
import dev.foss.expeditiongauge.ui.AppScreen
import dev.foss.expeditiongauge.ui.settings.SettingsScreen
import dev.foss.expeditiongauge.ui.theme.BrightnessMode
import dev.foss.expeditiongauge.ui.theme.ThemeMode
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun AppScreenSettingsRoute(
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
) {
    val developerModeEnabled by services.settingsPreferences.developerModeEnabled
        .collectAsStateWithLifecycle(initialValue = false)
    val androidAutoEnabled by services.settingsPreferences.androidAutoEnabled
        .collectAsStateWithLifecycle(initialValue = false)
    val androidAutoMetrics by services.settingsPreferences.androidAutoMetricAllowlist
        .collectAsStateWithLifecycle(initialValue = emptySet())
    val mediaCompressionQuality by services.settingsPreferences.mediaCompressionQuality
        .collectAsStateWithLifecycle(initialValue = MediaCompressionQuality.BALANCED)
    var mediaStorageBytes by remember { mutableLongStateOf(0L) }
    LaunchedEffect(Unit) {
        mediaStorageBytes = services.sessionMediaRepository.totalStorageBytes()
    }
    SettingsScreen(
        themeMode = themeMode,
        updateCheckEnabled = SettingsLogic.isUpdateCheckEnabled(checkInterval),
        highContrastEnabled = highContrast,
        largeTextEnabled = largeTextEnabled,
        ttsReadoutEnabled = ttsReadoutEnabled,
        liveTelemetryEnabled = liveTelemetryEnabled,
        audibleTonesEnabled = audibleTonesEnabled,
        speedUnit = speedUnit,
        logIntervalMs = logInterval,
        obdDevices = services.obdManager.pairedDevices(),
        externalGpsDevices = services.externalGpsManager.pairedDevices(),
        selectedObdAddress = obdAddress,
        selectedExternalGpsAddress = externalGpsAddress,
        obdPidConfig = obdPidConfig,
        onThemeModeSelect = onThemeModeSelect,
        onUpdateCheckChange = onUpdateCheckChange,
        onHighContrastChange = onHighContrastChange,
        onLargeTextChange = onLargeTextChange,
        onTtsReadoutChange = onTtsReadoutChange,
        onLiveTelemetryChange = onLiveTelemetryChange,
        liveSignalWssUrl = liveSignalWssUrl,
        onLiveSignalWssUrlChange = onLiveSignalWssUrlChange,
        onLiveReceiverOpen = { onScreenChange(AppScreen.LiveReceiver) },
        onAudibleTonesChange = onAudibleTonesChange,
        androidAutoEnabled = androidAutoEnabled,
        androidAutoMetrics = androidAutoMetrics,
        onAndroidAutoEnabledChange = { enabled ->
            scope.launch {
                services.settingsPreferences.setAndroidAutoEnabled(enabled)
                FeatureFlags.androidAutoEnabled = enabled
            }
        },
        onAndroidAutoMetricToggle = { metric ->
            scope.launch { services.settingsPreferences.toggleAndroidAutoMetric(metric) }
        },
        mediaCompressionQuality = mediaCompressionQuality,
        onMediaCompressionSelect = { quality ->
            scope.launch { services.settingsPreferences.setMediaCompressionQuality(quality) }
        },
        mediaStorageBytes = mediaStorageBytes,
        brightnessMode = brightnessMode,
        onBrightnessModeSelect = onBrightnessModeSelect,
        onSpeedUnitSelect = { unit -> scope.launch { services.settingsPreferences.setSpeedUnit(unit) } },
        onLogIntervalSelect = { ms -> scope.launch { services.settingsPreferences.setLogIntervalMs(ms) } },
        onObdDeviceSelect = { address ->
            scope.launch {
                services.settingsPreferences.setObdDeviceAddress(address)
                services.obdManager.selectDevice(address)
                services.obdManager.connect()
            }
        },
        onObdPidConfigChange = { config ->
            scope.launch {
                services.settingsPreferences.setObdPidConfig(config)
                services.obdManager.pidConfig = config
            }
        },
        onExternalGpsSelect = { address ->
            scope.launch {
                services.settingsPreferences.setExternalGpsEnabled(true)
                FeatureFlags.externalGpsEnabled = true
                services.settingsPreferences.setExternalGpsAddress(address)
                services.externalGpsManager.selectDevice(address)
                services.externalGpsManager.connect()
            }
        },
        externalGpsEnabled = externalGpsEnabled,
        onExternalGpsEnabledChange = { enabled ->
            scope.launch {
                services.settingsPreferences.setExternalGpsEnabled(enabled)
                FeatureFlags.externalGpsEnabled = enabled
                if (!enabled) {
                    services.externalGpsManager.disconnect()
                } else {
                    externalGpsAddress?.let { address ->
                        services.externalGpsManager.selectDevice(address)
                        services.externalGpsManager.connect()
                    }
                }
            }
        },
        onForgetExternalGps = {
            scope.launch {
                services.settingsPreferences.forgetExternalGpsDevice()
                FeatureFlags.externalGpsEnabled = false
                services.externalGpsManager.disconnect()
            }
        },
        onImuManage = { onScreenChange(AppScreen.ImuManage) },
        tpmsEnabled = tpmsEnabled,
        onTpmsEnabledChange = { enabled ->
            scope.launch {
                services.settingsPreferences.setTpmsEnabled(enabled)
                FeatureFlags.tpmsEnabled = enabled
                if (!enabled) services.bleTpmsManager.stopScan()
            }
        },
        onTpmsManage = { onScreenChange(AppScreen.TpmsManage) },
        pressureUnit = pressureUnit,
        tempUnit = tempUnit,
        onPressureUnitSelect = { unit ->
            scope.launch { services.settingsPreferences.setPressureUnit(unit) }
        },
        onTempUnitSelect = { unit ->
            scope.launch { services.settingsPreferences.setTempUnit(unit) }
        },
        onCalibrationReset = {
            scope.launch {
                services.calibrationStore.clearOffsets()
            }
        },
        onCalibrationTips = { onScreenChange(AppScreen.CalibrationTips) },
        onCalibrationWizard = { onScreenChange(AppScreen.CalibrationWizard) },
        developerModeEnabled = developerModeEnabled,
        onDeveloperModeChange = { enabled ->
            scope.launch { services.settingsPreferences.setDeveloperModeEnabled(enabled) }
        },
        onDeveloperModeOpen = { onScreenChange(AppScreen.DeveloperMode) },
        recordingMode = recordingMode,
        onRecordingModeSelect = { mode ->
            scope.launch { services.settingsProfileRepository.updateRecordingMode(mode) }
        },
        lapTimingEnabled = lapTimingEnabled,
        onLapTimingEnabledChange = { enabled ->
            scope.launch { services.settingsPreferences.setLapTimingEnabled(enabled) }
        },
        onTrackSetup = { onScreenChange(AppScreen.TrackSetup) },
        attitudeGaugeMode = attitudeGaugeMode,
        onAttitudeGaugeModeSelect = { mode ->
            scope.launch { services.settingsPreferences.setAttitudeGaugeMode(mode) }
        },
        alertThresholds = alertThresholds,
        onAlertThresholdsChange = onAlertThresholdsChange,
        activePresetId = activePresetId,
        onPresetSelected = onPresetSelected,
        onBack = { onScreenChange(AppScreen.Dashboard) },
    )
}
