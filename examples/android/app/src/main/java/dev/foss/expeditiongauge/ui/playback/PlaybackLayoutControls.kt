package dev.foss.expeditiongauge.ui.playback

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import dev.foss.expeditiongauge.R
import dev.foss.expeditiongauge.playback.PlaybackLayoutState
import dev.foss.expeditiongauge.ui.theme.GaugeScaleWhite
import dev.foss.expeditiongauge.ui.theme.SpacingMd
import kotlin.math.abs

object PlaybackLayoutPresets {
    const val MAP_FOCUS = 0.7f
    const val BALANCED = 0.6f
    const val GAUGES_FOCUS = 0.3f

    val all = listOf(MAP_FOCUS, BALANCED, GAUGES_FOCUS)

    fun labelRes(weight: Float): Int = when {
        abs(weight - MAP_FOCUS) < 0.05f -> R.string.playback_layout_map_focus
        abs(weight - GAUGES_FOCUS) < 0.05f -> R.string.playback_layout_gauges_focus
        else -> R.string.playback_layout_balanced
    }

    fun testTag(weight: Float): String = when {
        abs(weight - MAP_FOCUS) < 0.05f -> "playback_layout_map_focus"
        abs(weight - GAUGES_FOCUS) < 0.05f -> "playback_layout_gauges_focus"
        else -> "playback_layout_balanced"
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PlaybackLayoutControls(
    mapWeight: Float,
    graphsExpanded: Boolean,
    graphsToggleEnabled: Boolean,
    onMapWeightPreset: (Float) -> Unit,
    onGraphsExpandedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .testTag("playback_layout_controls")
            .semantics { contentDescription = "playback layout map weight $mapWeight" },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(SpacingMd),
    ) {
        Text(
            text = stringResource(R.string.playback_layout_title),
            color = GaugeScaleWhite,
            style = MaterialTheme.typography.labelMedium,
        )
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(SpacingMd),
            modifier = Modifier.weight(1f),
        ) {
            PlaybackLayoutPresets.all.forEach { preset ->
                val selected = abs(mapWeight - preset) < 0.05f
                FilterChip(
                    selected = selected,
                    onClick = { onMapWeightPreset(preset) },
                    label = {
                        Text(
                            text = stringResource(PlaybackLayoutPresets.labelRes(preset)),
                            style = MaterialTheme.typography.labelSmall,
                        )
                    },
                    modifier = Modifier.testTag(PlaybackLayoutPresets.testTag(preset)),
                )
            }
        }
        if (graphsToggleEnabled) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(SpacingMd),
            ) {
                Text(
                    text = stringResource(R.string.playback_layout_graphs),
                    color = GaugeScaleWhite,
                    style = MaterialTheme.typography.labelSmall,
                )
                Switch(
                    checked = graphsExpanded,
                    onCheckedChange = onGraphsExpandedChange,
                    modifier = Modifier.testTag("playback_layout_graphs_toggle"),
                )
            }
        }
    }
}
