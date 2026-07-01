package dev.foss.expeditiongauge.gauge

import org.junit.Assert.assertEquals
import org.junit.Test

class GaugeLogicPaddedFormatsTest {
    @Test
    fun formatSpeedPadded_zeroPadsToThreeDigits() {
        assertEquals("000", GaugeLogic.formatSpeedPadded(0f, useMetric = true))
    }

    @Test
    fun formatSpeedPadded_typicalSpeed() {
        assertEquals("045", GaugeLogic.formatSpeedPadded(12.5f, useMetric = true))
        assertEquals("128", GaugeLogic.formatSpeedPadded(128f / 3.6f, useMetric = true))
    }

    @Test
    fun formatHeadingPadded_threeZeroPaddedDigits() {
        assertEquals("247", GaugeLogic.formatHeadingPadded(247.4f))
        assertEquals("012", GaugeLogic.formatHeadingPadded(12f))
        assertEquals("000", GaugeLogic.formatHeadingPadded(0f))
    }
}
