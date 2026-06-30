package dev.foss.expeditiongauge.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import dev.foss.expeditiongauge.R
import dev.foss.expeditiongauge.settings.SpeedUnit
import dev.foss.expeditiongauge.ui.theme.SpacingMd
import dev.foss.expeditiongauge.ui.theme.ThemeMode

@OptIn(ExperimentalLayoutApi::class)
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
    highContrastEnabled: Boolean = false,
    liveTelemetryEnabled: Boolean = false,
    audibleTonesEnabled: Boolean = false,
    onHighContrastChange: (Boolean) -> Unit = {},
    onLiveTelemetryChange: (Boolean) -> Unit = {},
    onAudibleTonesChange: (Boolean) -> Unit = {},
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
        Text(text = stringResource(R.string.settings_theme_label))
        FlowRow(horizontalArrangement = Arrangement.spacedBy(SpacingMd)) {
            ThemeMode.entries.forEach { mode ->
                FilterChip(
                    selected = themeMode == mode,
                    onClick = { onThemeModeSelect(mode) },
                    label = {
                        Text(
                            when (mode) {
                                ThemeMode.System -> stringResource(R.string.settings_theme_mode_system)
                                ThemeMode.Light -> stringResource(R.string.settings_theme_mode_light)
                                ThemeMode.Dark -> stringResource(R.string.settings_theme_mode_dark)
                            },
                        )
                    },
                )
            }
        }
        Text(text = stringResource(R.string.settings_units_label))
        FlowRow(horizontalArrangement = Arrangement.spacedBy(SpacingMd)) {
            SpeedUnit.entries.forEach { unit ->
                FilterChip(
                    selected = speedUnit == unit,
                    onClick = { onSpeedUnitSelect(unit) },
                    label = {
                        Text(
                            when (unit) {
                                SpeedUnit.METRIC -> stringResource(R.string.settings_unit_metric)
                                SpeedUnit.IMPERIAL -> stringResource(R.string.settings_unit_imperial)
                            },
                        )
                    },
                )
            }
        }
        Text(text = stringResource(R.string.settings_log_rate_label))
        FlowRow(horizontalArrangement = Arrangement.spacedBy(SpacingMd)) {
            listOf(20L, 50L, 100L).forEach { rate ->
                FilterChip(
                    selected = logIntervalMs == rate,
                    onClick = { onLogIntervalSelect(rate) },
                    label = { Text(stringResource(R.string.settings_log_rate_hz, 1000 / rate)) },
                )
            }
        }
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
            label = stringResource(R.string.settings_audible_tones),
            checked = audibleTonesEnabled,
            onCheckedChange = onAudibleTonesChange,
        )
        SettingsSwitchRow(
            label = stringResource(R.string.settings_live_telemetry),
            checked = liveTelemetryEnabled,
            onCheckedChange = onLiveTelemetryChange,
        )
        Text(text = stringResource(R.string.settings_obd_label))
        FlowRow(horizontalArrangement = Arrangement.spacedBy(SpacingMd)) {
            obdDevices.forEach { (address, name) ->
                FilterChip(
                    selected = selectedObdAddress == address,
                    onClick = { onObdDeviceSelect(address) },
                    label = { Text(name) },
                )
            }
        }
        Text(text = stringResource(R.string.settings_external_gps_label))
        FlowRow(horizontalArrangement = Arrangement.spacedBy(SpacingMd)) {
            externalGpsDevices.forEach { (address, name) ->
                FilterChip(
                    selected = selectedExternalGpsAddress == address,
                    onClick = { onExternalGpsSelect(address) },
                    label = { Text(name) },
                )
            }
        }
        Button(onClick = onImuManage, modifier = Modifier.fillMaxWidth()) {
            Text(stringResource(R.string.imu_management_title))
        }
        Button(onClick = onCalibrationReset, modifier = Modifier.fillMaxWidth()) {
            Text(stringResource(R.string.settings_calibration_reset))
        }
        Button(onClick = onBack) {
            Text(stringResource(R.string.settings_close))
        }
    }
}

@Composable
private fun SettingsSwitchRow(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(SpacingMd),
    ) {
        Text(text = label, modifier = Modifier.weight(1f))
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}
