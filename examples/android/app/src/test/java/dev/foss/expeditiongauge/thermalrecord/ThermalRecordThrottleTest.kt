package dev.foss.expeditiongauge.thermalrecord

import dev.foss.expeditiongauge.thermal.ThermalStatus
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ThermalRecordThrottleTest {
    @Test
    fun mapsStatusToInterval() {
        assertNull(ThermalRecordThrottle.suggestedIntervalMs(ThermalStatus.Normal))
        assertEquals(50L, ThermalRecordThrottle.suggestedIntervalMs(ThermalStatus.Warning))
        assertEquals(200L, ThermalRecordThrottle.suggestedIntervalMs(ThermalStatus.Critical))
        assertEquals(20, ThermalRecordThrottle.hzLabel(50L))
        assertEquals(5, ThermalRecordThrottle.hzLabel(200L))
    }
}
