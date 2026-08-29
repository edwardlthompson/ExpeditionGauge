package dev.foss.expeditiongauge.ui.playback

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import dev.foss.expeditiongauge.R
import dev.foss.expeditiongauge.playback.HeatmapMetric
import dev.foss.expeditiongauge.playback.RouteHeatmapLayer
import dev.foss.expeditiongauge.ui.theme.SpacingMd

@Composable
fun RouteHeatmapControls(
    selected: HeatmapMetric,
    onSelect: (HeatmapMetric) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(modifier = modifier.padding(SpacingMd)) {
        HeatmapMetric.entries.forEach { metric ->
            FilterChip(
                selected = selected == metric,
                onClick = { onSelect(metric) },
                label = {
                    Text(
                        when (metric) {
                            HeatmapMetric.LAT_G -> stringResource(R.string.heatmap_lat_g)
                            HeatmapMetric.LON_G -> stringResource(R.string.heatmap_lon_g)
                            HeatmapMetric.DRIFT_ANGLE -> stringResource(R.string.heatmap_drift)
                            HeatmapMetric.SLIP_RATIO -> stringResource(R.string.heatmap_slip)
                        },
                    )
                },
                modifier = Modifier.padding(end = SpacingMd),
            )
        }
    }
}

@Composable
fun HeatmapLegend(intensity: Float, metric: HeatmapMetric, modifier: Modifier = Modifier) {
    val color = RouteHeatmapLayer.intensityToColor(intensity, metric)
    Row(modifier = modifier.padding(SpacingMd)) {
        Text(stringResource(R.string.heatmap_legend))
        Text(
            text = " %.2f".format(intensity),
            modifier = Modifier
                .padding(start = SpacingMd)
                .background(color)
                .padding(SpacingMd),
        )
    }
}
