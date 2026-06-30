package dev.foss.expeditiongauge.gauge

import kotlin.math.hypot
import kotlin.math.max

data class BallPosition(
    val normalizedX: Float,
    val normalizedY: Float,
    val zone: GaugeZone,
)

object AttitudeBallLogic {
    const val MAX_ANGLE_DEG = GaugeLogic.RING_30_DEG
    const val SAFE_THRESHOLD_DEG = GaugeLogic.RING_10_DEG
    const val CAUTION_THRESHOLD_DEG = GaugeLogic.RING_20_DEG

    fun mapPitchRoll(pitchDeg: Float, rollDeg: Float): BallPosition {
        val clampedPitch = pitchDeg.coerceIn(-MAX_ANGLE_DEG, MAX_ANGLE_DEG)
        val clampedRoll = rollDeg.coerceIn(-MAX_ANGLE_DEG, MAX_ANGLE_DEG)
        val normX = clampedRoll / MAX_ANGLE_DEG
        val normY = -clampedPitch / MAX_ANGLE_DEG

        val distance = hypot(normX.toDouble(), normY.toDouble()).toFloat()
        val (clampedX, clampedY) = if (distance > 1f) {
            Pair(normX / distance, normY / distance)
        } else {
            Pair(normX, normY)
        }

        val magnitude = max(kotlin.math.abs(pitchDeg), kotlin.math.abs(rollDeg))
        val zone = GaugeLogic.zoneForAngle(magnitude, SAFE_THRESHOLD_DEG, CAUTION_THRESHOLD_DEG)
        return BallPosition(clampedX, clampedY, zone)
    }

    fun ringRadiusFraction(ringDeg: Float): Float = ringDeg / MAX_ANGLE_DEG
}
