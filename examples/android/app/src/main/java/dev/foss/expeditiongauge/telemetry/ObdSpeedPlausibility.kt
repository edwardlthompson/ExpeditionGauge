package dev.foss.expeditiongauge.telemetry

/** Reject mis-parsed OBD speed samples before they reach the HUD. */
internal object ObdSpeedPlausibility {
    fun resolveMps(speedKmh: Float?, gpsSpeedMps: Float, fallbackMps: Float): Float {
        if (speedKmh == null) return fallbackMps
        val obdMps = speedKmh / 3.6f
        return if (isPlausible(speedKmh, gpsSpeedMps)) obdMps else gpsSpeedMps
    }

    fun isPlausible(speedKmh: Float, gpsSpeedMps: Float): Boolean {
        if (speedKmh < 0f || speedKmh > 250f) return false
        val obdMps = speedKmh / 3.6f
        if (gpsSpeedMps < 2.5f && obdMps > 20f) return false
        if (gpsSpeedMps > 2.5f && kotlin.math.abs(obdMps - gpsSpeedMps) > 25f) return false
        return true
    }
}
