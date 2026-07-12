package dev.foss.expeditiongauge.fusion

import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

/** Tilt-compensated magnetic yaw (degrees, signed −180…180). Null if mag too weak. */
object MagHeading {
    fun yawDeg(
        accelX: Float,
        accelY: Float,
        accelZ: Float,
        magX: Float,
        magY: Float,
        magZ: Float,
    ): Float? {
        val magNorm = sqrt(magX * magX + magY * magY + magZ * magZ)
        if (magNorm < 5f) return null
        val aNorm = sqrt(accelX * accelX + accelY * accelY + accelZ * accelZ)
        if (aNorm < 1e-3f) return null
        val ax = accelX / aNorm
        val ay = accelY / aNorm
        val az = accelZ / aNorm
        // Pitch/roll from gravity (radians), then tilt-compensate mag.
        val pitch = kotlin.math.asin((-ax).coerceIn(-1f, 1f))
        val roll = atan2(ay, az)
        val mx2 = magX * cos(pitch) + magZ * sin(pitch)
        val my2 = magX * sin(roll) * sin(pitch) + magY * cos(roll) - magZ * sin(roll) * cos(pitch)
        val yawRad = atan2(-my2, mx2)
        return Math.toDegrees(yawRad.toDouble()).toFloat()
    }
}
