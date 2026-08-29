package dev.foss.expeditiongauge.thermalloginterval

import dev.foss.expeditiongauge.batterysaverrecord.BatterySaverRecord
import dev.foss.expeditiongauge.thermal.ThermalStatus
import dev.foss.expeditiongauge.thermalrecord.ThermalRecordThrottle

/** Auto-picks a log interval from thermal status while a session is recording. */
object ThermalLogInterval {
    fun autoIntervalMs(
        status: ThermalStatus,
        recording: Boolean,
        batterySaver: Boolean = BatterySaverRecord.active,
    ): Long? {
        if (!recording) return null
        val suggested = ThermalRecordThrottle.suggestedIntervalMs(status) ?: return null
        return if (batterySaver) maxOf(suggested, BatterySaverRecord.INTERVAL_MS) else suggested
    }
}
