package dev.foss.expeditiongauge.gauge

import kotlin.math.cos
import kotlin.math.sin

/**
 * Elite-style wireframe compass sphere projection (vehicle-stable view).
 * Display math only — callers pass already-calibrated pitch/roll/yaw.
 */
object CompassBallLogic {
    data class Vec3(val x: Float, val y: Float, val z: Float)

    data class ProjectedPoint(
        val x: Float,
        val y: Float,
        /** Camera-forward depth; higher = closer to viewer. */
        val depth: Float,
    )

    /** Prefer body yaw; else finite heading; else null (untrusted). */
    fun resolveYawDeg(bodyYawDeg: Float?, headingDeg: Float): Float? {
        if (bodyYawDeg != null && bodyYawDeg.isFinite()) return bodyYawDeg
        if (headingDeg.isFinite()) return headingDeg
        return null
    }

    /**
     * Rotate a unit-sphere point from world/NED into the vehicle view, then project.
     * Order: yaw (heading), then pitch, then roll — sphere moves under a fixed reticle.
     */
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

        // yaw about +Y (up)
        var x = point.x * cy + point.z * sy
        var z = -point.x * sy + point.z * cy
        var yv = point.y

        // pitch about +X
        val y2 = yv * cp - z * sp
        val z2 = yv * sp + z * cp
        yv = y2
        z = z2

        // roll about +Z
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

    /** Cardinals on the equator: N=0°, E=90°, S=180°, W=270° lon (deg). */
    fun cardinalLonDeg(label: Char): Float = when (label) {
        'N' -> 0f
        'E' -> 90f
        'S' -> 180f
        'W' -> 270f
        else -> 0f
    }

    /** Shortest-path lerp for circular degrees (display smoothing). */
    fun lerpYawDeg(from: Float, to: Float, t: Float): Float {
        var delta = (to - from) % 360f
        if (delta > 180f) delta -= 360f
        if (delta < -180f) delta += 360f
        return from + delta * t.coerceIn(0f, 1f)
    }
}
