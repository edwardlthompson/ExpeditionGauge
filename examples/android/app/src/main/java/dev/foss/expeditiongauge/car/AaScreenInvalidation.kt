package dev.foss.expeditiongauge.car

import kotlin.math.abs

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
        val attitudeChanged = abs(pitchDeg - lastSamplePitch) > 0.1f ||
            abs(rollDeg - lastSampleRoll) > 0.1f
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
        const val AA_INVALIDATE_MIN_INTERVAL_MS = 250L
        private const val PERIODIC_REFRESH_MS = 1_000L
    }
}
