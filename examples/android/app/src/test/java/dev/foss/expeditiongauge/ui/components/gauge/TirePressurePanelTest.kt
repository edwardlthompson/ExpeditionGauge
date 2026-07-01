package dev.foss.expeditiongauge.ui.components.gauge

import dev.foss.expeditiongauge.settings.PressureUnit
import dev.foss.expeditiongauge.settings.TempUnit
import dev.foss.expeditiongauge.telemetry.TirePressureReading
import org.junit.Assert.assertEquals
import org.junit.Test

class TirePressurePanelTest {
    @Test
    fun formatPressure_nullPsi_showsDash() {
        assertEquals("--", formatPressure(TirePressureReading(), PressureUnit.PSI))
    }

    @Test
    fun formatPressure_zeroPsi_showsZero() {
        assertEquals("0.0 psi", formatPressure(TirePressureReading(psi = 0f), PressureUnit.PSI))
    }

    @Test
    fun formatTemp_null_showsDash() {
        assertEquals("--", formatTemp(null, TempUnit.CELSIUS))
    }

    @Test
    fun formatTemp_zero_showsZero() {
        assertEquals("0°C", formatTemp(0f, TempUnit.CELSIUS))
    }
}
