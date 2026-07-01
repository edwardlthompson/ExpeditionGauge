package dev.foss.expeditiongauge.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import dev.foss.expeditiongauge.FeatureFlags
import dev.foss.expeditiongauge.R
import dev.foss.expeditiongauge.alerts.AlertThresholds
import dev.foss.expeditiongauge.presets.DashboardPresetId
import dev.foss.expeditiongauge.recording.RecordingMode
import dev.foss.expeditiongauge.settings.SpeedUnit
import dev.foss.expeditiongauge.ui.theme.BrightnessMode
import dev.foss.expeditiongauge.ui.theme.SpacingMd
import dev.foss.expeditiongauge.ui.theme.ThemeMode

@Composable
fun SettingsScreen(
    themeMode: ThemeMode,
    updateCheckEnabled: Boolean,
    speedUnit: SpeedUnit,
    logIntervalMs: Long,
    obdDevices: List<Pair<String, String>>,
    externalGpsDevices: List<Pair<String, String>>,
    selectedObdAddress: String?,
    selectedExternalGpsAddress: String?,
    onThemeModeSelect: (ThemeMode) -> Unit,
    onUpdateCheckChange: (Boolean) -> Unit,
    onSpeedUnitSelect: (SpeedUnit) -> Unit,
    onLogIntervalSelect: (Long) -> Unit,
    onObdDeviceSelect: (String) -> Unit,
    onExternalGpsSelect: (String) -> Unit,
    onImuManage: () -> Unit,
    onCalibrationReset: () -> Unit,
    onCalibrationTips: () -> Unit = {},
    onCalibrationWizard: () -> Unit = {},
    developerModeEnabled: Boolean = false,
    onDeveloperModeChange: (Boolean) -> Unit = {},
    onDeveloperModeOpen: () -> Unit = {},
    obdPidConfig: dev.foss.expeditiongauge.settings.ObdPidConfig = dev.foss.expeditiongauge.settings.ObdPidConfig(),
    onObdPidConfigChange: (dev.foss.expeditiongauge.settings.ObdPidConfig) -> Unit = {},
    externalGpsEnabled: Boolean = false,
    onExternalGpsEnabledChange: (Boolean) -> Unit = {},
    onForgetExternalGps: () -> Unit = {},
    tpmsEnabled: Boolean = false,
    onTpmsEnabledChange: (Boolean) -> Unit = {},
    onTpmsManage: () -> Unit = {},
    pressureUnit: dev.foss.expeditiongauge.settings.PressureUnit = dev.foss.expeditiongauge.settings.PressureUnit.PSI,
    tempUnit: dev.foss.expeditiongauge.settings.TempUnit = dev.foss.expeditiongauge.settings.TempUnit.CELSIUS,
    onPressureUnitSelect: (dev.foss.expeditiongauge.settings.PressureUnit) -> Unit = {},
    onTempUnitSelect: (dev.foss.expeditiongauge.settings.TempUnit) -> Unit = {},
    highContrastEnabled: Boolean = false,
    largeTextEnabled: Boolean = false,
    ttsReadoutEnabled: Boolean = false,
    liveTelemetryEnabled: Boolean = false,
    liveSignalWssUrl: String = dev.foss.expeditiongauge.live.LivePairingManager.DEFAULT_SIGNAL_WSS,
    audibleTonesEnabled: Boolean = false,
    onHighContrastChange: (Boolean) -> Unit = {},
    onLargeTextChange: (Boolean) -> Unit = {},
    onTtsReadoutChange: (Boolean) -> Unit = {},
    onLiveTelemetryChange: (Boolean) -> Unit = {},
    onLiveSignalWssUrlChange: (String) -> Unit = {},
    onLiveReceiverOpen: () -> Unit = {},
    onAudibleTonesChange: (Boolean) -> Unit = {},
    androidAutoEnabled: Boolean = false,
    androidAutoMetrics: Set<String> = emptySet(),
    onAndroidAutoEnabledChange: (Boolean) -> Unit = {},
    onAndroidAutoMetricToggle: (String) -> Unit = {},
    mediaCompressionQuality: dev.foss.expeditiongauge.settings.MediaCompressionQuality =
        dev.foss.expeditiongauge.settings.MediaCompressionQuality.BALANCED,
    onMediaCompressionSelect: (dev.foss.expeditiongauge.settings.MediaCompressionQuality) -> Unit = {},
    mediaStorageBytes: Long = 0L,
    autoRecordEnabled: Boolean = false,
    autoRecordDeviceAddresses: Set<String> = emptySet(),
    onAutoRecordEnabledChange: (Boolean) -> Unit = {},
    onAutoRecordDeviceToggle: (String, Boolean) -> Unit = { _, _ -> },
    sessionStoragePercent: Int = 25,
    sessionStorageUsedBytes: Long = 0L,
    sessionStorageAllowedBytes: Long = 0L,
    onSessionStoragePercentChange: (Int) -> Unit = {},
    brightnessMode: BrightnessMode = BrightnessMode.Auto,
    onBrightnessModeSelect: (BrightnessMode) -> Unit = {},
    recordingMode: RecordingMode = RecordingMode.NORMAL,
    onRecordingModeSelect: (RecordingMode) -> Unit = {},
    lapTimingEnabled: Boolean = false,
    onLapTimingEnabledChange: (Boolean) -> Unit = {},
    onTrackSetup: () -> Unit = {},
    attitudeGaugeMode: dev.foss.expeditiongauge.gauge.AttitudeGaugeMode =
        dev.foss.expeditiongauge.gauge.AttitudeGaugeMode.ATTITUDE,
    onAttitudeGaugeModeSelect: (dev.foss.expeditiongauge.gauge.AttitudeGaugeMode) -> Unit = {},
    alertThresholds: AlertThresholds = AlertThresholds(),
    onAlertThresholdsChange: (AlertThresholds) -> Unit = {},
    activePresetId: DashboardPresetId = DashboardPresetId.Default,
    onPresetSelected: (DashboardPresetId) -> Unit = {},
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .padding(SpacingMd)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(SpacingMd),
    ) {
        Text(
            text = stringResource(R.string.settings_title),
            style = MaterialTheme.typography.headlineSmall,
        )
        SettingsGeneralSections(
            themeMode = themeMode,
            onThemeModeSelect = onThemeModeSelect,
            brightnessMode = brightnessMode,
            onBrightnessModeSelect = onBrightnessModeSelect,
            speedUnit = speedUnit,
            onSpeedUnitSelect = onSpeedUnitSelect,
            logIntervalMs = logIntervalMs,
            onLogIntervalSelect = onLogIntervalSelect,
            recordingMode = recordingMode,
            onRecordingModeSelect = onRecordingModeSelect,
        )
        SettingsPresetOptions(
            activePresetId = activePresetId,
            onPresetSelected = onPresetSelected,
        )
        SettingsPolishOptions(
            lapTimingEnabled = lapTimingEnabled,
            onLapTimingEnabledChange = onLapTimingEnabledChange,
            onTrackSetup = onTrackSetup,
            attitudeGaugeMode = attitudeGaugeMode,
            onAttitudeGaugeModeSelect = onAttitudeGaugeModeSelect,
        )
        SettingsAlertOptions(
            thresholds = alertThresholds,
            onThresholdsChange = onAlertThresholdsChange,
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(SpacingMd),
        ) {
            Text(
                text = stringResource(R.string.settings_update_check_label),
                modifier = Modifier.weight(1f),
            )
            Switch(checked = updateCheckEnabled, onCheckedChange = onUpdateCheckChange)
        }
        SettingsSwitchRow(
            label = stringResource(R.string.settings_high_contrast),
            checked = highContrastEnabled,
            onCheckedChange = onHighContrastChange,
        )
        SettingsSwitchRow(
            label = stringResource(R.string.settings_large_text),
            checked = largeTextEnabled,
            onCheckedChange = onLargeTextChange,
            modifier = Modifier.testTag("settings_large_text"),
        )
        SettingsSwitchRow(
            label = stringResource(R.string.settings_tts_readout),
            checked = ttsReadoutEnabled,
            onCheckedChange = onTtsReadoutChange,
            modifier = Modifier.testTag("settings_tts_readout"),
        )
        SettingsSwitchRow(
            label = stringResource(R.string.settings_audible_tones),
            checked = audibleTonesEnabled,
            onCheckedChange = onAudibleTonesChange,
        )
        Button(onClick = onCalibrationTips, modifier = Modifier.testTag("settings_calibration_tips")) {
            Text(stringResource(R.string.calibration_tips_open))
        }
        if (FeatureFlags.videoSyncEnabled) {
            Button(onClick = onCalibrationWizard, modifier = Modifier.testTag("settings_calibration_wizard")) {
                Text(stringResource(R.string.calibration_wizard_open))
            }
        }
        SettingsSwitchRow(
            label = stringResource(R.string.developer_mode_enable),
            checked = developerModeEnabled,
            onCheckedChange = onDeveloperModeChange,
            modifier = Modifier.testTag("settings_developer_mode"),
        )
        if (developerModeEnabled) {
            Button(onClick = onDeveloperModeOpen, modifier = Modifier.testTag("settings_developer_open")) {
                Text(stringResource(R.string.developer_mode_open))
            }
        }
        SettingsSwitchRow(
            label = stringResource(R.string.settings_live_telemetry),
            checked = liveTelemetryEnabled,
            onCheckedChange = onLiveTelemetryChange,
            modifier = Modifier.testTag("settings_live_telemetry"),
        )
        if (liveTelemetryEnabled) {
            OutlinedTextField(
                value = liveSignalWssUrl,
                onValueChange = onLiveSignalWssUrlChange,
                label = { Text(stringResource(R.string.live_signal_url_hint)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("settings_live_signal_url"),
                singleLine = true,
            )
            Button(onClick = onLiveReceiverOpen, modifier = Modifier.testTag("settings_live_receiver")) {
                Text(stringResource(R.string.live_receiver_open))
            }
        }
        SettingsAndroidAutoOptions(
            androidAutoEnabled = androidAutoEnabled,
            onAndroidAutoEnabledChange = onAndroidAutoEnabledChange,
            allowedMetrics = androidAutoMetrics,
            onToggleMetric = onAndroidAutoMetricToggle,
        )
        SettingsMediaOptions(
            compressionQuality = mediaCompressionQuality,
            onCompressionSelect = onMediaCompressionSelect,
            storageBytes = mediaStorageBytes,
        )
        SettingsStorageOptions(
            storagePercent = sessionStoragePercent,
            usedBytes = sessionStorageUsedBytes,
            allowedBytes = sessionStorageAllowedBytes,
            onPercentChange = onSessionStoragePercentChange,
        )
        SettingsRecordingOptions(
            autoRecordEnabled = autoRecordEnabled,
            selectedAddresses = autoRecordDeviceAddresses,
            onAutoRecordEnabledChange = onAutoRecordEnabledChange,
            onDeviceToggle = onAutoRecordDeviceToggle,
        )
        SettingsHardwareOptions(
            tpmsEnabled = tpmsEnabled,
            onTpmsEnabledChange = onTpmsEnabledChange,
            onTpmsManage = onTpmsManage,
            pressureUnit = pressureUnit,
            tempUnit = tempUnit,
            onPressureUnitSelect = onPressureUnitSelect,
            onTempUnitSelect = onTempUnitSelect,
            obdDevices = obdDevices,
            selectedObdAddress = selectedObdAddress,
            onObdDeviceSelect = onObdDeviceSelect,
            obdPidConfig = obdPidConfig,
            onObdPidConfigChange = onObdPidConfigChange,
            externalGpsEnabled = externalGpsEnabled,
            onExternalGpsEnabledChange = onExternalGpsEnabledChange,
            externalGpsDevices = externalGpsDevices,
            selectedExternalGpsAddress = selectedExternalGpsAddress,
            onExternalGpsSelect = onExternalGpsSelect,
            onForgetExternalGps = onForgetExternalGps,
            onImuManage = onImuManage,
            onCalibrationReset = onCalibrationReset,
        )
        Button(onClick = onBack) {
            Text(stringResource(R.string.settings_close))
        }
    }
}
