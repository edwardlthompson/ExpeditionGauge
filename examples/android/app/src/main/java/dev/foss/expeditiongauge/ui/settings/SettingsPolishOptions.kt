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
import dev.foss.expeditiongauge.FeatureFlags
import dev.foss.expeditiongauge.R
import dev.foss.expeditiongauge.gauge.AttitudeGaugeMode
import dev.foss.expeditiongauge.ui.theme.SpacingMd

@Composable
fun SettingsLapTimingOptions(
    lapTimingEnabled: Boolean,
    onLapTimingEnabledChange: (Boolean) -> Unit,
    onTrackSetup: () -> Unit,
) {
    if (!FeatureFlags.lapTimingEnabled) return
    SettingsSwitchRow(
        label = stringResource(R.string.settings_lap_timing_enable),
        checked = lapTimingEnabled,
        onCheckedChange = onLapTimingEnabledChange,
        modifier = Modifier.testTag("settings_lap_timing_enable"),
    )
    Button(
        onClick = onTrackSetup,
        modifier = Modifier.fillMaxWidth().testTag("settings_track_setup"),
    ) {
        Text(stringResource(R.string.track_setup_open))
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SettingsPolishOptions(
    attitudeGaugeMode: AttitudeGaugeMode,
    onAttitudeGaugeModeSelect: (AttitudeGaugeMode) -> Unit,
) {
    if (FeatureFlags.telemetryGraphsEnabled) {
        Text(text = stringResource(R.string.settings_attitude_gauge_label))
        FlowRow(horizontalArrangement = Arrangement.spacedBy(SpacingMd)) {
            AttitudeGaugeMode.DISPLAY_CYCLE.forEach { mode ->
                FilterChip(
                    selected = attitudeGaugeMode == mode,
                    onClick = { onAttitudeGaugeModeSelect(mode) },
                    label = { Text(attitudeModeLabel(mode)) },
                    modifier = Modifier.testTag(attitudeModeTestTag(mode)),
                )
            }
        }
    }
}

@Composable
private fun attitudeModeLabel(mode: AttitudeGaugeMode): String = when (mode) {
    AttitudeGaugeMode.G_FORCE -> stringResource(R.string.settings_attitude_mode_gforce)
    AttitudeGaugeMode.INCLINOMETER_LADDER -> stringResource(R.string.settings_attitude_mode_ladder)
    AttitudeGaugeMode.INCLINOMETER_HORIZON -> stringResource(R.string.settings_attitude_mode_horizon)
    AttitudeGaugeMode.INCLINOMETER_DUAL_DIAL -> stringResource(R.string.settings_attitude_mode_dual_dial)
    AttitudeGaugeMode.INCLINOMETER_BUBBLE -> stringResource(R.string.settings_attitude_mode_bubble)
    AttitudeGaugeMode.COMPASS_BALL -> stringResource(R.string.settings_attitude_mode_compass)
}

private fun attitudeModeTestTag(mode: AttitudeGaugeMode): String = when (mode) {
    AttitudeGaugeMode.G_FORCE -> "settings_attitude_mode_gforce"
    AttitudeGaugeMode.INCLINOMETER_LADDER -> "settings_attitude_mode_ladder"
    AttitudeGaugeMode.INCLINOMETER_HORIZON -> "settings_attitude_mode_horizon"
    AttitudeGaugeMode.INCLINOMETER_DUAL_DIAL -> "settings_attitude_mode_dual_dial"
    AttitudeGaugeMode.INCLINOMETER_BUBBLE -> "settings_attitude_mode_bubble"
    AttitudeGaugeMode.COMPASS_BALL -> "settings_attitude_mode_compass"
}
