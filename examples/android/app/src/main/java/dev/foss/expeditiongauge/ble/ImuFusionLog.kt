package dev.foss.expeditiongauge.ble

import android.util.Log

object ImuFusionLog {
    const val TAG = "ExpeditionGauge/ImuFusion"

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
        Log.d(
            TAG,
            "fusionSource=$fusionSource active=$activeCount " +
                "twist=${chassisTwistDeg ?: 0f} beta=${driftAngleDeg ?: 0f} " +
                "latG=$latG pitch=$pitchDeg roll=$rollDeg " +
                "rot=$displayRotation rawP=$rawPitchDeg rawR=$rawRollDeg",
        )
    }
}
