package dev.foss.expeditiongauge.fusion

import dev.foss.expeditiongauge.ble.WitMotionSample
import kotlin.math.abs

/**
 * Lightweight per-device orientation filter for external WitMotion IMU packets.
 */
class ImuOrientationFilter {
    private var lastSample: WitMotionSample? = null
    private var noiseScore = 1f

    fun onSample(sample: WitMotionSample) {
        val prev = lastSample
        if (prev != null) {
            val delta = abs(sample.yawDeg - prev.yawDeg)
            noiseScore = (noiseScore * 0.9f + (if (delta < 5f) 1f else 0.5f) * 0.1f).coerceIn(0.1f, 1f)
        }
        lastSample = sample
    }

    fun yawDeg(): Float = lastSample?.yawDeg ?: 0f
    fun pitchDeg(): Float = lastSample?.pitchDeg ?: 0f
    fun rollDeg(): Float = lastSample?.rollDeg ?: 0f
    fun latG(): Float = (lastSample?.ayG ?: 0f)
    fun lonG(): Float = (lastSample?.axG ?: 0f)
    fun yawRateDegPerSec(): Float = lastSample?.gzDegPerSec ?: 0f
    fun quality(): Float = noiseScore
}
