package dev.foss.expeditiongauge.fusion

import kotlin.math.atan2
import kotlin.math.sqrt

class ComplementaryFilter(
    private val alpha: Float = 0.98f,
) {
    private var pitchDeg = 0f
    private var rollDeg = 0f
    private var yawDeg = 0f

    fun reset() {
        pitchDeg = 0f
        rollDeg = 0f
        yawDeg = 0f
    }

    fun update(
        gyroX: Float,
        gyroY: Float,
        gyroZ: Float,
        accelX: Float,
        accelY: Float,
        accelZ: Float,
        dtSec: Float,
    ) {
        val accelPitch = Math.toDegrees(
            atan2(-accelX.toDouble(), sqrt((accelY * accelY + accelZ * accelZ).toDouble())),
        ).toFloat()
        val accelRoll = Math.toDegrees(
            atan2(accelY.toDouble(), accelZ.toDouble()),
        ).toFloat()

        pitchDeg = alpha * (pitchDeg + Math.toDegrees(gyroX.toDouble()).toFloat() * dtSec) +
            (1f - alpha) * accelPitch
        rollDeg = alpha * (rollDeg + Math.toDegrees(gyroY.toDouble()).toFloat() * dtSec) +
            (1f - alpha) * accelRoll
        yawDeg += Math.toDegrees(gyroZ.toDouble()).toFloat() * dtSec
    }

    fun pitchDeg(): Float = pitchDeg
    fun rollDeg(): Float = rollDeg
    fun yawDeg(): Float = yawDeg

    fun gravityUnit(): Triple<Float, Float, Float> =
        GravityVector.fromPitchRollDeg(pitchDeg, rollDeg)
}
