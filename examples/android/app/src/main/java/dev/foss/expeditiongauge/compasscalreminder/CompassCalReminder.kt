package dev.foss.expeditiongauge.compasscalreminder

import kotlin.math.abs
import kotlin.math.sqrt

/** Remind to recalibrate compass after a magnetometer spike. */
object CompassCalReminder {
    const val SPIKE_UT = 40f

    fun magnitude(x: Float, y: Float, z: Float): Float = sqrt(x * x + y * y + z * z)

    fun shouldRemind(previousUt: Float, nextUt: Float, threshold: Float = SPIKE_UT): Boolean =
        abs(nextUt - previousUt) >= threshold
}
