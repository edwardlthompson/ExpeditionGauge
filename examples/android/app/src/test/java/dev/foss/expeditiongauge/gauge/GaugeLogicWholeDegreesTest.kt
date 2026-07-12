package dev.foss.expeditiongauge.gauge

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test

class GaugeLogicWholeDegreesTest {
    @Test
    fun formatters_neverContainDecimalPoint() {
        val samples = listOf(0f, 0.4f, 0.6f, -0.4f, -1.9f, 12.49f, -12.51f, 179.9f)
        for (v in samples) {
            val signed = GaugeLogic.formatSignedDegrees(v)
            val whole = GaugeLogic.formatWholeDegrees(v)
            assertFalse("signed=$signed", signed.contains('.'))
            assertFalse("whole=$whole", whole.contains('.'))
            assertTrueEndsWithDegree(signed)
            assertTrueEndsWithDegree(whole)
        }
    }

    @Test
    fun formatSignedDegrees_delegatesToWhole() {
        assertEquals(GaugeLogic.formatWholeDegrees(2.6f), GaugeLogic.formatSignedDegrees(2.6f))
        assertEquals("+3°", GaugeLogic.formatSignedDegrees(2.6f))
        assertEquals("-2°", GaugeLogic.formatSignedDegrees(-2.4f))
    }

    private fun assertTrueEndsWithDegree(s: String) {
        assertEquals('°', s.last())
    }
}
