package dev.foss.expeditiongauge.car.gauge

import kotlin.math.ceil
import kotlin.math.abs

/** One lit pitch bar or roll segment for rendering. */
data class InclinometerLitSegment(
    val angleDeg: Float,
    val colorArgb: Int,
)

data class InclinometerFrame(
    val pitchUp: List<InclinometerLitSegment>,
    val pitchDown: List<InclinometerLitSegment>,
    val rollLeft: List<InclinometerLitSegment>,
    val rollRight: List<InclinometerLitSegment>,
    val pitchMarkerDeg: Float?,
    val rollMarkerDeg: Float?,
)

object InclinometerSegmentLogic {
    const val BARS_PER_SIDE = 5
    const val ROLL_SEGMENTS_PER_SIDE = 7

    private val pitchStepDeg = InclinometerColor.MAX_DEG / BARS_PER_SIDE
    private val rollStepDeg = InclinometerColor.MAX_DEG / ROLL_SEGMENTS_PER_SIDE

    fun frame(
        pitchDeg: Float,
        rollDeg: Float,
        maxPitchThresholdDeg: Float? = null,
        maxRollThresholdDeg: Float? = null,
    ): InclinometerFrame {
        val clampedPitch = pitchDeg.coerceIn(-InclinometerColor.MAX_DEG, InclinometerColor.MAX_DEG)
        val clampedRoll = rollDeg.coerceIn(-InclinometerColor.MAX_DEG, InclinometerColor.MAX_DEG)

        return InclinometerFrame(
            pitchUp = pitchBars(clampedPitch, positive = true),
            pitchDown = pitchBars(clampedPitch, positive = false),
            rollLeft = rollSegments(clampedRoll, left = true),
            rollRight = rollSegments(clampedRoll, left = false),
            pitchMarkerDeg = maxPitchThresholdDeg?.coerceIn(0f, InclinometerColor.MAX_DEG),
            rollMarkerDeg = maxRollThresholdDeg?.coerceIn(0f, InclinometerColor.MAX_DEG),
        )
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

    private fun rollSegments(rollDeg: Float, left: Boolean): List<InclinometerLitSegment> {
        val wantLeft = rollDeg < 0f
        if (left != wantLeft || rollDeg == 0f) return emptyList()
        val count = ceil(abs(rollDeg) / rollStepDeg).toInt().coerceIn(1, ROLL_SEGMENTS_PER_SIDE)
        return (1..count).map { seg ->
            val angle = seg * rollStepDeg
            InclinometerLitSegment(angle, InclinometerColor.argbForAngleMagnitude(angle))
        }
    }
}
