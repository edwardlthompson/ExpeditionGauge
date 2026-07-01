package dev.foss.expeditiongauge.playback

import dev.foss.expeditiongauge.data.db.entities.SampleEntity
import kotlin.math.abs

data class ElevationProfileData(
    val smoothedAltitudesM: List<Double>,
    val minM: Double,
    val maxM: Double,
    val totalAscentM: Double,
    val totalDescentM: Double,
    val sampleCount: Int,
) {
    val hasProfile: Boolean get() = smoothedAltitudesM.size >= 2
}

object ElevationProfileBuilder {
    private const val SMOOTH_WINDOW = 5

    fun build(samples: List<SampleEntity>): ElevationProfileData? {
        if (samples.isEmpty()) return null
        val raw = samples.map { it.altitudeM }
        if (raw.count { it != null } < 2) return null

        val filled = fillMissing(raw)
        val smoothed = smooth(filled, SMOOTH_WINDOW)
        val stats = computeStats(filled)
        return ElevationProfileData(
            smoothedAltitudesM = smoothed,
            minM = stats.min,
            maxM = stats.max,
            totalAscentM = stats.ascent,
            totalDescentM = stats.descent,
            sampleCount = samples.size,
        )
    }

    internal fun fillMissing(raw: List<Double?>): List<Double> {
        val known = raw.mapIndexedNotNull { index, value -> value?.let { index to it } }
        if (known.isEmpty()) return emptyList()
        if (known.size == 1) return List(raw.size) { known.first().second }
        return raw.mapIndexed { index, value ->
            value ?: interpolate(index, known)
        }
    }

    internal fun smooth(values: List<Double>, window: Int): List<Double> {
        if (values.size <= 2 || window <= 1) return values
        val half = window / 2
        return values.mapIndexed { index, _ ->
            val start = (index - half).coerceAtLeast(0)
            val end = (index + half).coerceAtMost(values.lastIndex)
            values.subList(start, end + 1).average()
        }
    }

    private fun interpolate(index: Int, known: List<Pair<Int, Double>>): Double {
        val before = known.lastOrNull { it.first <= index }
        val after = known.firstOrNull { it.first >= index }
        return when {
            before != null && after != null && before.first != after.first -> {
                val fraction = (index - before.first).toDouble() / (after.first - before.first)
                before.second + fraction * (after.second - before.second)
            }
            before != null -> before.second
            after != null -> after.second
            else -> 0.0
        }
    }

    private data class AltitudeStats(
        val min: Double,
        val max: Double,
        val ascent: Double,
        val descent: Double,
    )

    private fun computeStats(values: List<Double>): AltitudeStats {
        if (values.isEmpty()) return AltitudeStats(0.0, 0.0, 0.0, 0.0)
        var ascent = 0.0
        var descent = 0.0
        for (i in 1 until values.size) {
            val delta = values[i] - values[i - 1]
            if (delta > 0) ascent += delta else descent += abs(delta)
        }
        return AltitudeStats(values.min(), values.max(), ascent, descent)
    }
}
