package dev.foss.expeditiongauge.offlinegeocoder

import dev.foss.expeditiongauge.timing.haversineM

data class GeoPlace(val name: String, val latitude: Double, val longitude: Double)

/** Nearest named place from a local gazetteer (no network). */
object OfflineGeocoder {
    val PLACES = listOf(
        GeoPlace("Pacific Raceways", 47.321, -122.145),
        GeoPlace("The Ridge", 47.255, -122.105),
        GeoPlace("Oregon Raceway", 44.707, -120.978),
        GeoPlace("Home track", 0.0, 0.0),
    )

    fun nearest(lat: Double, lon: Double, maxKm: Double = 20.0, places: List<GeoPlace> = PLACES): GeoPlace? =
        places.map { it to haversineM(lat, lon, it.latitude, it.longitude) }
            .filter { it.second <= maxKm * 1000.0 }
            .minByOrNull { it.second }
            ?.first

    fun titleFor(lat: Double?, lon: Double?, fallback: String = "Session"): String {
        if (lat == null || lon == null) return fallback
        return nearest(lat, lon)?.name ?: fallback
    }
}
