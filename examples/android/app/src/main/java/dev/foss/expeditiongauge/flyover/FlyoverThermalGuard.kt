package dev.foss.expeditiongauge.flyover

import android.content.Context
import dev.foss.expeditiongauge.thermal.ThermalMonitor
import dev.foss.expeditiongauge.thermal.ThermalStatus

object FlyoverThermalGuard {
    fun frameDelayMs(context: Context): Long {
        val monitor = ThermalMonitor(context.applicationContext)
        monitor.refresh()
        return when (monitor.status.value) {
            ThermalStatus.Critical -> 500L
            ThermalStatus.Warning -> 150L
            ThermalStatus.Normal -> 0L
        }
    }
}
