package dev.foss.expeditiongauge.ui.playback

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import dev.foss.expeditiongauge.R
import dev.foss.expeditiongauge.data.db.entities.SampleEntity
import dev.foss.expeditiongauge.ui.theme.SpacingMd
import kotlin.math.abs

enum class HeatmapMetric {
    LAT_G,
    DRIFT_ANGLE,
    SLIP_RATIO,
}

data class HeatmapSegment(
    val startIndex: Int,
    val endIndex: Int,
    val intensity: Float,
    val color: Color,
)

object RouteHeatmapLayer {
    fun computeSegments(
        samples: List<SampleEntity>,
        metric: HeatmapMetric,
        bucketSize: Int = 5,
    ): List<HeatmapSegment> {
        if (samples.isEmpty()) return emptyList()
        val segments = mutableListOf<HeatmapSegment>()
        var i = 0
        while (i < samples.size) {
            val end = (i + bucketSize).coerceAtMost(samples.lastIndex)
            val slice = samples.subList(i, end + 1)
            val intensity = when (metric) {
                HeatmapMetric.LAT_G -> slice.maxOf { abs(it.latG) }
                HeatmapMetric.DRIFT_ANGLE -> slice.mapNotNull { it.driftAngleDeg?.let(::abs) }.maxOrNull() ?: 0f
                HeatmapMetric.SLIP_RATIO -> slice.mapNotNull { it.slipRatio }.maxOrNull() ?: 0f
            }
            segments += HeatmapSegment(i, end, intensity, intensityToColor(intensity, metric))
            i = end + 1
        }
        return segments
    }

    fun intensityToColor(intensity: Float, metric: HeatmapMetric): Color {
        val normalized = when (metric) {
            HeatmapMetric.LAT_G -> (intensity / 2f).coerceIn(0f, 1f)
            HeatmapMetric.DRIFT_ANGLE -> (intensity / 30f).coerceIn(0f, 1f)
            HeatmapMetric.SLIP_RATIO -> (intensity / 0.3f).coerceIn(0f, 1f)
        }
        return Color(
            red = normalized,
            green = 1f - normalized * 0.5f,
            blue = 0.2f,
            alpha = 0.85f,
        )
    }
}

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
fun HeatmapLegend(intensity: Float, modifier: Modifier = Modifier) {
    val color = RouteHeatmapLayer.intensityToColor(intensity, HeatmapMetric.LAT_G)
    Row(modifier = modifier.padding(SpacingMd)) {
        Text(stringResource(R.string.heatmap_legend))
        Text(
            text = " %.0f%%".format(intensity * 50),
            modifier = Modifier
                .padding(start = SpacingMd)
                .background(color)
                .padding(SpacingMd),
        )
    }
}
