package dev.foss.expeditiongauge.car.gauge

import kotlin.math.cos
import kotlin.math.sin

/**
 * Wireframe compass sphere projection (vehicle-stable view).
 * Port of phone [dev.foss.expeditiongauge.gauge.CompassBallLogic] for `:car` bitmaps.
 */
object CompassBallLogic {
    data class Vec3(val x: Float, val y: Float, val z: Float)

    data class ProjectedPoint(
        val x: Float,
        val y: Float,
        val depth: Float,
    )

    fun resolveYawDeg(bodyYawDeg: Float?, headingDeg: Float): Float? {
        if (bodyYawDeg != null && bodyYawDeg.isFinite()) return bodyYawDeg
        if (headingDeg.isFinite()) return headingDeg
        return null
    }

    fun project(
        point: Vec3,
        pitchDeg: Float,
        rollDeg: Float,
        yawDeg: Float,
        perspective: Float = 2.6f,
    ): ProjectedPoint {
        val y = Math.toRadians(yawDeg.toDouble())
        val p = Math.toRadians(pitchDeg.toDouble())
        val r = Math.toRadians(rollDeg.toDouble())
        val cy = cos(y).toFloat()
        val sy = sin(y).toFloat()
        val cp = cos(p).toFloat()
        val sp = sin(p).toFloat()
        val cr = cos(r).toFloat()
        val sr = sin(r).toFloat()

        var x = point.x * cy + point.z * sy
        var z = -point.x * sy + point.z * cy
        var yv = point.y

        val y2 = yv * cp - z * sp
        val z2 = yv * sp + z * cp
        yv = y2
        z = z2

        val x3 = x * cr - yv * sr
        val y3 = x * sr + yv * cr
        x = x3
        yv = y3

        val w = perspective / (perspective + z)
        return ProjectedPoint(x = x * w, y = yv * w, depth = z)
    }

    fun spherePoint(latDeg: Float, lonDeg: Float): Vec3 {
        val lat = Math.toRadians(latDeg.toDouble())
        val lon = Math.toRadians(lonDeg.toDouble())
        val cl = cos(lat)
        return Vec3(
            x = (cl * sin(lon)).toFloat(),
            y = sin(lat).toFloat(),
            z = (cl * cos(lon)).toFloat(),
        )
    }

    fun cardinalLonDeg(label: Char): Float = when (label) {
        'N' -> 0f
        'E' -> 90f
        'S' -> 180f
        'W' -> 270f
        else -> 0f
    }
}
