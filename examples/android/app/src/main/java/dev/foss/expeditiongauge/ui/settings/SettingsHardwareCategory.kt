package dev.foss.expeditiongauge.ui.settings

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import dev.foss.expeditiongauge.FeatureFlags
import dev.foss.expeditiongauge.R

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
