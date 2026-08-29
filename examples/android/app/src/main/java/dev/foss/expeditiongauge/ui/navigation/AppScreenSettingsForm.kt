package dev.foss.expeditiongauge.ui.navigation

import android.content.Context
import androidx.compose.runtime.Composable
import dev.foss.expeditiongauge.ExpeditionGaugeServices
import dev.foss.expeditiongauge.FeatureFlags
import dev.foss.expeditiongauge.alerts.AlertAudioMode
import dev.foss.expeditiongauge.alerts.AlertThresholds
import dev.foss.expeditiongauge.gauge.AttitudeGaugeMode
import dev.foss.expeditiongauge.map.HomeMapRegion
import dev.foss.expeditiongauge.map.MapTilePrefetchWorker
import dev.foss.expeditiongauge.presets.DashboardPresetId
import dev.foss.expeditiongauge.recording.RecordingMode
import dev.foss.expeditiongauge.settings.HudScreenshotMode
import dev.foss.expeditiongauge.settings.MediaCompressionQuality
import dev.foss.expeditiongauge.settings.ObdPidConfig
import dev.foss.expeditiongauge.settings.PressureUnit
import dev.foss.expeditiongauge.settings.SettingsLogic
import dev.foss.expeditiongauge.settings.SpeedUnit
import dev.foss.expeditiongauge.settings.TempUnit
import dev.foss.expeditiongauge.ui.AppScreen
import dev.foss.expeditiongauge.ui.settings.SettingsScreen
import dev.foss.expeditiongauge.ui.settings.SettingsUiActions
import dev.foss.expeditiongauge.ui.settings.SettingsUiState
import dev.foss.expeditiongauge.ui.theme.BrightnessMode
import dev.foss.expeditiongauge.ui.theme.ThemeMode
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
internal fun AppScreenSettingsForm(
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
    obdPidConfig: ObdPidConfig,
    externalGpsAddress: String?,
    externalGpsEnabled: Boolean,
    tpmsEnabled: Boolean,
    pressureUnit: PressureUnit,
    tempUnit: TempUnit,
    brightnessMode: BrightnessMode,
    liveSignalWssUrl: String,
    recordingMode: RecordingMode,
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
    developerModeEnabled: Boolean,
    homeMapRegion: HomeMapRegion?,
    mediaCompressionQuality: MediaCompressionQuality,
    mediaStorageBytes: Long,
    autoRecordEnabled: Boolean,
    autoRecordDevices: Set<String>,
    sessionStoragePercent: Int,
    sessionStorageUsed: Long,
    sessionStorageAllowed: Long,
    onSessionStorageAllowedChange: (Long) -> Unit,
    autoCalibrateWhenStill: Boolean,
    screenshotMode: HudScreenshotMode,
    obd: AppScreenSettingsObdWiring,
) {
    SettingsScreen(
        state = SettingsUiState(
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
            obdPidConfig = obdPidConfig,
            liveSignalWssUrl = liveSignalWssUrl,
            homeMapRegion = homeMapRegion,
            mediaCompressionQuality = mediaCompressionQuality,
            mediaStorageBytes = mediaStorageBytes,
            autoRecordEnabled = autoRecordEnabled,
            autoRecordDeviceAddresses = autoRecordDevices,
            sessionStoragePercent = sessionStoragePercent,
            sessionStorageUsedBytes = sessionStorageUsed,
            sessionStorageAllowedBytes = sessionStorageAllowed,
            brightnessMode = brightnessMode,
            keepScreenAwake = keepScreenAwake,
            externalGpsEnabled = externalGpsEnabled,
            tpmsEnabled = tpmsEnabled,
            pressureUnit = pressureUnit,
            tempUnit = tempUnit,
            autoCalibrateWhenStill = autoCalibrateWhenStill,
            developerModeEnabled = developerModeEnabled,
            recordingMode = recordingMode,
            lapTimingEnabled = lapTimingEnabled,
            attitudeGaugeMode = attitudeGaugeMode,
            alertThresholds = alertThresholds,
            alertAudioMode = alertAudioMode,
            alertsMuted = alertsMuted,
            activePresetId = activePresetId,
            screenshotMode = screenshotMode,
            pidDiscoveryPids = obd.pidDiscoveryPids,
        ),
        actions = rememberSettingsUiActions(
            context = context,
            onScreenChange = onScreenChange,
            scope = scope,
            services = services,
            autoRecordDevices = autoRecordDevices,
            onSessionStorageAllowedChange = onSessionStorageAllowedChange,
            onAlertThresholdsChange = onAlertThresholdsChange,
            onAlertAudioModeChange = onAlertAudioModeChange,
            onAlertsMutedChange = onAlertsMutedChange,
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
            onKeepScreenAwakeChange = onKeepScreenAwakeChange,
            obd = obd,
        ),
    )
}

private fun rememberSettingsUiActions(
    context: Context,
    onScreenChange: (AppScreen) -> Unit,
    scope: CoroutineScope,
    services: ExpeditionGaugeServices,
    autoRecordDevices: Set<String>,
    onSessionStorageAllowedChange: (Long) -> Unit,
    onAlertThresholdsChange: (AlertThresholds) -> Unit,
    onAlertAudioModeChange: (AlertAudioMode) -> Unit,
    onAlertsMutedChange: (Boolean) -> Unit,
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
    onKeepScreenAwakeChange: (Boolean) -> Unit,
    obd: AppScreenSettingsObdWiring,
): SettingsUiActions = SettingsUiActions(
    onBack = { onScreenChange(AppScreen.Dashboard) },
    onAboutOpen = { onScreenChange(AppScreen.About) },
    onThemeModeSelect = onThemeModeSelect,
    onUpdateCheckChange = onUpdateCheckChange,
    onHighContrastChange = onHighContrastChange,
    onLargeTextChange = onLargeTextChange,
    onTtsReadoutChange = onTtsReadoutChange,
    onLiveTelemetryChange = onLiveTelemetryChange,
    onLiveSignalWssUrlChange = onLiveSignalWssUrlChange,
    onLiveReceiverOpen = { onScreenChange(AppScreen.LiveReceiver) },
    onAudibleTonesChange = onAudibleTonesChange,
    onUseCurrentLocationAsHomeRegion = {
        scope.launch {
            val snap = services.telemetryBus.snapshots.value
            val lat = snap.latitude ?: return@launch
            val lon = snap.longitude ?: return@launch
            services.homeMapRegionPreferences.setRegion(lat, lon)
            MapTilePrefetchWorker.enqueueHomePrefetch(context)
        }
    },
    onMediaCompressionSelect = { quality ->
        scope.launch { services.settingsPreferences.setMediaCompressionQuality(quality) }
    },
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
    onSessionStoragePercentChange = { percent ->
        scope.launch {
            services.settingsPreferences.setSessionStorageFreePercent(percent)
            onSessionStorageAllowedChange(services.sessionStorageBudget.allowedBytes())
        }
    },
    onBrightnessModeSelect = onBrightnessModeSelect,
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
    onObdRetry = obd.onRetry,
    onForgetObd = obd.onForget,
    onObdPairNew = obd.onPairNew,
    onObdPidConfigChange = obd.onPidConfigChange,
    onPidDiscover = obd.onPidDiscover,
    onExternalGpsSelect = { address ->
        scope.launch {
            FeatureFlags.externalGpsEnabled = true
            services.settingsPreferences.setExternalGpsEnabled(true)
            services.settingsPreferences.setExternalGpsAddress(address)
        }
    },
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
    onTpmsEnabledChange = { enabled ->
        scope.launch {
            services.settingsPreferences.setTpmsEnabled(enabled)
            FeatureFlags.tpmsEnabled = enabled
            if (!enabled) services.bleTpmsManager.stopScan()
        }
    },
    onTpmsManage = { onScreenChange(AppScreen.TpmsManage) },
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
    onAutoCalibrateWhenStillChange = { enabled ->
        scope.launch { services.settingsPreferences.setAutoCalibrateWhenStill(enabled) }
    },
    onDeveloperModeChange = { enabled ->
        scope.launch { services.settingsPreferences.setDeveloperModeEnabled(enabled) }
    },
    onDeveloperModeOpen = { onScreenChange(AppScreen.DeveloperMode) },
    onRecordingModeSelect = { mode ->
        scope.launch { services.settingsProfileRepository.updateRecordingMode(mode) }
    },
    onLapTimingEnabledChange = { enabled ->
        scope.launch { services.settingsPreferences.setLapTimingEnabled(enabled) }
    },
    onTrackSetup = { onScreenChange(AppScreen.TrackSetup) },
    onAttitudeGaugeModeSelect = { mode ->
        scope.launch { services.settingsPreferences.setAttitudeGaugeMode(mode) }
    },
    onAlertAudioModeChange = onAlertAudioModeChange,
    onAlertsMutedChange = onAlertsMutedChange,
    onAlertThresholdsChange = onAlertThresholdsChange,
    onPresetSelected = onPresetSelected,
    onScreenshotModeSelected = { mode ->
        scope.launch { services.settingsPreferences.setHudScreenshotMode(mode) }
    },
)
