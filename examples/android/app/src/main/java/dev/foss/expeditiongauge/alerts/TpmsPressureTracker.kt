package dev.foss.expeditiongauge.alerts

class TpmsPressureTracker {
    private data class Sample(val kpa: Float, val timestampMs: Long)

    private val history = mutableMapOf<String, Sample>()

    fun recordLossKpaPerMin(wheelId: String, kpa: Float, timestampMs: Long, windowMs: Long = 60_000L): Float? {
        val prev = history[wheelId] ?: run {
            history[wheelId] = Sample(kpa, timestampMs)
            return null
        }
        val elapsed = timestampMs - prev.timestampMs
        if (elapsed <= 0L) return null
        history[wheelId] = Sample(kpa, timestampMs)
        if (elapsed > windowMs) return null
        val loss = prev.kpa - kpa
        if (loss <= 0f) return null
        return loss * (60_000f / elapsed.toFloat())
    }
}
