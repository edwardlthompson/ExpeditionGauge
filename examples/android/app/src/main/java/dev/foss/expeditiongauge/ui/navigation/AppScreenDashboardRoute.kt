package dev.foss.expeditiongauge.ui.navigation

import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import dev.foss.expeditiongauge.settings.PressureUnit
import dev.foss.expeditiongauge.settings.SettingsLogic
import dev.foss.expeditiongauge.settings.TempUnit
import dev.foss.expeditiongauge.ui.AppScreen
import dev.foss.expeditiongauge.ui.dashboard.DashboardScreen
import dev.foss.expeditiongauge.ui.dashboard.DashboardViewModel
import dev.foss.expeditiongauge.ui.theme.ThemeMode
import dev.foss.expeditiongauge.accessibility.AudibleTones
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

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
    logInterval: Long,
    lapTimingEnabled: Boolean,
    lapTimingState: dev.foss.expeditiongauge.timing.PredictiveTimingState,
    attitudeGaugeMode: AttitudeGaugeMode,
    ttsReadoutEnabled: Boolean,
) {
    val telemetry by services.telemetryBus.snapshots.collectAsStateWithLifecycle(
        initialValue = dev.foss.expeditiongauge.telemetry.TelemetrySnapshot.empty(),
    )
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
        onStartLive = { dashboardViewModel.startLiveSession() },
        onStopLive = { dashboardViewModel.stopLiveSession() },
        tpmsEnabled = tpmsEnabled,
        pressureUnit = pressureUnit,
        tempUnit = tempUnit,
        logIntervalMs = logInterval,
        lapTimingEnabled = lapTimingEnabled,
        lapTimingState = lapTimingState,
        attitudeGaugeMode = attitudeGaugeMode,
    )
}
