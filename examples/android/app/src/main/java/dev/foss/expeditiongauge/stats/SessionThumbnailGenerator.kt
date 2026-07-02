package dev.foss.expeditiongauge.stats

import dev.foss.expeditiongauge.data.db.entities.SampleEntity
import dev.foss.expeditiongauge.playback.DrivingRouteStyling
import kotlin.math.max

object SessionThumbnailGenerator {
    const val DEFAULT_MAX_POINTS = 48

    data class ColoredSegment(
        val from: Pair<Float, Float>,
        val to: Pair<Float, Float>,
        val bucket: Int,
    )

    data class RouteThumb(
        val points: List<Pair<Float, Float>>,
        val segments: List<ColoredSegment> = emptyList(),
    )

    fun generate(samples: List<SampleEntity>, maxPoints: Int = DEFAULT_MAX_POINTS): RouteThumb {
        val indexed = samples.mapNotNull { sample ->
            val lat = sample.latitude ?: return@mapNotNull null
            val lon = sample.longitude ?: return@mapNotNull null
            Triple(lat, lon, DrivingRouteStyling.colorBucket(sample.lonAccel))
        }
        if (indexed.size < 2) return RouteThumb(emptyList())

        val decimated = decimate(indexed, maxPoints)
        val minLat = decimated.minOf { it.first }
        val maxLat = decimated.maxOf { it.first }
        val minLon = decimated.minOf { it.second }
        val maxLon = decimated.maxOf { it.second }
        val latSpan = max(maxLat - minLat, 1e-9)
        val lonSpan = max(maxLon - minLon, 1e-9)

        fun normalize(lat: Double, lon: Double): Pair<Float, Float> {
            val x = ((lon - minLon) / lonSpan).toFloat().coerceIn(0f, 1f)
            val y = (1f - ((lat - minLat) / latSpan).toFloat()).coerceIn(0f, 1f)
            return x to y
        }

        val points = decimated.map { (lat, lon, _) -> normalize(lat, lon) }
        val segments = decimated.zipWithNext { a, b ->
            ColoredSegment(
                from = normalize(a.first, a.second),
                to = normalize(b.first, b.second),
                bucket = b.third,
            )
        }
        return RouteThumb(points = points, segments = segments)
    }

    private fun decimate(
        coords: List<Triple<Double, Double, Int>>,
        maxPoints: Int,
    ): List<Triple<Double, Double, Int>> {
        if (coords.size <= maxPoints) return coords
        val step = (coords.size - 1).toDouble() / (maxPoints - 1)
        return (0 until maxPoints).map { index ->
            coords[(index * step).toInt().coerceIn(0, coords.lastIndex)]
        }
    }
}
