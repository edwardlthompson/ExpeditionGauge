package dev.foss.expeditiongauge.ui.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.foss.expeditiongauge.FeatureFlags
import dev.foss.expeditiongauge.R
import dev.foss.expeditiongauge.about.DonationsConfig
import dev.foss.expeditiongauge.ui.about.AboutScreen
import dev.foss.expeditiongauge.ui.components.ThemeToggle
import dev.foss.expeditiongauge.ui.components.gauge.ImuStatusStrip
import dev.foss.expeditiongauge.ui.components.gauge.RecordControls
import dev.foss.expeditiongauge.ui.live.LivePairingSheet
import dev.foss.expeditiongauge.timing.PredictiveTimingState
import dev.foss.expeditiongauge.ui.components.LapTimerStrip
import dev.foss.expeditiongauge.ui.recording.MarkEventFab
import dev.foss.expeditiongauge.ui.recording.RecordingAdvancedSheet
import dev.foss.expeditiongauge.ui.recording.RecordingLiveStrip
import dev.foss.expeditiongauge.ui.settings.SettingsScreen
import dev.foss.expeditiongauge.ui.theme.GaugeBackground
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
    onStartLive: () -> Unit,
    onStopLive: () -> Unit,
    tpmsEnabled: Boolean = false,
    pressureUnit: dev.foss.expeditiongauge.settings.PressureUnit = dev.foss.expeditiongauge.settings.PressureUnit.PSI,
    tempUnit: dev.foss.expeditiongauge.settings.TempUnit = dev.foss.expeditiongauge.settings.TempUnit.CELSIUS,
    logIntervalMs: Long = 20L,
    lapTimingEnabled: Boolean = false,
    lapTimingState: PredictiveTimingState = PredictiveTimingState(),
    attitudeGaugeMode: dev.foss.expeditiongauge.gauge.AttitudeGaugeMode =
        dev.foss.expeditiongauge.gauge.AttitudeGaugeMode.ATTITUDE,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val telemetry = uiState.telemetry
    val preset = uiState.activePreset
    var showRecordingAdvanced by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = GaugeBackground,
        floatingActionButton = {
            if (FeatureFlags.markEventEnabled) {
                MarkEventFab(visible = uiState.recording, onMarkEvent = onMarkEvent)
            }
        },
        topBar = {
            TopAppBar(
                title = {
                    if (uiState.isLive) {
                        Text(text = stringResource(R.string.live_banner), color = GaugeYellow)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = GaugeBackground,
                    navigationIconContentColor = GaugeYellow,
                    actionIconContentColor = GaugeYellow,
                ),
                actions = {
                    IconButton(onClick = onStatsOpen) {
                        Icon(Icons.Filled.BarChart, contentDescription = stringResource(R.string.stats_open))
                    }
                    IconButton(onClick = onSettingsOpen) {
                        Icon(Icons.Filled.Settings, contentDescription = stringResource(R.string.settings_open))
                    }
                    IconButton(onClick = onAboutOpen) {
                        Icon(Icons.Filled.Info, contentDescription = stringResource(R.string.about_open))
                    }
                    ThemeToggle(themeMode = themeMode, onToggle = onThemeToggle)
                },
            )
        },
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
            else -> Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(GaugeBackground)
                    .padding(innerPadding),
            ) {
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
                if (!uiState.recording) {
                    ImuStatusStrip(
                        statuses = telemetry.imuStatuses,
                        onManageClick = onImuManage,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
                if (uiState.thermalStatus != dev.foss.expeditiongauge.thermal.ThermalStatus.Normal) {
                    Text(
                        text = stringResource(R.string.thermal_warning),
                        color = GaugeYellow,
                        modifier = Modifier.fillMaxWidth().padding(SpacingMd),
                    )
                }
                if (FeatureFlags.dashboardPresetsEnabled) {
                    PresetSwitcherChip(
                        activePresetId = preset.id,
                        isRecording = uiState.recording,
                        onPresetSelected = viewModel::selectPreset,
                        modifier = Modifier.fillMaxWidth().padding(horizontal = SpacingMd),
                    )
                }
                if (liveTelemetryEnabled && FeatureFlags.liveTelemetryEnabled && !uiState.isLive) {
                    TextButton(
                        onClick = onStartLive,
                        modifier = Modifier
                            .padding(horizontal = SpacingMd)
                            .testTag("live_start_button"),
                    ) {
                        Text(stringResource(R.string.live_start))
                    }
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
                    recording = uiState.recording,
                    crawlingMode = uiState.recordingMode == dev.foss.expeditiongauge.recording.RecordingMode.CRAWLING,
                    tpmsEnabled = tpmsEnabled,
                    pressureUnit = pressureUnit,
                    tempUnit = tempUnit,
                    attitudeGaugeMode = attitudeGaugeMode,
                    activeAlerts = uiState.activeAlerts,
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                )
                RecordControls(
                    recording = uiState.recording,
                    onRecord = viewModel::startRecording,
                    onStop = viewModel::stopRecording,
                    onSessions = onSessionsOpen,
                    onAdvancedOptions = { showRecordingAdvanced = true },
                )
                RecordingAdvancedSheet(
                    visible = showRecordingAdvanced && uiState.recording,
                    logIntervalHz = (1000 / logIntervalMs.coerceAtLeast(1)).toInt(),
                    onDismiss = { showRecordingAdvanced = false },
                )
                if (!isOnline) {
                    DashboardOfflineBanner()
                }
            }
        }
    }
}
