package dev.foss.expeditiongauge.car

import kotlin.math.abs

/**
 * Rate-limits car HUD refresh. Surface painting can take ~30 Hz; Pane
 * [Screen.invalidate] stays host-friendly because those screens throttle separately.
 */
internal class AaScreenInvalidation {
    private var lastInvalidateMs: Long = 0L
    private var lastForceInvalidateMs: Long = 0L
    private var lastSamplePitch: Float = 0f
    private var lastSampleRoll: Float = 0f

    fun maybeInvalidate(
        pitchDeg: Float,
        rollDeg: Float,
        force: Boolean,
        listener: () -> Unit,
    ) {
        val now = System.currentTimeMillis()
        val attitudeChanged = abs(pitchDeg - lastSamplePitch) > ATTITUDE_EPS_DEG ||
            abs(rollDeg - lastSampleRoll) > ATTITUDE_EPS_DEG
        if (!force && !attitudeChanged && now - lastForceInvalidateMs < PERIODIC_REFRESH_MS) return
        if (!force && now - lastInvalidateMs < AA_INVALIDATE_MIN_INTERVAL_MS) return
        lastInvalidateMs = now
        if (attitudeChanged || force) {
            lastSamplePitch = pitchDeg
            lastSampleRoll = rollDeg
        }
        lastForceInvalidateMs = now
        listener()
    }

    companion object {
        /** ~30 Hz — matches Surface paint; IMU fusion is faster but bitmap cost dominates. */
        const val AA_INVALIDATE_MIN_INTERVAL_MS = 33L
        /** Speed/alt/TPMS refresh when attitude is still. */
        private const val PERIODIC_REFRESH_MS = 200L
        private const val ATTITUDE_EPS_DEG = 0.05f
    }
}
