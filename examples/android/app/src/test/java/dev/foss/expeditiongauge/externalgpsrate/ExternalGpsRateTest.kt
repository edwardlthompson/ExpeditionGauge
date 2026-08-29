package dev.foss.expeditiongauge.externalgpsrate

import org.junit.Assert.assertEquals
import org.junit.Test

class ExternalGpsRateTest {
    @Test
    fun clampsBaudAndRate() {
        assertEquals(9600, ExternalGpsRate.clampBaud(10000))
        assertEquals(1, ExternalGpsRate.clampHz(0))
        assertEquals(20, ExternalGpsRate.clampHz(99))
    }
}
