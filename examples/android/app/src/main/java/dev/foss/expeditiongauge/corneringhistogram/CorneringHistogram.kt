package dev.foss.expeditiongauge.corneringhistogram

import dev.foss.expeditiongauge.data.db.entities.SampleEntity
import kotlin.math.abs

data class HistogramBin(
    val startG: Float,
    val endG: Float,
    val count: Int,
)

/** Bin absolute latG samples into 0.25 G buckets. */
object CorneringHistogram {
    const val BIN_WIDTH = 0.25f
    const val MAX_G = 2.0f

    fun bins(samples: List<SampleEntity>, width: Float = BIN_WIDTH, maxG: Float = MAX_G): List<HistogramBin> {
        val n = ((maxG / width).toInt()).coerceAtLeast(1)
        val counts = IntArray(n)
        samples.forEach { sample ->
            val g = abs(sample.latG).coerceIn(0f, maxG - 0.001f)
            val idx = (g / width).toInt().coerceIn(0, n - 1)
            counts[idx]++
        }
        return counts.mapIndexed { i, count ->
            HistogramBin(startG = i * width, endG = (i + 1) * width, count = count)
        }
    }
}
