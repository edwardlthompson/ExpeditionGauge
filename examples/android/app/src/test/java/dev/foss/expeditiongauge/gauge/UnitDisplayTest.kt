package dev.foss.expeditiongauge.gauge

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class UnitDisplayTest {
    @Test
    fun speedMpsToDisplay_metric() {
        assertEquals(36f, UnitDisplay.speedMpsToDisplay(10f, dev.foss.expeditiongauge.settings.SpeedUnit.METRIC), 0.01f)
    }

    @Test
    fun speedMpsToDisplay_imperial() {
        assertEquals(22.3694f, UnitDisplay.speedMpsToDisplay(10f, dev.foss.expeditiongauge.settings.SpeedUnit.IMPERIAL), 0.01f)
    }

    @Test
    fun pressureKpaToDisplay_psi() {
        assertTrue(UnitDisplay.pressureKpaToDisplay(200f, dev.foss.expeditiongauge.settings.PressureUnit.PSI) < 30f)
    }
}
