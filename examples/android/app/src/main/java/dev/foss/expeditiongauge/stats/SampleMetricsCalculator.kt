package dev.foss.expeditiongauge.stats

import dev.foss.expeditiongauge.data.db.entities.SampleEntity
import kotlin.math.abs

data class SampleMetrics(
    val peakLatG: Float?,
    val maxBetaDeg: Float?,
    val slipEventCount: Int,
    val sparklineLatG: List<Float>,
)

internal object SampleMetricsCalculator {
    fun computeFromSamples(samples: List<SampleEntity>, slipThreshold: Float): SampleMetrics {
        if (samples.isEmpty()) {
            return SampleMetrics(
                peakLatG = null,
                maxBetaDeg = null,
                slipEventCount = 0,
                sparklineLatG = emptyList(),
            )
        }
        var peakLatG = 0f
        var maxBeta: Float? = null
        var slipEvents = 0
        samples.forEach { sample ->
            peakLatG = maxOf(peakLatG, abs(sample.latG))
            sample.driftAngleDeg?.let { beta ->
                val absBeta = abs(beta)
                maxBeta = maxOf(maxBeta ?: 0f, absBeta)
            }
            sample.slipRatio?.let { slip ->
                if (slip >= slipThreshold) slipEvents += 1
            }
        }
        val step = (samples.size / 40).coerceAtLeast(1)
        val sparkline = samples.filterIndexed { index, _ -> index % step == 0 }
            .take(40)
            .map { abs(it.latG) }
        return SampleMetrics(
            peakLatG = peakLatG,
            maxBetaDeg = maxBeta,
            slipEventCount = slipEvents,
            sparklineLatG = sparkline,
        )
    }
}
