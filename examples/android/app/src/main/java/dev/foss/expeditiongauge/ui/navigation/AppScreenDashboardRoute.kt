package dev.foss.expeditiongauge.ui.navigation

import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.foss.expeditiongauge.ExpeditionGaugeServices
import dev.foss.expeditiongauge.FeatureFlags
import dev.foss.expeditiongauge.about.AppUpdatePreferences
import dev.foss.expeditiongauge.about.DonationsConfig
import dev.foss.expeditiongauge.about.ReleaseAsset
import dev.foss.expeditiongauge.about.UpdateApplyCoordinator
import dev.foss.expeditiongauge.accessibility.AccessibilityPreferences
import dev.foss.expeditiongauge.accessibility.MetricTtsReadout
import dev.foss.expeditiongauge.gauge.AttitudeGaugeMode
import dev.foss.expeditiongauge.car.gauge.InclinometerStyle
import dev.foss.expeditiongauge.alerts.AlertThresholds
import dev.foss.expeditiongauge.settings.HudScreenshotMode
import dev.foss.expeditiongauge.settings.MediaCompressionQuality
import dev.foss.expeditiongauge.settings.PressureUnit
import dev.foss.expeditiongauge.settings.SettingsLogic
import dev.foss.expeditiongauge.settings.SpeedUnit
import dev.foss.expeditiongauge.settings.TempUnit
import dev.foss.expeditiongauge.stats.SessionAggregateStats
import dev.foss.expeditiongauge.ui.AppScreen
import dev.foss.expeditiongauge.imreadiness.ImReadiness
import dev.foss.expeditiongauge.ui.dashboard.DashboardScreen
import dev.foss.expeditiongauge.ui.dashboard.DashboardViewModel
import dev.foss.expeditiongauge.ui.theme.ThemeMode
import dev.foss.expeditiongauge.accessibility.AudibleTones
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.io.File

@Composable
fun AppScreenDashboardRoute(
    onScreenChange: (AppScreen) -> Unit,
    scope: CoroutineScope,
    services: ExpeditionGaugeServices,
    dashboardViewModel: DashboardViewModel,
    themeMode: ThemeMode,
    onThemeToggle: () -> Unit,
    onThemeModeSelect: (ThemeMode) -> Unit,
    liveTelemetryEnabled: Boolean,
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
    audibleTones: AudibleTones,
    tpmsEnabled: Boolean,
    pressureUnit: PressureUnit,
    tempUnit: TempUnit,
    speedUnit: SpeedUnit,
    logInterval: Long,
    lapTimingEnabled: Boolean,
    lapTimingState: dev.foss.expeditiongauge.timing.PredictiveTimingState,
    attitudeGaugeMode: AttitudeGaugeMode,
    inclinometerStyle: InclinometerStyle = InclinometerStyle.LADDER,
    alertThresholds: AlertThresholds,
    ttsReadoutEnabled: Boolean,
    statsAggregate: SessionAggregateStats,
) {
    val telemetry by services.telemetryBus.snapshots.collectAsStateWithLifecycle(
        initialValue = dev.foss.expeditiongauge.telemetry.TelemetrySnapshot.empty(),
    )
    val compression by services.settingsPreferences.mediaCompressionQuality
        .collectAsStateWithLifecycle(initialValue = MediaCompressionQuality.BALANCED)
    val screenshotMode by services.settingsPreferences.hudScreenshotMode
        .collectAsStateWithLifecycle(initialValue = HudScreenshotMode.FULL_SCREEN)
    var pendingCaptureFile by remember { mutableStateOf<File?>(null) }

    fun attachTimestampMs(): Long = telemetry.timestampMs

    val takePicture = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (!success) {
            pendingCaptureFile = null
            return@rememberLauncherForActivityResult
        }
        val sessionId = services.recordingWriter.activeSessionId.value ?: return@rememberLauncherForActivityResult
        val file = pendingCaptureFile ?: return@rememberLauncherForActivityResult
        scope.launch {
            services.sessionMediaRepository.attachPhotoFromFile(
                sessionId,
                attachTimestampMs(),
                file,
                compression,
            )
        }
        pendingCaptureFile = null
    }

    val pickGallery = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri ?: return@rememberLauncherForActivityResult
        val sessionId = services.recordingWriter.activeSessionId.value ?: return@rememberLauncherForActivityResult
        scope.launch {
            services.sessionMediaRepository.attachPhotoFromUri(
                sessionId,
                attachTimestampMs(),
                uri,
                compression,
            )
        }
    }

    val storedDtcs by services.obdManager.storedDtcs.collectAsStateWithLifecycle()
    val imReport by services.obdManager.imReadiness.collectAsStateWithLifecycle()
    MetricTtsReadout(enabled = ttsReadoutEnabled && FeatureFlags.accessibilityPackEnabled, snapshot = telemetry)
    DashboardScreen(
        viewModel = dashboardViewModel,
        themeMode = themeMode,
        liveTelemetryEnabled = liveTelemetryEnabled,
        isOnline = isOnline,
        showAbout = false,
        showSettings = false,
        updateCheckEnabled = SettingsLogic.isUpdateCheckEnabled(checkInterval),
        appVersion = appVersion,
        installedFormat = installedFormat ?: "apk",
        updateStatus = updateStatus,
        donations = donations,
        canApplyUpdate = applyAsset != null,
        onThemeToggle = onThemeToggle,
        onThemeModeSelect = onThemeModeSelect,
        onAboutOpen = { onScreenChange(AppScreen.About) },
        onAboutClose = { onScreenChange(AppScreen.Dashboard) },
        onSettingsOpen = { onScreenChange(AppScreen.Settings) },
        onSettingsClose = { onScreenChange(AppScreen.Dashboard) },
        onUpdateCheckChange = onUpdateCheckChange,
        onApplyUpdate = {
            val asset = applyAsset ?: return@DashboardScreen
            val host = activity ?: return@DashboardScreen
            scope.launch {
                UpdateApplyCoordinator.applySideloadUpdate(host, appUpdatePreferences, asset)
            }
        },
        onImuManage = { onScreenChange(AppScreen.ImuManage) },
        onSessionsOpen = { onScreenChange(AppScreen.Sessions) },
        onStatsOpen = { onScreenChange(AppScreen.Stats) },
        onMarkEvent = {
            if (!FeatureFlags.markEventEnabled) return@DashboardScreen
            dashboardViewModel.markEvent()
            scope.launch {
                audibleTones.playMarkEventTone(accessibilityPreferences.audibleTonesEnabled.first())
            }
        },
        onAttachMediaCamera = if (FeatureFlags.mediaAttachmentsEnabled && activity != null) {
            {
                val sessionId = services.recordingWriter.activeSessionId.value ?: return@DashboardScreen
                val (file, uri) = services.sessionMediaRepository.createCaptureTarget(sessionId)
                pendingCaptureFile = file
                takePicture.launch(uri)
            }
        } else {
            null
        },
        onAttachMediaGallery = if (FeatureFlags.mediaAttachmentsEnabled) {
            { pickGallery.launch("image/*") }
        } else {
            null
        },
        onAttachMediaStub = if (FeatureFlags.mediaAttachmentsEnabled) {
            {
                val sessionId = services.recordingWriter.activeSessionId.value ?: return@DashboardScreen
                scope.launch {
                    services.sessionMediaRepository.attachStubPhoto(sessionId, attachTimestampMs())
                }
            }
        } else {
            null
        },
        onStartLive = { dashboardViewModel.startLiveSession() },
        onStopLive = { dashboardViewModel.stopLiveSession() },
        tpmsEnabled = tpmsEnabled,
        pressureUnit = pressureUnit,
        tempUnit = tempUnit,
        speedUnit = speedUnit,
        logIntervalMs = logInterval,
        lapTimingEnabled = lapTimingEnabled,
        lapTimingState = lapTimingState,
        attitudeGaugeMode = attitudeGaugeMode,
        inclinometerStyle = inclinometerStyle,
        maxPitchAlertDeg = alertThresholds.maxPitchDeg,
        maxRollAlertDeg = alertThresholds.maxRollDeg,
        statsAggregate = statsAggregate,
        screenshotMode = screenshotMode,
        onScreenshotModeSelected = { mode ->
            scope.launch { services.settingsPreferences.setHudScreenshotMode(mode) }
        },
        storedDtcs = storedDtcs,
        onClearDtcs = { services.obdManager.requestClearDtcs() },
        imReadiness = ImReadiness.line(imReport),
    )
}
