package dev.foss.expeditiongauge.ui.dashboard

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import dev.foss.expeditiongauge.R
import dev.foss.expeditiongauge.presets.DashboardPreset
import dev.foss.expeditiongauge.presets.DashboardPresetId
import dev.foss.expeditiongauge.ui.theme.GaugeYellow
import dev.foss.expeditiongauge.ui.theme.SpacingMd

@Composable
fun DrawerPresetPage(
    activePresetId: DashboardPresetId,
    onPresetSelected: (DashboardPresetId) -> Unit,
    onBack: () -> Unit,
) {
    Column {
        DrawerMenuItem(
            label = stringResource(R.string.dashboard_menu_back),
            onClick = onBack,
            icon = Icons.AutoMirrored.Filled.ArrowBack,
            testTag = "drawer_back",
        )
        Text(
            text = stringResource(R.string.dashboard_menu_preset),
            color = GaugeYellow,
            modifier = Modifier.padding(vertical = SpacingMd),
        )
        DashboardPreset.all.forEach { preset ->
            val selected = activePresetId == preset.id
            DrawerMenuItem(
                label = presetDrawerLabel(preset.id),
                onClick = { onPresetSelected(preset.id) },
                icon = if (selected) Icons.Filled.Check else null,
                selected = selected,
                testTag = "drawer_preset_${preset.id.name.lowercase()}",
            )
        }
    }
}

@Composable
internal fun presetDrawerLabel(id: DashboardPresetId): String = when (id) {
    DashboardPresetId.Default -> stringResource(R.string.preset_default)
    DashboardPresetId.Drift -> stringResource(R.string.preset_drift)
    DashboardPresetId.Offroad -> stringResource(R.string.preset_offroad)
    DashboardPresetId.Track -> stringResource(R.string.preset_track)
    DashboardPresetId.Minimal -> stringResource(R.string.preset_minimal)
}
