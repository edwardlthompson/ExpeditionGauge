package dev.foss.expeditiongauge.obd

import android.util.Log

object ObdTelemetryLog {
    const val TAG = "ExpeditionGauge/Obd"

    fun publish(snapshot: ObdSnapshot, slipRatio: Float?, rearSlipRatio: Float?, betaDeg: Float?) {
        if (!snapshot.connected) return
        Log.d(
            TAG,
            "rpm=${snapshot.rpm ?: 0f} speed=${snapshot.speedKmh ?: 0f} " +
                "throttle=${snapshot.throttlePct ?: 0f} load=${snapshot.engineLoadPct ?: 0f} " +
                "slipRatio=${slipRatio ?: 0f} rearSlip=${rearSlipRatio ?: 0f} beta=${betaDeg ?: 0f}",
        )
    }
}
