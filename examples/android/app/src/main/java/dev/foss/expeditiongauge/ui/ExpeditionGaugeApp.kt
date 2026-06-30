package dev.foss.expeditiongauge.ui

import android.content.Context
import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.foss.expeditiongauge.BuildConfig
import dev.foss.expeditiongauge.ExpeditionGaugeServices
import dev.foss.expeditiongauge.FeatureFlags
import dev.foss.expeditiongauge.R
import dev.foss.expeditiongauge.about.AppUpdatePreferences
import dev.foss.expeditiongauge.about.CheckSchedule
import dev.foss.expeditiongauge.about.DonationsLoader
import dev.foss.expeditiongauge.about.ReleaseAsset
import dev.foss.expeditiongauge.about.ReleaseAssetSelector
import dev.foss.expeditiongauge.about.ReleaseTagFetcher
import dev.foss.expeditiongauge.about.UpdateApplyCoordinator
import dev.foss.expeditiongauge.about.UpdateStatusEvaluator
import dev.foss.expeditiongauge.accessibility.AccessibilityPreferences
import dev.foss.expeditiongauge.accessibility.AudibleTones
import dev.foss.expeditiongauge.export.HtmlSummaryExporter
import dev.foss.expeditiongauge.network.NetworkStatusMonitor
import dev.foss.expeditiongauge.onboarding.OnboardingPreferences
import dev.foss.expeditiongauge.onboarding.OnboardingTour
import dev.foss.expeditiongauge.settings.SettingsLogic
import dev.foss.expeditiongauge.stats.SessionStatsAggregator
import dev.foss.expeditiongauge.ui.dashboard.DashboardViewModel
import dev.foss.expeditiongauge.ui.dashboard.DashboardViewModelFactory
import dev.foss.expeditiongauge.ui.playback.PlaybackScreen
import dev.foss.expeditiongauge.ui.playback.SessionListScreen
import dev.foss.expeditiongauge.ui.settings.ImuManagementScreen
import dev.foss.expeditiongauge.ui.settings.SettingsScreen
import dev.foss.expeditiongauge.ui.stats.SessionComparisonScreen
import dev.foss.expeditiongauge.ui.stats.SessionStatsDashboard
import dev.foss.expeditiongauge.ui.theme.BrightnessMode
import dev.foss.expeditiongauge.ui.theme.ExpeditionGaugeTheme
import dev.foss.expeditiongauge.ui.theme.ThemeMode
import dev.foss.expeditiongauge.ui.theme.ThemePreferences
import dev.foss.expeditiongauge.ui.theme.next
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

enum class AppScreen {
    Dashboard,
    Settings,
    About,
    ImuManage,
    Sessions,
    Playback,
    Stats,
    Comparison,
}

@Composable
fun ExpeditionGaugeApp(
    context: Context,
    scope: CoroutineScope,
    services: ExpeditionGaugeServices,
    themePreferences: ThemePreferences,
    accessibilityPreferences: AccessibilityPreferences,
    onboardingPreferences: OnboardingPreferences,
    appUpdatePreferences: AppUpdatePreferences,
    networkStatusMonitor: NetworkStatusMonitor,
    dashboardViewModelFactory: DashboardViewModelFactory,
    sessionStatsAggregator: SessionStatsAggregator,
    audibleTones: AudibleTones,
    brightnessMode: BrightnessMode = BrightnessMode.Auto,
) {
    val themeMode by themePreferences.themeMode.collectAsStateWithLifecycle(initialValue = ThemeMode.System)
    val highContrast by accessibilityPreferences.highContrastEnabled.collectAsStateWithLifecycle(initialValue = false)
    val audibleTonesEnabled by accessibilityPreferences.audibleTonesEnabled.collectAsStateWithLifecycle(initialValue = false)
    val tourCompleted by onboardingPreferences.tourCompleted.collectAsStateWithLifecycle(initialValue = false)
    val isOnline by networkStatusMonitor.isOnline.collectAsStateWithLifecycle(initialValue = true)
    val installedFormat by appUpdatePreferences.installedFormat.collectAsStateWithLifecycle(initialValue = "apk")
    val checkInterval by appUpdatePreferences.checkInterval.collectAsStateWithLifecycle(initialValue = "off")
    val lastChecked by appUpdatePreferences.lastChecked.collectAsStateWithLifecycle(initialValue = null)
    val pendingRestart by appUpdatePreferences.pendingRestart.collectAsStateWithLifecycle(initialValue = false)
    val speedUnit by services.settingsPreferences.speedUnit.collectAsStateWithLifecycle(
        initialValue = dev.foss.expeditiongauge.settings.SpeedUnit.METRIC,
    )
    val logInterval by services.settingsPreferences.logIntervalMs.collectAsStateWithLifecycle(initialValue = 20L)
    val obdAddress by services.settingsPreferences.obdDeviceAddress.collectAsStateWithLifecycle(initialValue = null)
    val externalGpsAddress by services.settingsPreferences.externalGpsAddress.collectAsStateWithLifecycle(initialValue = null)
    val imuSessions by services.bleImuManager.sessionsFlow.collectAsStateWithLifecycle(initialValue = emptyList())
    val sessions by services.database.recordingSessionDao().observeAll()
        .collectAsStateWithLifecycle(initialValue = emptyList())

    var screen by remember { mutableStateOf(AppScreen.Dashboard) }
    var liveTelemetryEnabled by remember { mutableStateOf(FeatureFlags.liveTelemetryEnabled) }
    var comparison by remember { mutableStateOf<dev.foss.expeditiongauge.stats.SessionComparison?>(null) }
    var updateStatus by remember { mutableStateOf(context.getString(R.string.about_update_current)) }
    var applyAsset by remember { mutableStateOf<ReleaseAsset?>(null) }
    val donations = remember { DonationsLoader.load(context) }
    val appVersion = BuildConfig.VERSION_NAME
    val activity = context as? ComponentActivity
    val dashboardViewModel: DashboardViewModel = viewModel(factory = dashboardViewModelFactory)

    val statsSummaries = remember(sessions) {
        sessions.map { session ->
            sessionStatsAggregator.summarize(session, eventCount = 0)
        }
    }

    LaunchedEffect(logInterval) {
        services.recordingWriter.setLogIntervalMs(logInterval)
    }

    LaunchedEffect(pendingRestart) {
        if (pendingRestart) {
            updateStatus = context.getString(R.string.about_update_restarting)
        }
    }

    LaunchedEffect(checkInterval, lastChecked, isOnline, installedFormat, pendingRestart) {
        if (pendingRestart) return@LaunchedEffect
        if (!isOnline) return@LaunchedEffect
        if (!CheckSchedule.shouldCheck(checkInterval, lastChecked, System.currentTimeMillis())) return@LaunchedEffect
        val repo = ReleaseTagFetcher.loadReleaseRepo(context) ?: return@LaunchedEffect
        val release = ReleaseTagFetcher.fetchLatestRelease(repo) ?: return@LaunchedEffect
        val format = installedFormat ?: "apk"
        if (release.assets.isNotEmpty() && ReleaseAssetSelector.select(release.assets, format) == null) {
            updateStatus = context.getString(R.string.about_update_no_compatible)
            return@LaunchedEffect
        }
        appUpdatePreferences.setLastChecked(System.currentTimeMillis())
        val selected = ReleaseAssetSelector.select(release.assets, format)
        applyAsset = when (val result = UpdateStatusEvaluator.evaluate(appVersion, release.tag)) {
            is UpdateStatusEvaluator.Result.Current -> {
                updateStatus = context.getString(R.string.about_update_current)
                null
            }
            is UpdateStatusEvaluator.Result.Available -> {
                updateStatus = context.getString(R.string.about_update_available, result.version)
                selected
            }
        }
    }

    val canApplyUpdate = applyAsset != null

    ExpeditionGaugeTheme(
        themeMode = themeMode,
        brightnessMode = brightnessMode,
        highContrastEnabled = highContrast,
    ) {
        if (!tourCompleted && screen == AppScreen.Dashboard) {
            OnboardingTour(
                onComplete = { scope.launch { onboardingPreferences.setTourCompleted(true) } },
                onSkip = { scope.launch { onboardingPreferences.setTourCompleted(true) } },
            )
        } else {
            when (screen) {
                AppScreen.Dashboard -> DashboardScreen(
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
                    canApplyUpdate = canApplyUpdate,
                    onThemeToggle = { scope.launch { themePreferences.setThemeMode(themeMode.next()) } },
                    onThemeModeSelect = { mode -> scope.launch { themePreferences.setThemeMode(mode) } },
                    onAboutOpen = { screen = AppScreen.About },
                    onAboutClose = { screen = AppScreen.Dashboard },
                    onSettingsOpen = { screen = AppScreen.Settings },
                    onSettingsClose = { screen = AppScreen.Dashboard },
                    onUpdateCheckChange = { enabled ->
                        scope.launch {
                            appUpdatePreferences.setCheckInterval(
                                SettingsLogic.intervalForToggle(enabled, checkInterval),
                            )
                        }
                    },
                    onApplyUpdate = {
                        val asset = applyAsset ?: return@DashboardScreen
                        val host = activity ?: return@DashboardScreen
                        scope.launch {
                            UpdateApplyCoordinator.applySideloadUpdate(host, appUpdatePreferences, asset)
                        }
                    },
                    onImuManage = { screen = AppScreen.ImuManage },
                    onSessionsOpen = { screen = AppScreen.Sessions },
                    onStatsOpen = { screen = AppScreen.Stats },
                    onMarkEvent = {
                        dashboardViewModel.markEvent()
                        scope.launch {
                            audibleTones.playMarkEventTone(accessibilityPreferences.audibleTonesEnabled.first())
                        }
                    },
                    onStartLive = { dashboardViewModel.startLiveSession() },
                    onStopLive = { dashboardViewModel.stopLiveSession() },
                )
                AppScreen.Settings -> SettingsScreen(
                    themeMode = themeMode,
                    updateCheckEnabled = SettingsLogic.isUpdateCheckEnabled(checkInterval),
                    highContrastEnabled = highContrast,
                    liveTelemetryEnabled = liveTelemetryEnabled,
                    audibleTonesEnabled = audibleTonesEnabled,
                    speedUnit = speedUnit,
                    logIntervalMs = logInterval,
                    obdDevices = services.obdManager.pairedDevices(),
                    externalGpsDevices = services.externalGpsManager.pairedDevices(),
                    selectedObdAddress = obdAddress,
                    selectedExternalGpsAddress = externalGpsAddress,
                    onThemeModeSelect = { mode -> scope.launch { themePreferences.setThemeMode(mode) } },
                    onUpdateCheckChange = { enabled ->
                        scope.launch {
                            appUpdatePreferences.setCheckInterval(
                                SettingsLogic.intervalForToggle(enabled, checkInterval),
                            )
                        }
                    },
                    onHighContrastChange = { enabled ->
                        scope.launch { accessibilityPreferences.setHighContrastEnabled(enabled) }
                    },
                    onLiveTelemetryChange = { enabled ->
                        liveTelemetryEnabled = enabled
                        FeatureFlags.liveTelemetryEnabled = enabled
                    },
                    onAudibleTonesChange = { enabled ->
                        scope.launch { accessibilityPreferences.setAudibleTonesEnabled(enabled) }
                    },
                    onSpeedUnitSelect = { unit -> scope.launch { services.settingsPreferences.setSpeedUnit(unit) } },
                    onLogIntervalSelect = { ms -> scope.launch { services.settingsPreferences.setLogIntervalMs(ms) } },
                    onObdDeviceSelect = { address ->
                        scope.launch {
                            services.settingsPreferences.setObdDeviceAddress(address)
                            services.obdManager.selectDevice(address)
                            services.obdManager.connect()
                        }
                    },
                    onExternalGpsSelect = { address ->
                        scope.launch {
                            services.settingsPreferences.setExternalGpsAddress(address)
                            services.externalGpsManager.selectDevice(address)
                            services.externalGpsManager.connect()
                        }
                    },
                    onImuManage = { screen = AppScreen.ImuManage },
                    onCalibrationReset = {
                        scope.launch {
                            services.settingsPreferences.resetCalibrationFlag()
                            services.calibrationStore.setLevel(0f, 0f)
                        }
                    },
                    onBack = { screen = AppScreen.Dashboard },
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
                    onBack = { screen = AppScreen.Dashboard },
                )
                AppScreen.ImuManage -> ImuManagementScreen(
                    sessions = imuSessions,
                    bleImuManager = services.bleImuManager,
                    onBack = { screen = AppScreen.Settings },
                )
            AppScreen.Sessions -> SessionListScreen(
                database = services.database,
                    onSessionSelected = { sessionId ->
                        scope.launch {
                            val samples = services.database.sampleDao().getBySession(sessionId)
                            services.playbackEngine.loadSamples(samples)
                            screen = AppScreen.Playback
                        }
                    },
                    onBack = { screen = AppScreen.Dashboard },
                )
                AppScreen.Playback -> PlaybackScreen(
                    engine = services.playbackEngine,
                    onBack = { screen = AppScreen.Sessions },
                )
                AppScreen.Stats -> SessionStatsDashboard(
                    sessions = statsSummaries,
                    onPlay = { sessionId ->
                        scope.launch {
                            val samples = services.database.sampleDao().getBySession(sessionId)
                            services.playbackEngine.loadSamples(samples)
                            screen = AppScreen.Playback
                        }
                    },
                    onCompare = { leftId, rightId ->
                        val left = statsSummaries.first { it.sessionId == leftId }
                        val right = statsSummaries.first { it.sessionId == rightId }
                        comparison = sessionStatsAggregator.compare(left, right)
                        screen = AppScreen.Comparison
                    },
                    onExport = { summary ->
                        shareHtml(context, HtmlSummaryExporter.export(summary))
                    },
                    onBack = { screen = AppScreen.Dashboard },
                )
                AppScreen.Comparison -> comparison?.let { cmp ->
                    SessionComparisonScreen(
                        comparison = cmp,
                        onExport = {
                            shareHtml(context, HtmlSummaryExporter.exportComparison(cmp.left, cmp.right))
                        },
                        onBack = { screen = AppScreen.Stats },
                    )
                }
            }
        }
    }
}

private fun shareHtml(context: Context, html: String) {
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/html"
        putExtra(Intent.EXTRA_TEXT, html)
    }
    context.startActivity(Intent.createChooser(intent, context.getString(R.string.stats_export)))
}
