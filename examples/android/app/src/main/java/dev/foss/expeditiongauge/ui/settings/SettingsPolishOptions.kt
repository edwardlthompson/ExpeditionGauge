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

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SettingsPolishOptions(
    lapTimingEnabled: Boolean,
    onLapTimingEnabledChange: (Boolean) -> Unit,
    onTrackSetup: () -> Unit,
    attitudeGaugeMode: AttitudeGaugeMode,
    onAttitudeGaugeModeSelect: (AttitudeGaugeMode) -> Unit,
) {
    if (FeatureFlags.lapTimingEnabled) {
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
    if (FeatureFlags.telemetryGraphsEnabled) {
        Text(text = stringResource(R.string.settings_attitude_gauge_label))
        FlowRow(horizontalArrangement = Arrangement.spacedBy(SpacingMd)) {
            AttitudeGaugeMode.entries.forEach { mode ->
                FilterChip(
                    selected = attitudeGaugeMode == mode,
                    onClick = { onAttitudeGaugeModeSelect(mode) },
                    label = {
                        Text(
                            when (mode) {
                                AttitudeGaugeMode.ATTITUDE ->
                                    stringResource(R.string.settings_attitude_mode_attitude)
                                AttitudeGaugeMode.G_FORCE ->
                                    stringResource(R.string.settings_attitude_mode_gforce)
                                AttitudeGaugeMode.HYBRID ->
                                    stringResource(R.string.settings_attitude_mode_hybrid)
                            },
                        )
                    },
                    modifier = Modifier.testTag(
                        when (mode) {
                            AttitudeGaugeMode.ATTITUDE -> "settings_attitude_mode_attitude"
                            AttitudeGaugeMode.G_FORCE -> "settings_attitude_mode_gforce"
                            AttitudeGaugeMode.HYBRID -> "settings_attitude_mode_hybrid"
                        },
                    ),
                )
            }
        }
    }
}
