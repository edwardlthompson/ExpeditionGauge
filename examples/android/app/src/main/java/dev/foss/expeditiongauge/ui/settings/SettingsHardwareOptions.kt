package dev.foss.expeditiongauge.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import dev.foss.expeditiongauge.R
import dev.foss.expeditiongauge.settings.ObdPidConfig
import dev.foss.expeditiongauge.settings.PressureUnit
import dev.foss.expeditiongauge.settings.TempUnit
import dev.foss.expeditiongauge.ui.theme.SpacingMd

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SettingsHardwareOptions(
    tpmsEnabled: Boolean,
    onTpmsEnabledChange: (Boolean) -> Unit,
    onTpmsManage: () -> Unit,
    pressureUnit: PressureUnit,
    tempUnit: TempUnit,
    onPressureUnitSelect: (PressureUnit) -> Unit,
    onTempUnitSelect: (TempUnit) -> Unit,
    obdDevices: List<Pair<String, String>>,
    selectedObdAddress: String?,
    onObdDeviceSelect: (String) -> Unit,
    obdConnectionStatus: String? = null,
    onObdRetry: () -> Unit = {},
    onForgetObd: () -> Unit = {},
    onObdPairNew: () -> Unit = {},
    obdPidConfig: ObdPidConfig,
    onObdPidConfigChange: (ObdPidConfig) -> Unit,
    externalGpsEnabled: Boolean,
    onExternalGpsEnabledChange: (Boolean) -> Unit,
    externalGpsDevices: List<Pair<String, String>>,
    selectedExternalGpsAddress: String?,
    onExternalGpsSelect: (String) -> Unit,
    onForgetExternalGps: () -> Unit,
    onImuManage: () -> Unit,
    onCalibrationReset: () -> Unit,
    autoCalibrateWhenStill: Boolean = true,
    onAutoCalibrateWhenStillChange: (Boolean) -> Unit = {},
) {
    SettingsSwitchRow(
        label = stringResource(R.string.settings_tpms_enable),
        checked = tpmsEnabled,
        onCheckedChange = onTpmsEnabledChange,
        modifier = Modifier.testTag("settings_tpms_enable"),
    )
    Button(
        onClick = onTpmsManage,
        modifier = Modifier.fillMaxWidth().testTag("settings_tpms_manage"),
    ) {
        Text(stringResource(R.string.tpms_management_title))
    }
    if (tpmsEnabled) {
        Text(text = stringResource(R.string.settings_pressure_unit_label))
        FlowRow(horizontalArrangement = Arrangement.spacedBy(SpacingMd)) {
            FilterChip(
                selected = pressureUnit == PressureUnit.PSI,
                onClick = { onPressureUnitSelect(PressureUnit.PSI) },
                label = { Text(stringResource(R.string.settings_pressure_psi)) },
            )
            FilterChip(
                selected = pressureUnit == PressureUnit.KPA,
                onClick = { onPressureUnitSelect(PressureUnit.KPA) },
                label = { Text(stringResource(R.string.settings_pressure_kpa)) },
            )
        }
        Text(text = stringResource(R.string.settings_temp_unit_label))
        FlowRow(horizontalArrangement = Arrangement.spacedBy(SpacingMd)) {
            FilterChip(
                selected = tempUnit == TempUnit.CELSIUS,
                onClick = { onTempUnitSelect(TempUnit.CELSIUS) },
                label = { Text(stringResource(R.string.settings_temp_celsius)) },
            )
            FilterChip(
                selected = tempUnit == TempUnit.FAHRENHEIT,
                onClick = { onTempUnitSelect(TempUnit.FAHRENHEIT) },
                label = { Text(stringResource(R.string.settings_temp_fahrenheit)) },
            )
        }
    }
    Text(text = stringResource(R.string.settings_obd_label))
    Text(text = stringResource(R.string.settings_obd_pair_hint))
    FlowRow(horizontalArrangement = Arrangement.spacedBy(SpacingMd)) {
        obdDevices.forEach { (address, name) ->
            FilterChip(
                selected = selectedObdAddress == address,
                onClick = { onObdDeviceSelect(address) },
                label = { Text(name) },
                modifier = Modifier.testTag("obd_device_$address"),
            )
        }
    }
    Button(
        onClick = onObdPairNew,
        modifier = Modifier.fillMaxWidth().testTag("settings_obd_pair_new"),
    ) {
        Text(stringResource(R.string.settings_obd_pair_new))
    }
    obdConnectionStatus?.let { status ->
        Text(text = status, modifier = Modifier.fillMaxWidth().testTag("settings_obd_status"))
    }
    if (selectedObdAddress != null) {
        FlowRow(horizontalArrangement = Arrangement.spacedBy(SpacingMd)) {
            Button(onClick = onObdRetry, modifier = Modifier.testTag("settings_obd_retry")) {
                Text(stringResource(R.string.settings_obd_retry))
            }
            Button(onClick = onForgetObd, modifier = Modifier.testTag("settings_obd_forget")) {
                Text(stringResource(R.string.settings_obd_forget))
            }
        }
    }
    Text(text = stringResource(R.string.settings_obd_pids_label))
    SettingsSwitchRow(
        label = stringResource(R.string.settings_obd_pid_rpm),
        checked = obdPidConfig.rpm,
        onCheckedChange = { onObdPidConfigChange(obdPidConfig.copy(rpm = it)) },
    )
    SettingsSwitchRow(
        label = stringResource(R.string.settings_obd_pid_speed),
        checked = obdPidConfig.speed,
        onCheckedChange = { onObdPidConfigChange(obdPidConfig.copy(speed = it)) },
    )
    SettingsSwitchRow(
        label = stringResource(R.string.settings_obd_pid_throttle),
        checked = obdPidConfig.throttle,
        onCheckedChange = { onObdPidConfigChange(obdPidConfig.copy(throttle = it)) },
    )
    SettingsSwitchRow(
        label = stringResource(R.string.settings_obd_pid_load),
        checked = obdPidConfig.load,
        onCheckedChange = { onObdPidConfigChange(obdPidConfig.copy(load = it)) },
    )
    SettingsSwitchRow(
        label = stringResource(R.string.settings_obd_pid_voltage),
        checked = obdPidConfig.voltage,
        onCheckedChange = { onObdPidConfigChange(obdPidConfig.copy(voltage = it)) },
    )
    SettingsSwitchRow(
        label = stringResource(R.string.settings_obd_pid_rear_wheels),
        checked = obdPidConfig.rearWheels,
        onCheckedChange = { onObdPidConfigChange(obdPidConfig.copy(rearWheels = it)) },
    )
    Text(text = stringResource(R.string.settings_external_gps_label))
    SettingsSwitchRow(
        label = stringResource(R.string.settings_external_gps_enable),
        checked = externalGpsEnabled,
        onCheckedChange = onExternalGpsEnabledChange,
        modifier = Modifier.testTag("settings_external_gps_enable"),
    )
    if (externalGpsEnabled) {
        FlowRow(horizontalArrangement = Arrangement.spacedBy(SpacingMd)) {
            externalGpsDevices.forEach { (address, name) ->
                FilterChip(
                    selected = selectedExternalGpsAddress == address,
                    onClick = { onExternalGpsSelect(address) },
                    label = { Text(name) },
                    modifier = Modifier.testTag("external_gps_device_$address"),
                )
            }
        }
        if (selectedExternalGpsAddress != null) {
            Button(
                onClick = onForgetExternalGps,
                modifier = Modifier.fillMaxWidth().testTag("settings_external_gps_forget"),
            ) {
                Text(stringResource(R.string.settings_external_gps_forget))
            }
        }
    }
    Button(
        onClick = onImuManage,
        modifier = Modifier.fillMaxWidth().testTag("settings_imu_manage"),
    ) {
        Text(stringResource(R.string.imu_management_title))
    }
    Button(onClick = onCalibrationReset, modifier = Modifier.fillMaxWidth()) {
        Text(stringResource(R.string.settings_calibration_reset))
    }
    SettingsSwitchRow(
        label = stringResource(R.string.settings_auto_calibrate),
        checked = autoCalibrateWhenStill,
        onCheckedChange = onAutoCalibrateWhenStillChange,
        modifier = Modifier.testTag("settings_auto_calibrate"),
    )
    Text(
        text = stringResource(R.string.settings_auto_calibrate_hint),
        modifier = Modifier.fillMaxWidth(),
    )
}
