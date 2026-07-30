package dev.foss.expeditiongauge.alerts

import kotlin.math.abs

/**
 * Holds pitch/roll audible feedback until Madgwick attitude stops swinging.
 * Desk cold-starts often begin near ±70° and converge over several seconds —
 * time-only grace still fires mid-settle; this waits for a stable window.
 */
class AttitudeSettleGate(
    private val stableWindowMs: Long = STABLE_WINDOW_MS,
    private val maxDeltaDeg: Float = MAX_DELTA_DEG,
    private val minSamples: Int = MIN_SAMPLES,
) {
    private var settled = false
    private var windowStartMs = 0L
    private var anchorPitch = Float.NaN
    private var anchorRoll = Float.NaN
    private var samplesInWindow = 0
    private var lastPitch = Float.NaN

    fun isSettled(): Boolean = settled

    /** Feed each telemetry sample; returns whether attitude is settled after this update. */
    fun onSample(pitchDeg: Float, rollDeg: Float, nowMs: Long): Boolean {
        if (settled) {
            // Large jump (e.g. Madgwick reset on rotation) → re-settle.
            if (!lastPitch.isNaN() && abs(pitchDeg - lastPitch) >= RESETTLE_JUMP_DEG) {
                settled = false
                beginWindow(pitchDeg, rollDeg, nowMs)
            }
            lastPitch = pitchDeg
            return settled
        }
        if (anchorPitch.isNaN()) {
            beginWindow(pitchDeg, rollDeg, nowMs)
            lastPitch = pitchDeg
            return false
        }
        if (abs(pitchDeg - anchorPitch) > maxDeltaDeg || abs(rollDeg - anchorRoll) > maxDeltaDeg) {
            beginWindow(pitchDeg, rollDeg, nowMs)
            lastPitch = pitchDeg
            return false
        }
        samplesInWindow++
        lastPitch = pitchDeg
        if (nowMs - windowStartMs >= stableWindowMs && samplesInWindow >= minSamples) {
            settled = true
        }
        return settled
    }

    fun reset() {
        settled = false
        anchorPitch = Float.NaN
        anchorRoll = Float.NaN
        samplesInWindow = 0
        lastPitch = Float.NaN
        windowStartMs = 0L
    }

    private fun beginWindow(pitchDeg: Float, rollDeg: Float, nowMs: Long) {
        anchorPitch = pitchDeg
        anchorRoll = rollDeg
        windowStartMs = nowMs
        samplesInWindow = 1
    }

    companion object {
        const val STABLE_WINDOW_MS = 1_000L
        const val MAX_DELTA_DEG = 2.5f
        const val MIN_SAMPLES = 3
        const val RESETTLE_JUMP_DEG = 25f
    }
}
