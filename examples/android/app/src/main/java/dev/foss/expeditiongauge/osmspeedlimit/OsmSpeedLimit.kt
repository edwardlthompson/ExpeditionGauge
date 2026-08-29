package dev.foss.expeditiongauge.osmspeedlimit

import dev.foss.expeditiongauge.timing.haversineM

data class SpeedLimitZone(
    val latitude: Double,
    val longitude: Double,
    val radiusKm: Double,
    val limitKph: Int,
)

/** Offline OSM maxspeed lookup from lat,lon,radiusKm,kph lines. */
object OsmSpeedLimit {
    fun parse(text: String): List<SpeedLimitZone> =
        text.lineSequence().mapNotNull { line ->
            val parts = line.trim().split(',')
            if (parts.size < 4) return@mapNotNull null
            runCatching {
                SpeedLimitZone(
                    latitude = parts[0].toDouble(),
                    longitude = parts[1].toDouble(),
                    radiusKm = parts[2].toDouble(),
                    limitKph = parts[3].toInt(),
                )
            }.getOrNull()
        }.toList()

    fun lookup(lat: Double, lon: Double, zones: List<SpeedLimitZone>): Int? =
        zones.filter { zone ->
            haversineM(lat, lon, zone.latitude, zone.longitude) <= zone.radiusKm * 1000.0
        }.minByOrNull { it.limitKph }?.limitKph

    fun overlayLabel(kph: Int?): String = kph?.let { "$it km/h" }.orEmpty()
}
