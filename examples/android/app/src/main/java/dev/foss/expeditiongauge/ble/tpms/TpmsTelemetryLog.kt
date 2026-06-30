package dev.foss.expeditiongauge.ble.tpms

import android.util.Log

object TpmsTelemetryLog {
    const val TAG = "ExpeditionGauge/Tpms"

    fun publish(corner: String, pressureKpa: Float?, tempC: Float?, parserId: String) {
        Log.d(
            TAG,
            "corner=$corner pressureKpa=${pressureKpa ?: 0f} tempC=${tempC ?: 0f} parser=$parserId",
        )
    }
}
