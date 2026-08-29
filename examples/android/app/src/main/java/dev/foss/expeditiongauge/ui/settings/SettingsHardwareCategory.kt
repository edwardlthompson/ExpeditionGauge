package dev.foss.expeditiongauge.ui.settings

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import dev.foss.expeditiongauge.FeatureFlags
import dev.foss.expeditiongauge.R
import dev.foss.expeditiongauge.piddiscovery.PidDiscovery
import dev.foss.expeditiongauge.ui.fordmode22.FordMode22CatalogDialog
import dev.foss.expeditiongauge.ui.piddiscovery.PidDiscoveryDialog

@Composable
internal fun SettingsHardwareCategory(
    state: SettingsUiState,
    actions: SettingsUiActions,
) {
    Button(onClick = actions.onCalibrationTips, modifier = Modifier.testTag("settings_calibration_tips")) {
        Text(stringResource(R.string.calibration_tips_open))
    }
    if (FeatureFlags.videoSyncEnabled) {
        Button(
            onClick = actions.onCalibrationWizard,
            modifier = Modifier.testTag("settings_calibration_wizard"),
        ) {
            Text(stringResource(R.string.calibration_wizard_open))
        }
    }
    var showDiscover by remember { mutableStateOf(false) }
    var showFord22 by remember { mutableStateOf(false) }
    Button(
        onClick = {
            actions.onPidDiscover()
            showDiscover = true
        },
        modifier = Modifier.testTag("pid_discovery_action"),
    ) {
        Text(stringResource(R.string.pid_discovery_action))
    }
    if (showDiscover) {
        PidDiscoveryDialog(
            pids = state.pidDiscoveryPids,
            onApply = {
                val pids = state.pidDiscoveryPids
                if (!pids.isNullOrEmpty()) {
                    actions.onObdPidConfigChange(PidDiscovery.applyToConfig(state.obdPidConfig, pids))
                }
                showDiscover = false
            },
            onDismiss = { showDiscover = false },
        )
    }
    Button(
        onClick = { showFord22 = true },
        modifier = Modifier.testTag("ford_mode22_action"),
    ) {
        Text(stringResource(R.string.ford_mode22_action))
    }
    if (showFord22) {
        FordMode22CatalogDialog(onDismiss = { showFord22 = false })
    }
    SettingsHardwareOptions(
        tpmsEnabled = state.tpmsEnabled,
        onTpmsEnabledChange = actions.onTpmsEnabledChange,
        onTpmsManage = actions.onTpmsManage,
        pressureUnit = state.pressureUnit,
        tempUnit = state.tempUnit,
        onPressureUnitSelect = actions.onPressureUnitSelect,
        onTempUnitSelect = actions.onTempUnitSelect,
        obdDevices = state.obdDevices,
        selectedObdAddress = state.selectedObdAddress,
        onObdDeviceSelect = actions.onObdDeviceSelect,
        obdConnectionStatus = state.obdConnectionStatus,
        onObdRetry = actions.onObdRetry,
        onForgetObd = actions.onForgetObd,
        onObdPairNew = actions.onObdPairNew,
        obdPidConfig = state.obdPidConfig,
        onObdPidConfigChange = actions.onObdPidConfigChange,
        externalGpsEnabled = state.externalGpsEnabled,
        onExternalGpsEnabledChange = actions.onExternalGpsEnabledChange,
        externalGpsDevices = state.externalGpsDevices,
        selectedExternalGpsAddress = state.selectedExternalGpsAddress,
        onExternalGpsSelect = actions.onExternalGpsSelect,
        onForgetExternalGps = actions.onForgetExternalGps,
        onImuManage = actions.onImuManage,
        onCalibrationReset = actions.onCalibrationReset,
        autoCalibrateWhenStill = state.autoCalibrateWhenStill,
        onAutoCalibrateWhenStillChange = actions.onAutoCalibrateWhenStillChange,
    )
}
