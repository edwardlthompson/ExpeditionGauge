package dev.foss.expeditiongauge.storagemeter

/** Percent of the session storage budget currently used. */
object StorageMeter {
    fun percentUsed(usedBytes: Long, allowedBytes: Long): Int {
        if (allowedBytes <= 0L) return 100
        return ((usedBytes * 100.0 / allowedBytes).toInt()).coerceIn(0, 100)
    }
}
