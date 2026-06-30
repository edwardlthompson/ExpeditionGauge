package dev.foss.expeditiongauge.ui.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import dev.foss.expeditiongauge.R
import dev.foss.expeditiongauge.presets.DashboardPreset
import dev.foss.expeditiongauge.presets.DashboardPresetId

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PresetSwitcherChip(
    activePresetId: DashboardPresetId,
    isRecording: Boolean,
    onPresetSelected: (DashboardPresetId) -> Unit,
    modifier: Modifier = Modifier,
) {
    FlowRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(dev.foss.expeditiongauge.ui.theme.SpacingMd),
    ) {
        DashboardPreset.all.forEach { preset ->
            FilterChip(
                selected = activePresetId == preset.id,
                onClick = { onPresetSelected(preset.id) },
                label = {
                    Text(
                        text = presetLabel(preset.id),
                        style = MaterialTheme.typography.labelMedium,
                    )
                },
                enabled = isRecording || activePresetId == preset.id,
            )
        }
    }
}

@Composable
private fun presetLabel(id: DashboardPresetId): String = when (id) {
    DashboardPresetId.Default -> stringResource(R.string.preset_default)
    DashboardPresetId.Drift -> stringResource(R.string.preset_drift)
    DashboardPresetId.Offroad -> stringResource(R.string.preset_offroad)
    DashboardPresetId.Track -> stringResource(R.string.preset_track)
    DashboardPresetId.Minimal -> stringResource(R.string.preset_minimal)
}
