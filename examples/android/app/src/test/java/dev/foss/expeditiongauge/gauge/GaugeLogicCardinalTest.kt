package dev.foss.expeditiongauge.gauge

import org.junit.Assert.assertEquals
import org.junit.Test

class GaugeLogicCardinalTest {
    @Test
    fun cardinalAbbreviation_sixteenPoints() {
        assertEquals("N", GaugeLogic.cardinalAbbreviation(0f))
        assertEquals("N", GaugeLogic.cardinalAbbreviation(359f))
        assertEquals("NNE", GaugeLogic.cardinalAbbreviation(22.5f))
        assertEquals("NE", GaugeLogic.cardinalAbbreviation(45f))
        assertEquals("ENE", GaugeLogic.cardinalAbbreviation(67.5f))
        assertEquals("E", GaugeLogic.cardinalAbbreviation(90f))
        assertEquals("ESE", GaugeLogic.cardinalAbbreviation(112.5f))
        assertEquals("SE", GaugeLogic.cardinalAbbreviation(135f))
        assertEquals("SSE", GaugeLogic.cardinalAbbreviation(157.5f))
        assertEquals("S", GaugeLogic.cardinalAbbreviation(180f))
        assertEquals("SSW", GaugeLogic.cardinalAbbreviation(202.5f))
        assertEquals("SW", GaugeLogic.cardinalAbbreviation(225f))
        assertEquals("WSW", GaugeLogic.cardinalAbbreviation(247.5f))
        assertEquals("W", GaugeLogic.cardinalAbbreviation(270f))
        assertEquals("WNW", GaugeLogic.cardinalAbbreviation(292.5f))
        assertEquals("NW", GaugeLogic.cardinalAbbreviation(315f))
        assertEquals("NNW", GaugeLogic.cardinalAbbreviation(337.5f))
    }

    @Test
    fun cardinalAbbreviation_normalizesNegativeAndOver360() {
        assertEquals("E", GaugeLogic.cardinalAbbreviation(-270f))
        assertEquals("S", GaugeLogic.cardinalAbbreviation(540f))
    }
}
