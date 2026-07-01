package dev.foss.expeditiongauge.gauge

import org.junit.Assert.assertEquals
import org.junit.Test

class GaugeLogicWholeGTest {
    @Test
    fun formatWholeGRoundsToNearestInteger() {
        assertEquals("1", GaugeLogic.formatWholeG(0.6f))
        assertEquals("-1", GaugeLogic.formatWholeG(-1.4f))
        assertEquals("0", GaugeLogic.formatWholeG(0.2f))
    }
}
