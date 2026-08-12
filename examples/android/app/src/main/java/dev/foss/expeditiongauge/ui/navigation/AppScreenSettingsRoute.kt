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
import dev.foss.expeditiongauge.FeatureFlags
import dev.foss.expeditiongauge.alerts.AlertThresholds
import dev.foss.expeditiongauge.gauge.AttitudeGaugeMode
import dev.foss.expeditiongauge.presets.DashboardPresetId
import dev.foss.expeditiongauge.settings.MediaCompressionQuality
import dev.foss.expeditiongauge.settings.PressureUnit
import dev.foss.expeditiongauge.settings.SettingsLogic
import dev.foss.expeditiongauge.settings.SpeedUnit
import dev.foss.expeditiongauge.settings.TempUnit
import dev.foss.expeditiongauge.map.MapTilePrefetchWorker
import dev.foss.expeditiongauge.ui.AppScreen
import dev.foss.expeditiongauge.ui.settings.SettingsScreen
import dev.foss.expeditiongauge.ui.theme.BrightnessMode
import dev.foss.expeditiongauge.ui.theme.ThemeMode
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

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
    LaunchedEffect(Unit) {
        mediaStorageBytes = services.sessionMediaRepository.totalStorageBytes()
        sessionStorageUsed = services.sessionStorageBudget.usedBytes()
        sessionStorageAllowed = services.sessionStorageBudget.allowedBytes()
    }
    val obd = rememberAppScreenSettingsObdWiring(context, scope, services, obdAddress)
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
        obdDevices = services.obdManager.suggestedObdDevices(),
        externalGpsDevices = services.externalGpsManager.pairedDevices(),
        selectedObdAddress = obdAddress,
        selectedExternalGpsAddress = externalGpsAddress,
        obdConnectionStatus = obd.connectionStatus,
        onObdRetry = obd.onRetry,
        onForgetObd = obd.onForget,
        onObdPairNew = obd.onPairNew,
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
        homeMapRegion = homeMapRegion,
        onUseCurrentLocationAsHomeRegion = {
            scope.launch {
                val snap = services.telemetryBus.snapshots.value
                val lat = snap.latitude ?: return@launch
                val lon = snap.longitude ?: return@launch
                services.homeMapRegionPreferences.setRegion(lat, lon)
                MapTilePrefetchWorker.enqueueHomePrefetch(context)
            }
        },
        mediaCompressionQuality = mediaCompressionQuality,
        onMediaCompressionSelect = { quality ->
            scope.launch { services.settingsPreferences.setMediaCompressionQuality(quality) }
        },
        mediaStorageBytes = mediaStorageBytes,
        autoRecordEnabled = autoRecordEnabled,
        autoRecordDeviceAddresses = autoRecordDevices,
        onAutoRecordEnabledChange = { enabled ->
            scope.launch { services.settingsPreferences.setAutoRecordEnabled(enabled) }
        },
        onAutoRecordDeviceToggle = { address, selected ->
            scope.launch {
                val next = autoRecordDevices.toMutableSet()
                if (selected) next.add(address) else next.remove(address)
                services.settingsPreferences.setAutoRecordDeviceAddresses(next)
            }
        },
        sessionStoragePercent = sessionStoragePercent,
        sessionStorageUsedBytes = sessionStorageUsed,
        sessionStorageAllowedBytes = sessionStorageAllowed,
        onSessionStoragePercentChange = { percent ->
            scope.launch {
                services.settingsPreferences.setSessionStorageFreePercent(percent)
                sessionStorageAllowed = services.sessionStorageBudget.allowedBytes()
            }
        },
        brightnessMode = brightnessMode,
        onBrightnessModeSelect = onBrightnessModeSelect,
        keepScreenAwake = keepScreenAwake,
        onKeepScreenAwakeChange = onKeepScreenAwakeChange,
        onSpeedUnitSelect = { unit ->
            scope.launch {
                services.settingsPreferences.setSpeedUnit(unit)
                services.settingsPreferences.setTempUnit(
                    if (unit == SpeedUnit.IMPERIAL) TempUnit.FAHRENHEIT else TempUnit.CELSIUS,
                )
            }
        },
        onLogIntervalSelect = { ms -> scope.launch { services.settingsPreferences.setLogIntervalMs(ms) } },
        onObdDeviceSelect = obd.onDeviceSelect,
        onObdPidConfigChange = obd.onPidConfigChange,
        onExternalGpsSelect = { address ->
            scope.launch {
                FeatureFlags.externalGpsEnabled = true
                services.settingsPreferences.setExternalGpsEnabled(true)
                services.settingsPreferences.setExternalGpsAddress(address)
            }
        },
        externalGpsEnabled = externalGpsEnabled,
        onExternalGpsEnabledChange = { enabled ->
            scope.launch {
                FeatureFlags.externalGpsEnabled = enabled
                services.settingsPreferences.setExternalGpsEnabled(enabled)
            }
        },
        onForgetExternalGps = {
            scope.launch {
                FeatureFlags.externalGpsEnabled = false
                services.settingsPreferences.forgetExternalGpsDevice()
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
            scope.launch { services.calibrationStore.clearOffsets() }
        },
        onCalibrationTips = { onScreenChange(AppScreen.CalibrationTips) },
        onCalibrationWizard = { onScreenChange(AppScreen.CalibrationWizard) },
        autoCalibrateWhenStill = autoCalibrateWhenStill,
        onAutoCalibrateWhenStillChange = { enabled ->
            scope.launch { services.settingsPreferences.setAutoCalibrateWhenStill(enabled) }
        },
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
        alertAudioMode = alertAudioMode,
        onAlertAudioModeChange = onAlertAudioModeChange,
        alertsMuted = alertsMuted,
        onAlertsMutedChange = onAlertsMutedChange,
        onAlertThresholdsChange = onAlertThresholdsChange,
        activePresetId = activePresetId,
        onPresetSelected = onPresetSelected,
        onBack = { onScreenChange(AppScreen.Dashboard) },
    )
}
