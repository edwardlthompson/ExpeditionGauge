package dev.foss.expeditiongauge.ui.dashboard

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import dev.foss.expeditiongauge.ui.layout.InsetAwareScaffold
import dev.foss.expeditiongauge.ui.layout.navigationBarPadding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.foss.expeditiongauge.FeatureFlags
import dev.foss.expeditiongauge.R
import dev.foss.expeditiongauge.about.DonationsConfig
import dev.foss.expeditiongauge.stats.SessionAggregateStats
import dev.foss.expeditiongauge.ui.about.AboutScreen
import dev.foss.expeditiongauge.timing.PredictiveTimingState
import dev.foss.expeditiongauge.ui.components.LapTimerStrip
import dev.foss.expeditiongauge.ui.live.LivePairingSheet
import dev.foss.expeditiongauge.ui.recording.RecordingAdvancedSheet
import dev.foss.expeditiongauge.ui.recording.RecordingLiveStrip
import dev.foss.expeditiongauge.ui.settings.SettingsScreen
import dev.foss.expeditiongauge.ui.theme.GaugeBackground
import dev.foss.expeditiongauge.ui.theme.GaugeRed
import dev.foss.expeditiongauge.ui.theme.GaugeYellow
import dev.foss.expeditiongauge.ui.theme.SpacingMd
import dev.foss.expeditiongauge.ui.theme.ThemeMode

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel,
    themeMode: ThemeMode,
    liveTelemetryEnabled: Boolean,
    isOnline: Boolean,
    showAbout: Boolean,
    showSettings: Boolean,
    updateCheckEnabled: Boolean,
    appVersion: String,
    installedFormat: String,
    updateStatus: String,
    donations: DonationsConfig,
    canApplyUpdate: Boolean,
    onThemeToggle: () -> Unit,
    onThemeModeSelect: (ThemeMode) -> Unit,
    onAboutOpen: () -> Unit,
    onAboutClose: () -> Unit,
    onSettingsOpen: () -> Unit,
    onSettingsClose: () -> Unit,
    onUpdateCheckChange: (Boolean) -> Unit,
    onApplyUpdate: () -> Unit,
    onImuManage: () -> Unit,
    onSessionsOpen: () -> Unit,
    onStatsOpen: () -> Unit,
    onMarkEvent: () -> Unit,
    onAttachMediaCamera: (() -> Unit)? = null,
    onAttachMediaGallery: (() -> Unit)? = null,
    onAttachMediaStub: (() -> Unit)? = null,
    onStartLive: () -> Unit,
    onStopLive: () -> Unit,
    tpmsEnabled: Boolean = false,
    pressureUnit: dev.foss.expeditiongauge.settings.PressureUnit = dev.foss.expeditiongauge.settings.PressureUnit.PSI,
    tempUnit: dev.foss.expeditiongauge.settings.TempUnit = dev.foss.expeditiongauge.settings.TempUnit.CELSIUS,
    speedUnit: dev.foss.expeditiongauge.settings.SpeedUnit = dev.foss.expeditiongauge.settings.SpeedUnit.METRIC,
    logIntervalMs: Long = 20L,
    lapTimingEnabled: Boolean = false,
    lapTimingState: PredictiveTimingState = PredictiveTimingState(),
    attitudeGaugeMode: dev.foss.expeditiongauge.gauge.AttitudeGaugeMode =
        dev.foss.expeditiongauge.gauge.AttitudeGaugeMode.G_FORCE,
    inclinometerStyle: dev.foss.expeditiongauge.car.gauge.InclinometerStyle =
        dev.foss.expeditiongauge.car.gauge.InclinometerStyle.LADDER,
    maxPitchAlertDeg: Float? = null,
    maxRollAlertDeg: Float? = null,
    statsAggregate: SessionAggregateStats = SessionAggregateStats(0, 0L, null),
    screenshotMode: dev.foss.expeditiongauge.settings.HudScreenshotMode =
        dev.foss.expeditiongauge.settings.HudScreenshotMode.FULL_SCREEN,
    onScreenshotModeSelected: (dev.foss.expeditiongauge.settings.HudScreenshotMode) -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val telemetry = uiState.telemetry
    val preset = uiState.activePreset
    val autocalPending by viewModel.autocalPending.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    var showRecordingAdvanced by remember { mutableStateOf(false) }
    var drawerOpen by remember { mutableStateOf(false) }
    val context = LocalContext.current
    // configChanges keeps the Activity; push live Display.rotation into fusion every frame.
    val configuration = LocalConfiguration.current
    val view = LocalView.current
    val displayRotation = view.display.rotation
    SideEffect {
        viewModel.updateDisplayRotation(displayRotation)
    }
    LaunchedEffect(configuration, displayRotation) {
        viewModel.updateDisplayRotation(displayRotation)
    }
    LaunchedEffect(Unit) {
        viewModel.autocalMessages.collect { msg ->
            snackbarHostState.showSnackbar(msg)
        }
    }

    BackHandler(enabled = drawerOpen) { drawerOpen = false }

    if (autocalPending != null) {
        AutocalConfirmDialog(
            onAccept = viewModel::acceptAutocal,
            onDismiss = viewModel::dismissAutocal,
        )
    }

    DashboardMenuDrawer(
        drawerOpen = drawerOpen,
        onDrawerOpenChange = { drawerOpen = it },
        recording = uiState.recording,
        isLive = uiState.isLive,
        liveTelemetryEnabled = liveTelemetryEnabled,
        activePresetId = preset.id,
        themeMode = themeMode,
        screenshotMode = screenshotMode,
        onStartRecording = viewModel::startRecording,
        onStopRecording = viewModel::stopRecording,
        onSessionsOpen = onSessionsOpen,
        onRecordingOptions = { showRecordingAdvanced = true },
        onStatsOpen = onStatsOpen,
        onPresetSelected = viewModel::selectPreset,
        onImuManage = onImuManage,
        onStartLive = onStartLive,
        onStopLive = onStopLive,
        onSettingsOpen = onSettingsOpen,
        onAboutOpen = onAboutOpen,
        onThemeToggle = onThemeToggle,
        onScreenshotModeSelected = onScreenshotModeSelected,
    ) {
        InsetAwareScaffold(
            containerColor = GaugeBackground,
        ) { innerPadding ->
            when {
                showSettings -> SettingsScreen(
                    themeMode = themeMode,
                    updateCheckEnabled = updateCheckEnabled,
                    highContrastEnabled = false,
                    liveTelemetryEnabled = liveTelemetryEnabled,
                    audibleTonesEnabled = false,
                    onThemeModeSelect = onThemeModeSelect,
                    onUpdateCheckChange = onUpdateCheckChange,
                    onHighContrastChange = {},
                    onLiveTelemetryChange = {},
                    onAudibleTonesChange = {},
                    onSpeedUnitSelect = {},
                    onLogIntervalSelect = {},
                    onObdDeviceSelect = {},
                    onExternalGpsSelect = {},
                    onImuManage = onImuManage,
                    onCalibrationReset = {},
                    speedUnit = dev.foss.expeditiongauge.settings.SpeedUnit.METRIC,
                    logIntervalMs = 20L,
                    obdDevices = emptyList(),
                    externalGpsDevices = emptyList(),
                    selectedObdAddress = null,
                    selectedExternalGpsAddress = null,
                    onBack = onSettingsClose,
                    modifier = Modifier.fillMaxSize().padding(innerPadding),
                )
                showAbout -> AboutScreen(
                    version = appVersion,
                    installedFormat = installedFormat,
                    updateStatus = updateStatus,
                    donations = donations,
                    canApplyUpdate = canApplyUpdate,
                    onApplyUpdate = onApplyUpdate,
                    onBack = onAboutClose,
                    modifier = Modifier.fillMaxSize().padding(innerPadding),
                )
                else -> Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(GaugeBackground)
                        .padding(innerPadding)
                        .navigationBarPadding(),
                ) {
                    Column(Modifier.fillMaxSize()) {
                        if (uiState.recording) {
                            RecordingLiveStrip()
                            if (uiState.recordingMode == dev.foss.expeditiongauge.recording.RecordingMode.CRAWLING) {
                                Text(
                                    text = stringResource(R.string.recording_mode_crawl),
                                    color = GaugeYellow,
                                    style = MaterialTheme.typography.titleMedium,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = SpacingMd)
                                        .testTag("crawl_badge"),
                                )
                            }
                            LapTimerStrip(
                                state = lapTimingState,
                                visible = lapTimingEnabled && FeatureFlags.lapTimingEnabled,
                                modifier = Modifier.testTag("lap_timer_strip"),
                            )
                        }
                        if (uiState.thermalStatus != dev.foss.expeditiongauge.thermal.ThermalStatus.Normal) {
                            Text(
                                text = stringResource(R.string.thermal_warning),
                                color = GaugeYellow,
                                modifier = Modifier.fillMaxWidth().padding(SpacingMd),
                            )
                        }
                        if (uiState.storageBlocked) {
                            Text(
                                text = stringResource(R.string.storage_cap_blocked),
                                color = GaugeRed,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = SpacingMd)
                                    .testTag("storage_cap_blocked"),
                            )
                        }
                        if (uiState.isLive) {
                            LivePairingSheet(
                                session = uiState.liveSession,
                                receiverCount = uiState.liveReceiverCount,
                                onStopLive = onStopLive,
                            )
                        }
                        DashboardHudLayout(
                            telemetry = telemetry,
                            preset = preset,
                            showDriftAngle = uiState.showDriftAngle,
                            onCalibrate = viewModel::calibrateLevel,
                            onToggleAttitudeDisplay = viewModel::toggleAttitudeDisplay,
                            onCycleInclinometerStyle = viewModel::cycleInclinometerStyle,
                            recording = uiState.recording,
                            crawlingMode = uiState.recordingMode == dev.foss.expeditiongauge.recording.RecordingMode.CRAWLING,
                            tpmsEnabled = tpmsEnabled,
                            pressureUnit = pressureUnit,
                            tempUnit = tempUnit,
                            speedUnit = speedUnit,
                            attitudeGaugeMode = attitudeGaugeMode,
                            inclinometerStyle = inclinometerStyle,
                            activeAlerts = uiState.activeAlerts,
                            maxPitchAlertDeg = maxPitchAlertDeg,
                            maxRollAlertDeg = maxRollAlertDeg,
                            displayRotation = displayRotation,
                            themeMode = themeMode,
                            isLive = uiState.isLive,
                            onMenuClick = { drawerOpen = true },
                            onRecordClick = {
                                if (uiState.recording) viewModel.stopRecording() else viewModel.startRecording()
                            },
                            onMarkEvent = onMarkEvent,
                            onScreenshotClick = { context.captureHudScreenshot(screenshotMode) },
                            modifier = Modifier.weight(1f).fillMaxWidth(),
                        )
                    }
                    RecordingAdvancedSheet(
                        visible = showRecordingAdvanced && uiState.recording,
                        logIntervalHz = (1000 / logIntervalMs.coerceAtLeast(1)).toInt(),
                        onDismiss = { showRecordingAdvanced = false },
                        onAttachCamera = onAttachMediaCamera,
                        onAttachGallery = onAttachMediaGallery,
                        onAttachStub = onAttachMediaStub,
                    )
                    if (!isOnline) {
                        DashboardOfflineBanner(
                            modifier = Modifier
                                .align(androidx.compose.ui.Alignment.BottomCenter)
                                .fillMaxWidth(),
                        )
                    }
                    SnackbarHost(
                        hostState = snackbarHostState,
                        modifier = Modifier.align(androidx.compose.ui.Alignment.BottomCenter),
                    )
                }
            }
        }
    }
}

