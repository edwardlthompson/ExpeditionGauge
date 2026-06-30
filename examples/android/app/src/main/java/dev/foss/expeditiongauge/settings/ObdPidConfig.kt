package dev.foss.expeditiongauge.settings

data class ObdPidConfig(
    val rpm: Boolean = true,
    val speed: Boolean = true,
    val throttle: Boolean = true,
    val load: Boolean = true,
    val voltage: Boolean = true,
    val rearWheels: Boolean = true,
)
