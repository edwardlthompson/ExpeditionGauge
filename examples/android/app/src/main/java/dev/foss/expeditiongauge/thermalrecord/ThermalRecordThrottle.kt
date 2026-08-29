package dev.foss.expeditiongauge.thermalrecord

import dev.foss.expeditiongauge.thermal.ThermalStatus

/** Maps phone thermal status to a safer recording log interval. */
object ThermalRecordThrottle {
    fun suggestedIntervalMs(status: ThermalStatus): Long? = when (status) {
        ThermalStatus.Normal -> null
        ThermalStatus.Warning -> 50L
        ThermalStatus.Critical -> 200L
    }

    fun hzLabel(intervalMs: Long): Int = (1000L / intervalMs.coerceAtLeast(1L)).toInt()
}
