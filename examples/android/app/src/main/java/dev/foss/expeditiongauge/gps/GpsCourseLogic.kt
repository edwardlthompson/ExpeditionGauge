package dev.foss.expeditiongauge.gps

import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * Course-over-ground from successive GPS fixes (great-circle initial bearing).
 * Chip COG is preferred; lat/lon deltas are a fallback. See [GpsCourseResolver].
 */
object GpsCourseLogic {
    /** Same floor as sideslip β — crawl speed course is unreliable. */
    const val MIN_SPEED_MPS = 2.0f
    const val MIN_SEGMENT_M = 2.0f

    fun bearingDeg(
        fromLat: Double,
        fromLon: Double,
        toLat: Double,
        toLon: Double,
    ): Float {
        val lat1 = Math.toRadians(fromLat)
        val lat2 = Math.toRadians(toLat)
        val dLon = Math.toRadians(toLon - fromLon)
        val y = sin(dLon) * cos(lat2)
        val x = cos(lat1) * sin(lat2) - sin(lat1) * cos(lat2) * cos(dLon)
        var deg = Math.toDegrees(atan2(y, x)).toFloat()
        if (deg < 0f) deg += 360f
        return deg
    }

    /** Haversine distance in metres. */
    fun distanceM(
        fromLat: Double,
        fromLon: Double,
        toLat: Double,
        toLon: Double,
    ): Float {
        val r = 6_371_000.0
        val dLat = Math.toRadians(toLat - fromLat)
        val dLon = Math.toRadians(toLon - fromLon)
        val a = sin(dLat / 2) * sin(dLat / 2) +
            cos(Math.toRadians(fromLat)) * cos(Math.toRadians(toLat)) *
            sin(dLon / 2) * sin(dLon / 2)
        return (2 * r * atan2(sqrt(a), sqrt(1 - a))).toFloat()
    }

    fun isReliableCourse(speedMps: Float, segmentM: Float): Boolean =
        speedMps >= MIN_SPEED_MPS && segmentM >= MIN_SEGMENT_M

    /**
     * HUD HDG uses a real GPS course (including a held last-good COG) whenever
     * one exists. Body / IMU yaw is only the fallback when GPS has never
     * produced a course — not when speed drops below [MIN_SPEED_MPS].
     */
    fun displayHeadingDeg(
        bodyYawDeg: Float,
        gpsCourseDeg: Float?,
        speedMps: Float = 0f,
    ): Float {
        val course = gpsCourseDeg?.takeIf { it.isFinite() }
        return if (course != null && speedMps >= 0f) {
            normalize360(course)
        } else {
            normalize360(bodyYawDeg)
        }
    }

    fun normalize360(deg: Float): Float {
        var a = deg % 360f
        if (a < 0f) a += 360f
        return a
    }
}
