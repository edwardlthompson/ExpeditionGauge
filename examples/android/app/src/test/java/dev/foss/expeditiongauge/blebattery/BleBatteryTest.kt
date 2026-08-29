package dev.foss.expeditiongauge.blebattery

import org.junit.Assert.assertEquals
import org.junit.Test

class BleBatteryTest {
    @Test
    fun bandsPercent() {
        assertEquals("low", BleBattery.band(10))
        assertEquals("warn", BleBattery.band(30))
        assertEquals("ok", BleBattery.band(80))
        assertEquals(100, BleBattery.parsePercent(140))
        assertEquals("unknown", BleBattery.band(null))
    }
}
