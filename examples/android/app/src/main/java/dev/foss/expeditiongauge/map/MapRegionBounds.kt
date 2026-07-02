package dev.foss.expeditiongauge.map

import dev.foss.expeditiongauge.data.db.entities.SampleEntity
import kotlin.math.max

data class MapRegionBounds(
    val minLat: Double,
    val maxLat: Double,
    val minLon: Double,
    val maxLon: Double,
) {
    val isValid: Boolean
        get() = minLat < maxLat && minLon < maxLon

    fun expand(marginFraction: Double = 0.08): MapRegionBounds {
        val latPad = (maxLat - minLat).coerceAtLeast(0.001) * marginFraction
        val lonPad = (maxLon - minLon).coerceAtLeast(0.001) * marginFraction
        return MapRegionBounds(
            minLat = minLat - latPad,
            maxLat = maxLat + latPad,
            minLon = minLon - lonPad,
            maxLon = maxLon + lonPad,
        )
    }

    fun contains(other: MapRegionBounds): Boolean =
        other.minLat >= minLat && other.maxLat <= maxLat &&
            other.minLon >= minLon && other.maxLon <= maxLon

    fun cacheKey(): String =
        "%.4f,%.4f,%.4f,%.4f".format(minLat, minLon, maxLat, maxLon)

    companion object {
        fun fromSamples(samples: List<SampleEntity>, marginFraction: Double = 0.08): MapRegionBounds? {
            val coords = samples.mapNotNull { sample ->
                val lat = sample.latitude ?: return@mapNotNull null
                val lon = sample.longitude ?: return@mapNotNull null
                lat to lon
            }
            if (coords.isEmpty()) return null
            val minLat = coords.minOf { it.first }
            val maxLat = coords.maxOf { it.first }
            val minLon = coords.minOf { it.second }
            val maxLon = coords.maxOf { it.second }
            return MapRegionBounds(minLat, maxLat, minLon, maxLon)
                .expand(marginFraction)
        }

        fun fromCenterRadiusKm(centerLat: Double, centerLon: Double, radiusKm: Float): MapRegionBounds {
            val latDelta = radiusKm / 111.0
            val lonDelta = radiusKm / max(111.0 * kotlin.math.cos(Math.toRadians(centerLat)), 1e-6)
            return MapRegionBounds(
                minLat = centerLat - latDelta,
                maxLat = centerLat + latDelta,
                minLon = centerLon - lonDelta,
                maxLon = centerLon + lonDelta,
            )
        }
    }
}
