package dev.foss.expeditiongauge.alerts

/**
 * Suppresses pitch/roll audible feedback until a short floor elapses **and**
 * [AttitudeSettleGate] reports Madgwick attitude is stable.
 * Level-triggered UI alerts still evaluate; only feedback (TTS/beep) is gated.
 */
object AlertStartupGrace {
    /** Minimum wall time before attitude TTS even if already stable (avoids first-frame noise). */
    const val ATTITUDE_GRACE_MS = 1_500L

    fun suppressFeedback(
        type: AlertType,
        elapsedSinceStartMs: Long,
        attitudeSettled: Boolean,
        graceMs: Long = ATTITUDE_GRACE_MS,
    ): Boolean {
        if (type != AlertType.PITCH && type != AlertType.ROLL) return false
        if (elapsedSinceStartMs < graceMs) return true
        return !attitudeSettled
    }
}
