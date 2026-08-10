package dev.foss.expeditiongauge.gps

/**
 * External NMEA wins over phone GNSS whenever the external link is up
 * or a recent valid external fix is still fresh.
 */
object GpsSourcePriority {
    fun preferExternal(
        externalConnected: Boolean,
        externalFix: NmeaFix,
        nowMs: Long = System.currentTimeMillis(),
        staleMs: Long = ExternalNmeaGpsManager.STALE_MS,
    ): Boolean {
        if (externalConnected) return true
        if (!externalFix.valid) return false
        return nowMs - externalFix.timestampMs <= staleMs
    }
}
