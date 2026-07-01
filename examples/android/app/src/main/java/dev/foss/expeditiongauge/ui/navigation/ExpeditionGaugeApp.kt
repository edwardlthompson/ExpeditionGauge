package dev.foss.expeditiongauge.ui.navigation

import android.content.Context
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.ui.Modifier
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
import dev.foss.expeditiongauge.about.AppUpdatePreferences
import dev.foss.expeditiongauge.about.DonationsLoader
import dev.foss.expeditiongauge.about.ReleaseAsset
import dev.foss.expeditiongauge.accessibility.AccessibilityPreferences
import dev.foss.expeditiongauge.accessibility.AudibleTones
import dev.foss.expeditiongauge.network.NetworkStatusMonitor
import dev.foss.expeditiongauge.onboarding.OnboardingPreferences
import dev.foss.expeditiongauge.onboarding.OnboardingTour
import dev.foss.expeditiongauge.settings.SettingsLogic
import dev.foss.expeditiongauge.stats.SessionStatsAggregator
import dev.foss.expeditiongauge.ui.AppScreen
import dev.foss.expeditiongauge.ui.dashboard.DashboardViewModel
import dev.foss.expeditiongauge.ui.dashboard.DashboardViewModelFactory
import dev.foss.expeditiongauge.ui.effects.AppUpdateEffects
import dev.foss.expeditiongauge.ui.theme.BrightnessMode
import dev.foss.expeditiongauge.ui.theme.BrightnessPreferences
import dev.foss.expeditiongauge.ui.theme.ExpeditionGaugeTheme
import dev.foss.expeditiongauge.ui.theme.ThemeMode
import dev.foss.expeditiongauge.ui.theme.ThemePreferences
import dev.foss.expeditiongauge.ui.theme.next
import dev.foss.expeditiongauge.ui.permissions.PermissionsRationaleScreen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

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
    brightnessPreferences: BrightnessPreferences,
    permissionsGranted: Boolean,
    onRequestPermissions: () -> Unit,
) {
    val themeMode by themePreferences.themeMode.collectAsStateWithLifecycle(initialValue = ThemeMode.System)
    val brightnessMode by brightnessPreferences.brightnessMode.collectAsStateWithLifecycle(
        initialValue = BrightnessMode.Auto,
    )
    val highContrast by accessibilityPreferences.highContrastEnabled.collectAsStateWithLifecycle(initialValue = false)
    val largeText by accessibilityPreferences.largeTextEnabled.collectAsStateWithLifecycle(initialValue = false)
    val ttsReadout by accessibilityPreferences.ttsReadoutEnabled.collectAsStateWithLifecycle(initialValue = false)
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
    val obdPidConfig by services.settingsPreferences.obdPidConfig.collectAsStateWithLifecycle(
        initialValue = dev.foss.expeditiongauge.settings.ObdPidConfig(),
    )
    val externalGpsAddress by services.settingsPreferences.externalGpsAddress.collectAsStateWithLifecycle(initialValue = null)
    val externalGpsEnabled by services.settingsPreferences.externalGpsEnabled.collectAsStateWithLifecycle(initialValue = false)
    val tpmsEnabled by services.settingsPreferences.tpmsEnabled.collectAsStateWithLifecycle(initialValue = false)
    val pressureUnit by services.settingsPreferences.pressureUnit.collectAsStateWithLifecycle(
        initialValue = dev.foss.expeditiongauge.settings.PressureUnit.PSI,
    )
    val tempUnit by services.settingsPreferences.tempUnit.collectAsStateWithLifecycle(
        initialValue = dev.foss.expeditiongauge.settings.TempUnit.CELSIUS,
    )
    val imuSessions by services.bleImuManager.sessionsFlow.collectAsStateWithLifecycle(initialValue = emptyList())
    val tpmsSessions by services.bleTpmsManager.sessionsFlow.collectAsStateWithLifecycle(initialValue = emptyList())
    val sessions by services.database.recordingSessionDao().observeAll()
        .collectAsStateWithLifecycle(initialValue = emptyList())
    val activeProfile by services.settingsProfileRepository.activeProfile
        .collectAsStateWithLifecycle(initialValue = dev.foss.expeditiongauge.settings.SettingsProfile.defaultProfile())
    val lapTimingEnabled by services.settingsPreferences.lapTimingEnabled
        .collectAsStateWithLifecycle(initialValue = false)
    val attitudeGaugeMode by services.settingsPreferences.attitudeGaugeMode
        .collectAsStateWithLifecycle(initialValue = dev.foss.expeditiongauge.gauge.AttitudeGaugeMode.ATTITUDE)
    val alertThresholds by services.alertThresholdsPreferences.thresholds
        .collectAsStateWithLifecycle(initialValue = dev.foss.expeditiongauge.alerts.AlertThresholds())
    val dashboardViewModel: DashboardViewModel = viewModel(factory = dashboardViewModelFactory)
    val lapTimingState by dashboardViewModel.lapTimingState.collectAsStateWithLifecycle(
        initialValue = dev.foss.expeditiongauge.timing.PredictiveTimingState(),
    )

    var screen by remember { mutableStateOf(AppScreen.Dashboard) }
    var editingSessionId by remember { mutableStateOf<Long?>(null) }
    val liveTelemetryEnabledPref by services.settingsPreferences.liveTelemetryEnabled
        .collectAsStateWithLifecycle(initialValue = false)
    var liveTelemetryEnabled by remember { mutableStateOf(false) }
    LaunchedEffect(liveTelemetryEnabledPref) {
        liveTelemetryEnabled = liveTelemetryEnabledPref
        FeatureFlags.liveTelemetryEnabled = liveTelemetryEnabledPref
    }
    val liveSignalWssUrl by services.settingsPreferences.liveSignalWssUrl
        .collectAsStateWithLifecycle(
            initialValue = dev.foss.expeditiongauge.live.LivePairingManager.DEFAULT_SIGNAL_WSS,
        )
    var comparison = remember { mutableStateOf<dev.foss.expeditiongauge.stats.SessionComparison?>(null) }
    val updateStatus = remember {
        mutableStateOf(context.getString(dev.foss.expeditiongauge.R.string.about_update_current))
    }
    val applyAsset = remember { mutableStateOf<ReleaseAsset?>(null) }
    val donations = remember { DonationsLoader.load(context) }
    val appVersion = BuildConfig.VERSION_NAME
    val activity = context as? ComponentActivity

    androidx.compose.runtime.LaunchedEffect(tpmsEnabled) {
        FeatureFlags.tpmsEnabled = tpmsEnabled
    }
    androidx.compose.runtime.LaunchedEffect(externalGpsEnabled) {
        FeatureFlags.externalGpsEnabled = externalGpsEnabled
    }

    var statsSummaries by remember { mutableStateOf<List<dev.foss.expeditiongauge.stats.SessionStatsSummary>>(emptyList()) }
    val statsAggregate = remember(statsSummaries) { sessionStatsAggregator.aggregate(statsSummaries) }

    androidx.compose.runtime.LaunchedEffect(sessions) {
        statsSummaries = if (sessions.isEmpty()) {
            emptyList()
        } else {
            sessionStatsAggregator.loadSummaries(services.database, services.lapTimingService, sessions)
        }
    }

    AppUpdateEffects(
        context = context,
        appVersion = appVersion,
        appUpdatePreferences = appUpdatePreferences,
        services = services,
        logInterval = logInterval,
        checkInterval = checkInterval,
        lastChecked = lastChecked,
        isOnline = isOnline,
        installedFormat = installedFormat,
        pendingRestart = pendingRestart,
        updateStatus = updateStatus,
        applyAsset = applyAsset,
    )

    ExpeditionGaugeTheme(
        themeMode = themeMode,
        brightnessMode = brightnessMode,
        highContrastEnabled = highContrast,
        textScale = if (largeText) 1.25f else 1f,
    ) {
        if (FeatureFlags.onboardingEnabled && !tourCompleted && screen == AppScreen.Dashboard) {
            OnboardingTour(
                onComplete = { scope.launch { onboardingPreferences.setTourCompleted(true) } },
                onSkip = { scope.launch { onboardingPreferences.setTourCompleted(true) } },
                onRequestPermissions = onRequestPermissions,
            )
        } else if (!permissionsGranted) {
            PermissionsRationaleScreen(onRequestPermissions = onRequestPermissions)
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .windowInsetsPadding(WindowInsets.statusBars),
            ) {
                AppScreenRouter(
                screen = screen,
                onScreenChange = { screen = it },
                context = context,
                scope = scope,
                services = services,
                dashboardViewModel = dashboardViewModel,
                themeMode = themeMode,
                onThemeToggle = { scope.launch { themePreferences.setThemeMode(themeMode.next()) } },
                onThemeModeSelect = { mode -> scope.launch { themePreferences.setThemeMode(mode) } },
                liveTelemetryEnabled = liveTelemetryEnabled,
                liveSignalWssUrl = liveSignalWssUrl,
                onLiveTelemetryChange = { enabled ->
                    liveTelemetryEnabled = enabled
                    FeatureFlags.liveTelemetryEnabled = enabled
                    scope.launch { services.settingsPreferences.setLiveTelemetryEnabled(enabled) }
                },
                onLiveSignalWssUrlChange = { url ->
                    scope.launch { services.settingsPreferences.setLiveSignalWssUrl(url) }
                },
                isOnline = isOnline,
                checkInterval = checkInterval,
                onUpdateCheckChange = { enabled ->
                    scope.launch {
                        appUpdatePreferences.setCheckInterval(
                            SettingsLogic.intervalForToggle(enabled, checkInterval),
                        )
                    }
                },
                appVersion = appVersion,
                installedFormat = installedFormat,
                updateStatus = updateStatus.value,
                donations = donations,
                applyAsset = applyAsset.value,
                activity = activity,
                appUpdatePreferences = appUpdatePreferences,
                accessibilityPreferences = accessibilityPreferences,
                audibleTonesEnabled = audibleTonesEnabled,
                highContrast = highContrast,
                largeTextEnabled = largeText,
                ttsReadoutEnabled = ttsReadout,
                onLargeTextChange = { enabled ->
                    scope.launch { accessibilityPreferences.setLargeTextEnabled(enabled) }
                },
                onTtsReadoutChange = { enabled ->
                    scope.launch { accessibilityPreferences.setTtsReadoutEnabled(enabled) }
                },
                onHighContrastChange = { enabled ->
                    scope.launch { accessibilityPreferences.setHighContrastEnabled(enabled) }
                },
                onAudibleTonesChange = { enabled ->
                    scope.launch { accessibilityPreferences.setAudibleTonesEnabled(enabled) }
                },
                brightnessMode = brightnessMode,
                onBrightnessModeSelect = { mode ->
                    scope.launch { brightnessPreferences.setBrightnessMode(mode) }
                },
                speedUnit = speedUnit,
                logInterval = logInterval,
                obdAddress = obdAddress,
                obdPidConfig = obdPidConfig,
                externalGpsAddress = externalGpsAddress,
                externalGpsEnabled = externalGpsEnabled,
                tpmsEnabled = tpmsEnabled,
                pressureUnit = pressureUnit,
                tempUnit = tempUnit,
                imuSessions = imuSessions,
                tpmsSessions = tpmsSessions,
                statsSummaries = statsSummaries,
                statsAggregate = statsAggregate,
                comparison = comparison,
                sessionStatsAggregator = sessionStatsAggregator,
                audibleTones = audibleTones,
                editingSessionId = editingSessionId,
                onEditingSessionIdChange = { editingSessionId = it },
                recordingMode = activeProfile.recordingMode,
                lapTimingEnabled = lapTimingEnabled,
                lapTimingState = lapTimingState,
                attitudeGaugeMode = attitudeGaugeMode,
                alertThresholds = alertThresholds,
                onAlertThresholdsChange = { thresholds ->
                    scope.launch { services.alertThresholdsPreferences.setThresholds(thresholds) }
                },
                activePresetId = activeProfile.presetId,
                onPresetSelected = { presetId ->
                    scope.launch { services.settingsProfileRepository.updatePresetForActiveProfile(presetId) }
                },
            )
            }
        }
    }
}
