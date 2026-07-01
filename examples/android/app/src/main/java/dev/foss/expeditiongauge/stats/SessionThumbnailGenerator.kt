package dev.foss.expeditiongauge.stats

import dev.foss.expeditiongauge.data.db.entities.SampleEntity
import kotlin.math.max

object SessionThumbnailGenerator {
    const val DEFAULT_MAX_POINTS = 48

    data class RouteThumb(val points: List<Pair<Float, Float>>)

    fun generate(samples: List<SampleEntity>, maxPoints: Int = DEFAULT_MAX_POINTS): RouteThumb {
        val coords = samples.mapNotNull { sample ->
            val lat = sample.latitude ?: return@mapNotNull null
            val lon = sample.longitude ?: return@mapNotNull null
            lat to lon
        }
        if (coords.size < 2) return RouteThumb(emptyList())

        val decimated = decimate(coords, maxPoints)
        val minLat = decimated.minOf { it.first }
        val maxLat = decimated.maxOf { it.first }
        val minLon = decimated.minOf { it.second }
        val maxLon = decimated.maxOf { it.second }
        val latSpan = max(maxLat - minLat, 1e-9)
        val lonSpan = max(maxLon - minLon, 1e-9)

        val points = decimated.map { (lat, lon) ->
            val x = ((lon - minLon) / lonSpan).toFloat().coerceIn(0f, 1f)
            val y = (1f - ((lat - minLat) / latSpan).toFloat()).coerceIn(0f, 1f)
            x to y
        }
        return RouteThumb(points)
    }

    private fun decimate(coords: List<Pair<Double, Double>>, maxPoints: Int): List<Pair<Double, Double>> {
        if (coords.size <= maxPoints) return coords
        val step = (coords.size - 1).toDouble() / (maxPoints - 1)
        return (0 until maxPoints).map { index ->
            coords[(index * step).toInt().coerceIn(0, coords.lastIndex)]
        }
    }
}
