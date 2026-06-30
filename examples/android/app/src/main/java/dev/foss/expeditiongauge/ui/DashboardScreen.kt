package dev.foss.expeditiongauge.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.foss.expeditiongauge.FeatureFlags
import dev.foss.expeditiongauge.R
import dev.foss.expeditiongauge.about.DonationsConfig
import dev.foss.expeditiongauge.ui.about.AboutScreen
import dev.foss.expeditiongauge.ui.components.ThemeToggle
import dev.foss.expeditiongauge.ui.components.gauge.AttitudeGMeterGauge
import dev.foss.expeditiongauge.ui.components.gauge.GpsReadoutPanel
import dev.foss.expeditiongauge.ui.components.gauge.GpsStatusChip
import dev.foss.expeditiongauge.ui.components.gauge.HeadingReadout
import dev.foss.expeditiongauge.ui.components.gauge.ImuStatusStrip
import dev.foss.expeditiongauge.ui.components.gauge.RecordControls
import dev.foss.expeditiongauge.ui.components.gauge.SpeedometerGauge
import dev.foss.expeditiongauge.ui.components.gauge.StatusIcons
import dev.foss.expeditiongauge.ui.components.gauge.TirePressurePanel
import dev.foss.expeditiongauge.ui.dashboard.DashboardViewModel
import dev.foss.expeditiongauge.ui.dashboard.PresetSwitcherChip
import dev.foss.expeditiongauge.ui.live.LivePairingSheet
import dev.foss.expeditiongauge.ui.recording.MarkEventFab
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
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val telemetry = uiState.telemetry
    val preset = uiState.activePreset

    Scaffold(
        containerColor = GaugeBackground,
        floatingActionButton = {
            MarkEventFab(visible = uiState.recording, onMarkEvent = onMarkEvent)
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
                ImuStatusStrip(
                    statuses = telemetry.imuStatuses,
                    onManageClick = onImuManage,
                    modifier = Modifier.fillMaxWidth(),
                )
                if (uiState.thermalStatus != dev.foss.expeditiongauge.thermal.ThermalStatus.Normal) {
                    Text(
                        text = stringResource(R.string.thermal_warning),
                        color = GaugeYellow,
                        modifier = Modifier.fillMaxWidth().padding(SpacingMd),
                    )
                }
                PresetSwitcherChip(
                    activePresetId = preset.id,
                    isRecording = uiState.recording,
                    onPresetSelected = viewModel::selectPreset,
                    modifier = Modifier.fillMaxWidth().padding(horizontal = SpacingMd),
                )
                if (liveTelemetryEnabled && FeatureFlags.liveTelemetryEnabled && !uiState.isLive) {
                    TextButton(onClick = onStartLive, modifier = Modifier.padding(horizontal = SpacingMd)) {
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
                Row(modifier = Modifier.weight(1f).fillMaxWidth()) {
                    if (preset.showAttitude && preset.weights.attitude > 0f) {
                        Box(
                            modifier = Modifier.weight(preset.weights.attitude).fillMaxHeight(),
                            contentAlignment = Alignment.Center,
                        ) {
                            AttitudeGMeterGauge(
                                pitchDeg = telemetry.pitchDeg,
                                rollDeg = telemetry.rollDeg,
                                onCalibrate = viewModel::calibrateLevel,
                            )
                        }
                    }
                    if (preset.showSpeed || preset.showHeading || preset.showGps) {
                        Column(
                            modifier = Modifier.weight(preset.weights.center.coerceAtLeast(0.1f)).fillMaxHeight(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            if (preset.showSpeed) SpeedometerGauge(speedMps = telemetry.speedMps)
                            if (preset.showHeading) HeadingReadout(headingDeg = telemetry.headingDeg)
                            if (preset.showGps) {
                                GpsStatusChip(
                                    gpsFix = telemetry.gpsFix,
                                    gpsSource = telemetry.gpsSource,
                                    numSatellites = telemetry.numSatellites,
                                    hdop = telemetry.hdop,
                                )
                                GpsReadoutPanel(
                                    latitude = telemetry.latitude,
                                    longitude = telemetry.longitude,
                                    altitudeM = telemetry.altitudeM,
                                    driftAngleDeg = telemetry.driftAngleDeg,
                                    showDriftAngle = uiState.showDriftAngle || preset.emphasizeDrift,
                                )
                            }
                            telemetry.slipRatio?.let { slip ->
                                Text(
                                    text = stringResource(R.string.gauge_slip_ratio, slip),
                                    color = GaugeYellow,
                                    style = MaterialTheme.typography.bodySmall,
                                )
                            }
                        }
                    }
                    if (preset.showTirePressure && FeatureFlags.tpmsEnabled) {
                        Column(modifier = Modifier.weight(preset.weights.side.coerceAtLeast(0.1f)).fillMaxHeight()) {
                            TirePressurePanel(
                                frontLeft = telemetry.frontLeftPressure,
                                frontRight = telemetry.frontRightPressure,
                                rearLeft = telemetry.rearLeftPressure,
                                rearRight = telemetry.rearRightPressure,
                                modifier = Modifier.weight(1f),
                            )
                            StatusIcons(gpsFix = telemetry.gpsFix, batteryVoltage = telemetry.batteryVoltage)
                        }
                    }
                }
                RecordControls(
                    recording = uiState.recording,
                    onRecord = viewModel::startRecording,
                    onStop = viewModel::stopRecording,
                    onSessions = onSessionsOpen,
                )
                if (!isOnline) {
                    Text(
                        text = stringResource(R.string.app_status_offline),
                        style = MaterialTheme.typography.bodySmall,
                        color = GaugeYellow,
                        modifier = Modifier.padding(SpacingMd),
                    )
                }
            }
        }
    }
}
