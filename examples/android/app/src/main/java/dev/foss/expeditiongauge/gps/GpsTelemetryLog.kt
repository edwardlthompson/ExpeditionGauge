package dev.foss.expeditiongauge.gps

import android.util.Log

object GpsTelemetryLog {
    const val TAG = "ExpeditionGauge/Gps"

    fun publish(fix: NmeaFix, source: String) {
        Log.d(
            TAG,
            "source=$source lat=${fix.latitude} lon=${fix.longitude} " +
                "speedMps=${fix.speedMps ?: 0f} course=${fix.courseDeg ?: 0f} " +
                "sats=${fix.numSatellites ?: 0} hdop=${fix.hdop ?: 0f} quality=${fix.fixQuality}",
        )
    }
}
