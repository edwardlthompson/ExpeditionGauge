package dev.foss.expeditiongauge.hapticalerts

object HapticOverLimit {
    fun shouldVibrate(enabled: Boolean, overLimit: Boolean): Boolean = enabled && overLimit
}
