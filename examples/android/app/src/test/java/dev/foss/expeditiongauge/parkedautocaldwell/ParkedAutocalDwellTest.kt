package dev.foss.expeditiongauge.parkedautocaldwell

import org.junit.Assert.assertEquals
import org.junit.Test

class ParkedAutocalDwellTest {
    @Test
    fun parkedHoldIsLonger() {
        assertEquals(2_500L, ParkedAutocalDwell.holdMs(false))
        assertEquals(5_000L, ParkedAutocalDwell.holdMs(true))
    }
}
