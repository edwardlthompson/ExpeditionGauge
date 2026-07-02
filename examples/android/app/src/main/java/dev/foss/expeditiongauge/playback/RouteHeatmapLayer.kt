package dev.foss.expeditiongauge.playback

import androidx.compose.ui.graphics.Color
import dev.foss.expeditiongauge.data.db.entities.SampleEntity
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
    fun sampleIntensity(sample: SampleEntity, metric: HeatmapMetric): Float = when (metric) {
        HeatmapMetric.LAT_G -> abs(sample.latG)
        HeatmapMetric.DRIFT_ANGLE -> abs(sample.driftAngleDeg ?: 0f)
        HeatmapMetric.SLIP_RATIO -> sample.slipRatio ?: 0f
    }

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
            val intensity = slice.maxOf { sampleIntensity(it, metric) }
            segments += HeatmapSegment(i, end, intensity, intensityToColor(intensity, metric))
            i = end + 1
        }
        return segments
    }

    fun intensityToColor(intensity: Float, metric: HeatmapMetric): Color {
        return when (metric) {
            HeatmapMetric.DRIFT_ANGLE -> {
                val signed = intensity.coerceIn(0f, 30f)
                if (signed <= 5f) DrivingRouteStyling.Coast
                else DrivingRouteStyling.Accel.copy(alpha = (signed / 30f).coerceIn(0.4f, 1f))
            }
            HeatmapMetric.SLIP_RATIO -> DriftRouteStyling.SlipHighlight.copy(
                alpha = (intensity / 0.3f).coerceIn(0.2f, 1f),
            )
            HeatmapMetric.LAT_G -> {
                val normalized = (intensity / 2f).coerceIn(0f, 1f)
                Color(red = normalized, green = 1f - normalized * 0.5f, blue = 0.2f, alpha = 0.85f)
            }
        }
    }

    fun heatmapColorBucket(intensity: Float, metric: HeatmapMetric): Int = when (metric) {
        HeatmapMetric.LAT_G -> when {
            intensity >= 1.5f -> 3
            intensity >= 1.0f -> 2
            intensity >= 0.5f -> 1
            else -> 0
        }
        HeatmapMetric.DRIFT_ANGLE -> when {
            intensity >= 20f -> 3
            intensity >= 10f -> 2
            intensity >= 5f -> 1
            else -> 0
        }
        HeatmapMetric.SLIP_RATIO -> when {
            intensity >= 0.25f -> 3
            intensity >= 0.15f -> 2
            intensity >= 0.08f -> 1
            else -> 0
        }
    }
}
