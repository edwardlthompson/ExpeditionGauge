package dev.foss.expeditiongauge.gnssdeadreckon

import org.junit.Assert.assertTrue
import org.junit.Test

class GnssDeadReckonTest {
    @Test
    fun stepsNorth() {
        val next = GnssDeadReckon.step(GeoFix(0.0, 0.0), speedMps = 10f, headingDeg = 0f, dtSec = 1f)
        assertTrue(next.latitude > 0.0)
    }
}
