package dev.foss.expeditiongauge.ble

import android.util.Log
import kotlin.math.abs

object ImuFusionLog {
    const val TAG = "ExpeditionGauge/ImuFusion"

    /** Min interval between near-identical samples (ADB smoke still sees fusionSource=). */
    const val THROTTLE_NS = 2_000_000_000L

    private var lastLogNs = 0L
    private var lastFusionSource: String? = null
    private var lastPitch = Float.NaN
    private var lastRoll = Float.NaN
    private var lastActive = -1

    fun publish(
        fusionSource: String,
        activeCount: Int,
        chassisTwistDeg: Float?,
        driftAngleDeg: Float?,
        latG: Float,
        pitchDeg: Float,
        rollDeg: Float,
        displayRotation: Int = 0,
        rawPitchDeg: Float = 0f,
        rawRollDeg: Float = 0f,
    ) {
        val nowNs = System.nanoTime()
        val unchanged = fusionSource == lastFusionSource &&
            activeCount == lastActive &&
            abs(pitchDeg - lastPitch) < DELTA_DEG &&
            abs(rollDeg - lastRoll) < DELTA_DEG
        if (unchanged && nowNs - lastLogNs <= THROTTLE_NS) return
        // Cap burst rate even when attitude is changing (desk settle / rotate).
        if (nowNs - lastLogNs <= MIN_BURST_NS) return
        lastLogNs = nowNs
        lastFusionSource = fusionSource
        lastActive = activeCount
        lastPitch = pitchDeg
        lastRoll = rollDeg
        Log.d(
            TAG,
            "fusionSource=$fusionSource active=$activeCount " +
                "twist=${chassisTwistDeg ?: 0f} beta=${driftAngleDeg ?: 0f} " +
                "latG=$latG pitch=$pitchDeg roll=$rollDeg " +
                "rot=$displayRotation rawP=$rawPitchDeg rawR=$rawRollDeg",
        )
    }

    /** Test / process-restart hook. */
    fun resetThrottleForTests() {
        lastLogNs = 0L
        lastFusionSource = null
        lastPitch = Float.NaN
        lastRoll = Float.NaN
        lastActive = -1
    }

    private const val DELTA_DEG = 0.5f
    private const val MIN_BURST_NS = 1_000_000_000L
}
