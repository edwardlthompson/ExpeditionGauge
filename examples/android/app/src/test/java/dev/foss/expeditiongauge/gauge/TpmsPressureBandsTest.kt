package dev.foss.expeditiongauge.gauge

import org.junit.Assert.assertEquals
import org.junit.Test

class TpmsPressureBandsTest {
    @Test
    fun band_disconnected_whenNull() {
        assertEquals(TpmsPressureBands.Band.DISCONNECTED, TpmsPressureBands.band(null))
    }

    @Test
    fun band_critical_below25Psi() {
        assertEquals(TpmsPressureBands.Band.CRITICAL, TpmsPressureBands.band(20f))
    }

    @Test
    fun band_low_betweenThresholds() {
        assertEquals(TpmsPressureBands.Band.LOW, TpmsPressureBands.band(26f))
    }

    @Test
    fun band_ok_atNormalPressure() {
        assertEquals(TpmsPressureBands.Band.OK, TpmsPressureBands.band(35f))
    }
}
