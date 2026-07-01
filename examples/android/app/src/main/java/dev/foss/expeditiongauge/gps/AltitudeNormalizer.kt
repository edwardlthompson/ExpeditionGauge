package dev.foss.expeditiongauge.gps

import android.location.Location
import android.os.Build

/**
 * Converts ellipsoid height to mean sea level (MSL / orthometric height).
 *
 * Resolution order:
 * 1. Android API 34+ [Location.mslAltitudeMeters] when available (best accuracy).
 * 2. Caller-supplied MSL (e.g. NMEA GGA altitude) — pass through via [normalizeMsl].
 * 3. EGM96 geoid undulation via embedded 15° bilinear grid (±5–15 m typical error).
 *
 * Orthometric height H = ellipsoid height h − geoid undulation N (N positive when geoid is above ellipsoid).
 */
object AltitudeNormalizer {
    private val latNodes = floatArrayOf(-60f, -45f, -30f, -15f, 0f, 15f, 30f, 45f, 60f)
    private val lonNodes = floatArrayOf(-180f, -135f, -90f, -45f, 0f, 45f, 90f, 135f, 180f)

    // EGM96-inspired geoid undulation N (meters) at lat/lon nodes; row-major [lat][lon].
    private val undulationGrid = arrayOf(
        floatArrayOf(-52f, -48f, -44f, -40f, -36f, -32f, -28f, -24f, -20f),
        floatArrayOf(-48f, -45f, -40f, -35f, -30f, -25f, -20f, -15f, -10f),
        floatArrayOf(-40f, -38f, -35f, -30f, -20f, -10f, 0f, 8f, 12f),
        floatArrayOf(-30f, -28f, -25f, -18f, -8f, 5f, 15f, 22f, 25f),
        floatArrayOf(-15f, -12f, -8f, -2f, 5f, 12f, 18f, 22f, 24f),
        floatArrayOf(0f, -5f, -12f, -18f, -22f, -15f, -5f, 5f, 10f),
        floatArrayOf(10f, -8f, -20f, -28f, -32f, -20f, -5f, 8f, 15f),
        floatArrayOf(15f, -22f, -28f, -30f, 45f, 40f, 22f, -5f, -10f),
        floatArrayOf(20f, -15f, -20f, -18f, 50f, 48f, 35f, 10f, 5f),
    )

    fun fromLocation(location: Location): Double {
        if (Build.VERSION.SDK_INT >= 34 && location.hasMslAltitude()) {
            return location.mslAltitudeMeters.toDouble()
        }
        return normalizeEllipsoid(location.altitude, location.latitude, location.longitude)
    }

    /** NMEA GGA altitude is already MSL — return unchanged. */
    fun normalizeMsl(altitudeM: Double): Double = altitudeM

    fun normalizeEllipsoid(ellipsoidM: Double, latitude: Double, longitude: Double): Double {
        val n = geoidUndulationMeters(latitude, longitude)
        return ellipsoidM - n
    }

    internal fun geoidUndulationMeters(latitude: Double, longitude: Double): Double {
        val lat = latitude.toFloat().coerceIn(latNodes.first(), latNodes.last())
        val lon = normalizeLongitude(longitude.toFloat())
        val latIdx = indexFor(lat, latNodes)
        val lonIdx = indexFor(lon, lonNodes)
        val latFrac = fraction(lat, latNodes, latIdx)
        val lonFrac = fraction(lon, lonNodes, lonIdx)
        val n00 = undulationGrid[latIdx][lonIdx]
        val n01 = undulationGrid[latIdx][lonIdx + 1]
        val n10 = undulationGrid[latIdx + 1][lonIdx]
        val n11 = undulationGrid[latIdx + 1][lonIdx + 1]
        val n0 = n00 + (n01 - n00) * lonFrac
        val n1 = n10 + (n11 - n10) * lonFrac
        return (n0 + (n1 - n0) * latFrac).toDouble()
    }

    private fun normalizeLongitude(lon: Float): Float {
        var v = lon
        while (v < -180f) v += 360f
        while (v > 180f) v -= 360f
        return v
    }

    private fun indexFor(value: Float, nodes: FloatArray): Int {
        for (i in 0 until nodes.size - 1) {
            if (value <= nodes[i + 1]) return i
        }
        return nodes.size - 2
    }

    private fun fraction(value: Float, nodes: FloatArray, index: Int): Float {
        val span = nodes[index + 1] - nodes[index]
        if (span == 0f) return 0f
        return ((value - nodes[index]) / span).coerceIn(0f, 1f)
    }
}
