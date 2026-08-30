package dev.foss.expeditiongauge.keepawake

object KeepAwakeMoving {
    const val MOVING_MPS = 0.5f

    fun moving(speedMps: Float?): Boolean = speedMps != null && speedMps >= MOVING_MPS

    fun parked(speedMps: Float?): Boolean = !moving(speedMps)

    fun shouldKeep(preferenceEnabled: Boolean, speedMps: Float?): Boolean =
        preferenceEnabled && moving(speedMps)
}
