package dev.foss.expeditiongauge.car.gauge

import kotlin.math.hypot
import kotlin.math.max

/** Normalized ball position for AA G-meter bitmap (vehicle frame). */
data class AaBallPosition(
    val normalizedX: Float,
    val normalizedY: Float,
)

/**
 * Pitch/roll → ball, matching phone AttitudeBallLogic (no screen-rotation remap on AA).
 */
object AaBallLogic {
    const val MAX_ANGLE_DEG = 30f

    fun mapPitchRoll(pitchDeg: Float, rollDeg: Float): AaBallPosition {
        val clampedPitch = pitchDeg.coerceIn(-MAX_ANGLE_DEG, MAX_ANGLE_DEG)
        val clampedRoll = rollDeg.coerceIn(-MAX_ANGLE_DEG, MAX_ANGLE_DEG)
        val normX = clampedRoll / MAX_ANGLE_DEG
        val normY = clampedPitch / MAX_ANGLE_DEG
        val distance = hypot(normX.toDouble(), normY.toDouble()).toFloat()
        return if (distance > 1f) {
            AaBallPosition(normX / distance, normY / distance)
        } else {
            AaBallPosition(normX, normY)
        }
    }

    fun ringRadiusFraction(ringDeg: Float): Float = ringDeg / MAX_ANGLE_DEG

    fun magnitudeDeg(pitchDeg: Float, rollDeg: Float): Float =
        max(kotlin.math.abs(pitchDeg), kotlin.math.abs(rollDeg))
}
