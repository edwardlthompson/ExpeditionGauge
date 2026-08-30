package dev.foss.expeditiongauge.car.gauge

import org.junit.Assert.assertEquals
import org.junit.Test

class SatCountBadgeTest {
    @Test
    fun labelClampsToTwoDigits() {
        assertEquals("0", SatCountBadge.label(0))
        assertEquals("12", SatCountBadge.label(12))
        assertEquals("99", SatCountBadge.label(140))
        assertEquals("0", SatCountBadge.label(-3))
    }
}
