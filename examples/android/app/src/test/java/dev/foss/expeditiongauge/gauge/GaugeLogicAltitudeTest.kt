package dev.foss.expeditiongauge.gauge

import org.junit.Assert.assertEquals
import org.junit.Test

class GaugeLogicAltitudeTest {
    @Test
    fun formatAltitude_metric() {
        assertEquals("120 m", GaugeLogic.formatAltitude(119.6, useMetric = true))
    }

    @Test
    fun formatAltitude_imperial() {
        assertEquals("394 ft", GaugeLogic.formatAltitude(120.0, useMetric = false))
    }

    @Test
    fun formatWholeDegrees_positiveAndNegative() {
        assertEquals("+3°", GaugeLogic.formatWholeDegrees(2.6f))
        assertEquals("-2°", GaugeLogic.formatWholeDegrees(-2.4f))
    }
}
