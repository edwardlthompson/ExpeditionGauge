package dev.foss.expeditiongauge.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import dev.foss.expeditiongauge.FeatureFlags
import dev.foss.expeditiongauge.R
import dev.foss.expeditiongauge.presets.DashboardPreset
import dev.foss.expeditiongauge.presets.DashboardPresetId
import dev.foss.expeditiongauge.ui.theme.SpacingMd

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SettingsPresetOptions(
    activePresetId: DashboardPresetId,
    onPresetSelected: (DashboardPresetId) -> Unit,
) {
    if (!FeatureFlags.dashboardPresetsEnabled) return
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(SpacingMd),
    ) {
        Text(
            text = stringResource(R.string.settings_presets_title),
            style = MaterialTheme.typography.titleSmall,
        )
        Text(
            text = stringResource(R.string.settings_presets_hint),
            style = MaterialTheme.typography.bodySmall,
        )
        FlowRow(horizontalArrangement = Arrangement.spacedBy(SpacingMd)) {
            DashboardPreset.all.forEach { preset ->
                FilterChip(
                    selected = activePresetId == preset.id,
                    onClick = { onPresetSelected(preset.id) },
                    label = { Text(presetLabel(preset.id)) },
                    modifier = Modifier.testTag("settings_preset_${preset.id.name.lowercase()}"),
                )
            }
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
