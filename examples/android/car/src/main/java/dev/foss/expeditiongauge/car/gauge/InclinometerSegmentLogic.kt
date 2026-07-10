package dev.foss.expeditiongauge.car.gauge

import kotlin.math.abs
import kotlin.math.ceil

/** One lit pitch bar for rendering. */
data class InclinometerLitSegment(
    val angleDeg: Float,
    val colorArgb: Int,
)

/**
 * Pitch bars light from center; roll uses communicating-vessel fills
 * (one side fills as the other drains).
 */
data class InclinometerFrame(
    val pitchUp: List<InclinometerLitSegment>,
    val pitchDown: List<InclinometerLitSegment>,
    val leftRollFill: Float,
    val rightRollFill: Float,
    val pitchDeg: Float,
    val rollDeg: Float,
    val pitchMarkerDeg: Float?,
    val rollMarkerDeg: Float?,
)

object InclinometerSegmentLogic {
    const val BARS_PER_SIDE = 5
    const val ROLL_SEGMENTS_PER_SIDE = 10

    private val pitchStepDeg = InclinometerColor.MAX_DEG / BARS_PER_SIDE

    fun frame(
        pitchDeg: Float,
        rollDeg: Float,
        maxPitchThresholdDeg: Float? = null,
        maxRollThresholdDeg: Float? = null,
    ): InclinometerFrame {
        // Callers pass vehicle-frame pitch/roll (fusion after calibration).
        // Do not apply G-meter displayRotation remaps — pitch drives the ladder, roll the sides.
        val clampedPitch = pitchDeg.coerceIn(-InclinometerColor.MAX_DEG, InclinometerColor.MAX_DEG)
        val clampedRoll = rollDeg.coerceIn(-InclinometerColor.MAX_DEG, InclinometerColor.MAX_DEG)
        val (left, right) = rollFills(clampedRoll)
        return InclinometerFrame(
            pitchUp = pitchBars(clampedPitch, positive = true),
            pitchDown = pitchBars(clampedPitch, positive = false),
            leftRollFill = left,
            rightRollFill = right,
            pitchDeg = clampedPitch,
            rollDeg = clampedRoll,
            pitchMarkerDeg = maxPitchThresholdDeg?.coerceIn(0f, InclinometerColor.MAX_DEG),
            rollMarkerDeg = maxRollThresholdDeg?.coerceIn(0f, InclinometerColor.MAX_DEG),
        )
    }

    /** At 0° both sides half-full; negative roll fills left / drains right. */
    fun rollFills(rollDeg: Float): Pair<Float, Float> {
        val n = (rollDeg / InclinometerColor.MAX_DEG).coerceIn(-1f, 1f)
        return ((1f - n) / 2f) to ((1f + n) / 2f)
    }

    private fun pitchBars(pitchDeg: Float, positive: Boolean): List<InclinometerLitSegment> {
        val wantPositive = pitchDeg > 0f
        if (positive != wantPositive || pitchDeg == 0f) return emptyList()
        val count = ceil(abs(pitchDeg) / pitchStepDeg).toInt().coerceIn(1, BARS_PER_SIDE)
        return (1..count).map { bar ->
            val angle = bar * pitchStepDeg
            InclinometerLitSegment(angle, InclinometerColor.argbForAngleMagnitude(angle))
        }
    }
}
