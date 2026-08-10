package dev.foss.expeditiongauge.obd

data class ObdSnapshot(
    val connected: Boolean = false,
    val rpm: Float? = null,
    val speedKmh: Float? = null,
    val throttlePct: Float? = null,
    val engineLoadPct: Float? = null,
    val wheelSpeedKmh: Float? = null,
    val rearLeftKmh: Float? = null,
    val rearRightKmh: Float? = null,
    val batteryVoltage: Float? = null,
)
