package dev.foss.expeditiongauge.gnssdeadreckon

import kotlin.math.cos
import kotlin.math.sin

data class GeoFix(val latitude: Double, val longitude: Double)

/** Integrate speed and heading when GNSS drops. */
object GnssDeadReckon {
    private const val METERS_PER_DEG_LAT = 111_320.0

    fun step(fix: GeoFix, speedMps: Float, headingDeg: Float, dtSec: Float): GeoFix {
        val dist = speedMps * dtSec
        val rad = Math.toRadians(headingDeg.toDouble())
        val dLat = dist * cos(rad) / METERS_PER_DEG_LAT
        val dLon = dist * sin(rad) / (METERS_PER_DEG_LAT * cos(Math.toRadians(fix.latitude)).coerceAtLeast(1e-6))
        return GeoFix(fix.latitude + dLat, fix.longitude + dLon)
    }
}
