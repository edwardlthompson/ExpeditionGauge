package dev.foss.expeditiongauge.sessionmapcompare

import dev.foss.expeditiongauge.data.db.entities.SampleEntity

data class MapPoint(val lat: Double, val lon: Double)

/** Build two GPS polylines for side-by-side session compare. */
object SessionMapCompare {
    fun polyline(samples: List<SampleEntity>): List<MapPoint> =
        samples.mapNotNull { sample ->
            val lat = sample.latitude ?: return@mapNotNull null
            val lon = sample.longitude ?: return@mapNotNull null
            MapPoint(lat, lon)
        }

    fun pair(left: List<SampleEntity>, right: List<SampleEntity>): Pair<List<MapPoint>, List<MapPoint>> =
        polyline(left) to polyline(right)

    fun fromThumb(thumb: List<Pair<Float, Float>>): List<MapPoint> =
        thumb.map { MapPoint(it.first.toDouble(), it.second.toDouble()) }
}
