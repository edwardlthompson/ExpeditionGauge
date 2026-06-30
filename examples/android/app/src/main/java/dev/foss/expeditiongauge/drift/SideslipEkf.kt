package dev.foss.expeditiongauge.drift

import kotlin.math.abs

fun normalizeAngleDeg(angleDeg: Float): Float {
    var a = angleDeg % 360f
    if (a > 180f) a -= 360f
    if (a < -180f) a += 360f
    return a
}

class SideslipEkf(
    private val minSpeedMps: Float = 2.0f,
) {
    var yawDeg: Float = 0f
        private set
    var yawRateDegPerSec: Float = 0f
        private set
    var betaDeg: Float = 0f
        private set
    var velocityHeadingDeg: Float = 0f
        private set

    fun predict(dtSec: Float, gyroYawRateDeg: Float) {
        yawRateDegPerSec = gyroYawRateDeg
        yawDeg = normalizeAngleDeg(yawDeg + yawRateDegPerSec * dtSec)
    }

    fun updateBodyYaw(bodyYawDeg: Float) {
        yawDeg = bodyYawDeg
    }

    fun updateVelocityHeading(headingDeg: Float, speedMps: Float) {
        velocityHeadingDeg = headingDeg
        if (speedMps < minSpeedMps) return
        betaDeg = normalizeAngleDeg(yawDeg - velocityHeadingDeg)
    }

    fun currentBeta(): Float = betaDeg
}
