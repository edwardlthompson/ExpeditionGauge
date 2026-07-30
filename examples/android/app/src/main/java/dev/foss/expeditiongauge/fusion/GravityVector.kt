package dev.foss.expeditiongauge.fusion

import kotlin.math.cos
import kotlin.math.sin

/**
 * Gravity unit vector and linear (specific-force) G helpers.
 *
 * Raw accelerometer includes gravity; vehicle latG/lonG must subtract it or a
 * parked upright phone reports ~1 G lateral.
 */
object GravityVector {
    const val GRAVITY_MS2 = 9.81f

    /**
     * Sensor-frame gravity unit vector matching [MadgwickFilter] conventions
     * (identity quaternion → +Z).
     */
    fun fromPitchRollDeg(pitchDeg: Float, rollDeg: Float): Triple<Float, Float, Float> {
        val p = Math.toRadians(pitchDeg.toDouble())
        val r = Math.toRadians(rollDeg.toDouble())
        val cosP = cos(p)
        return Triple(
            (-sin(p)).toFloat(),
            (sin(r) * cosP).toFloat(),
            (cos(r) * cosP).toFloat(),
        )
    }

    /** Linear lateral / longitudinal G from accel in m/s² and gravity unit vector. */
    fun linearLatLonG(
        axMs2: Float,
        ayMs2: Float,
        gravUnit: Triple<Float, Float, Float>,
    ): Pair<Float, Float> {
        val (gx, gy, _) = gravUnit
        val latG = (ayMs2 - gy * GRAVITY_MS2) / GRAVITY_MS2
        val lonG = (axMs2 - gx * GRAVITY_MS2) / GRAVITY_MS2
        return latG to lonG
    }

    /** Linear lat/lon G when accel is already in g-units (WitMotion). */
    fun linearLatLonFromG(
        axG: Float,
        ayG: Float,
        pitchDeg: Float,
        rollDeg: Float,
    ): Pair<Float, Float> {
        val (gx, gy, _) = fromPitchRollDeg(pitchDeg, rollDeg)
        return (ayG - gy) to (axG - gx)
    }
}
