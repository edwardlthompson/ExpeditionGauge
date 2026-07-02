package dev.foss.expeditiongauge.ui.navigation

import android.content.Context
import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.foss.expeditiongauge.ExpeditionGaugeServices
import dev.foss.expeditiongauge.about.AppUpdatePreferences
import dev.foss.expeditiongauge.about.DonationsConfig
import dev.foss.expeditiongauge.about.ReleaseAsset
import dev.foss.expeditiongauge.about.UpdateApplyCoordinator
import dev.foss.expeditiongauge.alerts.AlertThresholds
import dev.foss.expeditiongauge.accessibility.AccessibilityPreferences
import dev.foss.expeditiongauge.accessibility.AudibleTones
import dev.foss.expeditiongauge.settings.PressureUnit
import dev.foss.expeditiongauge.settings.SpeedUnit
import dev.foss.expeditiongauge.settings.TempUnit
import dev.foss.expeditiongauge.stats.SessionAggregateStats
import dev.foss.expeditiongauge.stats.SessionComparison
import dev.foss.expeditiongauge.stats.SessionStatsAggregator
import dev.foss.expeditiongauge.stats.SessionStatsSummary
import dev.foss.expeditiongauge.ui.AppScreen
import dev.foss.expeditiongauge.ui.calibration.CalibrationTipsScreen
import dev.foss.expeditiongauge.ui.calibration.CalibrationWizardScreen
import dev.foss.expeditiongauge.ui.developer.DeveloperModeScreen
import dev.foss.expeditiongauge.ui.dashboard.DashboardViewModel
import dev.foss.expeditiongauge.presets.DashboardPresetId
import dev.foss.expeditiongauge.gauge.AttitudeGaugeMode
import dev.foss.expeditiongauge.ui.timing.TrackSetupScreen
import dev.foss.expeditiongauge.ui.live.LiveReceiverScreen
import dev.foss.expeditiongauge.ui.settings.ImuManagementScreen
import dev.foss.expeditiongauge.ui.settings.TpmsManagementScreen
import dev.foss.expeditiongauge.ui.theme.BrightnessMode
import dev.foss.expeditiongauge.ui.theme.ThemeMode
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun AppScreenRouter(
    screen: AppScreen,
    onScreenChange: (AppScreen) -> Unit,
    context: Context,
    scope: CoroutineScope,
    services: ExpeditionGaugeServices,
    dashboardViewModel: DashboardViewModel,
    themeMode: ThemeMode,
    onThemeToggle: () -> Unit,
    onThemeModeSelect: (ThemeMode) -> Unit,
    liveTelemetryEnabled: Boolean,
    liveSignalWssUrl: String,
    onLiveTelemetryChange: (Boolean) -> Unit,
    onLiveSignalWssUrlChange: (String) -> Unit,
    isOnline: Boolean,
    checkInterval: String,
    onUpdateCheckChange: (Boolean) -> Unit,
    appVersion: String,
    installedFormat: String?,
    updateStatus: String,
    donations: DonationsConfig,
    applyAsset: ReleaseAsset?,
    activity: ComponentActivity?,
    appUpdatePreferences: AppUpdatePreferences,
    accessibilityPreferences: AccessibilityPreferences,
    audibleTonesEnabled: Boolean,
    highContrast: Boolean,
    largeTextEnabled: Boolean,
    ttsReadoutEnabled: Boolean,
    onLargeTextChange: (Boolean) -> Unit,
    onTtsReadoutChange: (Boolean) -> Unit,
    onHighContrastChange: (Boolean) -> Unit,
    onAudibleTonesChange: (Boolean) -> Unit,
    brightnessMode: BrightnessMode,
    onBrightnessModeSelect: (BrightnessMode) -> Unit,
    keepScreenAwake: Boolean,
    onKeepScreenAwakeChange: (Boolean) -> Unit,
    speedUnit: SpeedUnit,
    logInterval: Long,
    obdAddress: String?,
    obdPidConfig: dev.foss.expeditiongauge.settings.ObdPidConfig,
    externalGpsAddress: String?,
    externalGpsEnabled: Boolean,
    tpmsEnabled: Boolean,
    pressureUnit: PressureUnit,
    tempUnit: TempUnit,
    imuSessions: List<dev.foss.expeditiongauge.ble.ImuDeviceSession>,
    tpmsSessions: List<dev.foss.expeditiongauge.ble.tpms.TpmsDeviceSession>,
    statsSummaries: List<SessionStatsSummary>,
    statsAggregate: SessionAggregateStats,
    comparison: MutableState<SessionComparison?>,
    sessionStatsAggregator: SessionStatsAggregator,
    audibleTones: AudibleTones,
    editingSessionId: Long?,
    onEditingSessionIdChange: (Long?) -> Unit,
    recordingMode: dev.foss.expeditiongauge.recording.RecordingMode,
    lapTimingEnabled: Boolean,
    lapTimingState: dev.foss.expeditiongauge.timing.PredictiveTimingState,
    attitudeGaugeMode: AttitudeGaugeMode,
    alertThresholds: AlertThresholds,
    onAlertThresholdsChange: (AlertThresholds) -> Unit,
    activePresetId: DashboardPresetId,
    onPresetSelected: (DashboardPresetId) -> Unit,
) {
    val canApplyUpdate = applyAsset != null
    when (screen) {
        AppScreen.Dashboard -> AppScreenDashboardRoute(
            onScreenChange = onScreenChange,
            scope = scope,
            services = services,
            dashboardViewModel = dashboardViewModel,
            themeMode = themeMode,
            onThemeToggle = onThemeToggle,
            onThemeModeSelect = onThemeModeSelect,
            liveTelemetryEnabled = liveTelemetryEnabled,
            isOnline = isOnline,
            checkInterval = checkInterval,
            onUpdateCheckChange = onUpdateCheckChange,
            appVersion = appVersion,
            installedFormat = installedFormat,
            updateStatus = updateStatus,
            donations = donations,
            applyAsset = applyAsset,
            activity = activity,
            appUpdatePreferences = appUpdatePreferences,
            accessibilityPreferences = accessibilityPreferences,
            audibleTones = audibleTones,
            tpmsEnabled = tpmsEnabled,
            pressureUnit = pressureUnit,
            tempUnit = tempUnit,
            speedUnit = speedUnit,
            logInterval = logInterval,
            lapTimingEnabled = lapTimingEnabled,
            lapTimingState = lapTimingState,
            attitudeGaugeMode = attitudeGaugeMode,
            ttsReadoutEnabled = ttsReadoutEnabled,
            statsAggregate = statsAggregate,
        )
        AppScreen.Settings -> AppScreenSettingsRoute(
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
        AppScreen.About -> dev.foss.expeditiongauge.ui.about.AboutScreen(
            version = appVersion,
            installedFormat = installedFormat ?: "apk",
            updateStatus = updateStatus,
            donations = donations,
            canApplyUpdate = canApplyUpdate,
            onApplyUpdate = {
                val asset = applyAsset
                val host = activity
                if (asset != null && host != null) {
                    scope.launch {
                        UpdateApplyCoordinator.applySideloadUpdate(host, appUpdatePreferences, asset)
                    }
                }
            },
            onBack = { onScreenChange(AppScreen.Dashboard) },
        )
        AppScreen.ImuManage -> ImuManagementScreen(
            sessions = imuSessions,
            bleImuManager = services.bleImuManager,
            onBack = { onScreenChange(AppScreen.Settings) },
        )
        AppScreen.TpmsManage -> TpmsManagementScreen(
            sessions = tpmsSessions,
            bleTpmsManager = services.bleTpmsManager,
            onBack = { onScreenChange(AppScreen.Settings) },
        )
        AppScreen.TrackSetup -> TrackSetupScreen(
            telemetryBus = services.telemetryBus,
            settingsPreferences = services.settingsPreferences,
            onBack = { onScreenChange(AppScreen.Settings) },
        )
        AppScreen.CalibrationTips -> CalibrationTipsScreen(
            onBack = { onScreenChange(AppScreen.Settings) },
        )
        AppScreen.CalibrationWizard -> {
            val telemetry by services.telemetryBus.snapshots.collectAsStateWithLifecycle(
                initialValue = dev.foss.expeditiongauge.telemetry.TelemetrySnapshot.empty(),
            )
            CalibrationWizardScreen(
                wizardStore = services.calibrationWizardStore,
                telemetry = telemetry,
                imuSessionCount = imuSessions.count { it.connected },
                onImuManage = { onScreenChange(AppScreen.ImuManage) },
                onBack = { onScreenChange(AppScreen.Settings) },
            )
        }
        AppScreen.DeveloperMode -> {
            val telemetry by services.telemetryBus.snapshots.collectAsStateWithLifecycle(
                initialValue = dev.foss.expeditiongauge.telemetry.TelemetrySnapshot.empty(),
            )
            val madgwickBeta by services.settingsPreferences.madgwickBeta
                .collectAsStateWithLifecycle(initialValue = 0.1f)
            DeveloperModeScreen(
                telemetry = telemetry,
                madgwickBeta = madgwickBeta,
                onMadgwickBetaChange = { beta ->
                    scope.launch { services.settingsPreferences.setMadgwickBeta(beta) }
                },
                onBack = { onScreenChange(AppScreen.Settings) },
                speedUnit = speedUnit,
            )
        }
        AppScreen.LiveReceiver -> {
            var sessionId by remember { mutableStateOf("") }
            var sessionCode by remember { mutableStateOf("") }
            var signalWss by remember { mutableStateOf(liveSignalWssUrl) }
            LaunchedEffect(liveSignalWssUrl) { signalWss = liveSignalWssUrl }
            val latestSample by services.liveTelemetryModule.receiver.latestSample
                .collectAsStateWithLifecycle(initialValue = null)
            val receiverState by services.liveTelemetryModule.receiver.state
                .collectAsStateWithLifecycle(initialValue = dev.foss.expeditiongauge.live.LiveReceiverState.Idle)
            LiveReceiverScreen(
                latestSample = latestSample,
                sessionId = sessionId,
                onSessionIdChange = { sessionId = it },
                sessionCode = sessionCode,
                onSessionCodeChange = { sessionCode = it },
                signalWss = signalWss,
                onSignalWssChange = { signalWss = it },
                onJoin = {
                    services.liveTelemetryModule.joinReceiver(scope, sessionId, sessionCode, signalWss)
                },
                onDisconnect = { services.liveTelemetryModule.stopReceiver() },
                isConnected = receiverState == dev.foss.expeditiongauge.live.LiveReceiverState.Connected,
                onBack = {
                    services.liveTelemetryModule.stopReceiver()
                    onScreenChange(AppScreen.Settings)
                },
                speedUnit = speedUnit,
            )
        }
        AppScreen.Sessions,
        AppScreen.SessionEdit,
        AppScreen.Playback,
        AppScreen.Stats,
        AppScreen.Comparison -> AppScreenSessionRoutes(
            screen = screen,
            onScreenChange = onScreenChange,
            context = context,
            scope = scope,
            services = services,
            statsSummaries = statsSummaries,
            statsAggregate = statsAggregate,
            sessionStatsAggregator = sessionStatsAggregator,
            comparison = comparison,
            editingSessionId = editingSessionId,
            onEditingSessionIdChange = onEditingSessionIdChange,
            speedUnit = speedUnit,
            pressureUnit = pressureUnit,
        )
    }
}
