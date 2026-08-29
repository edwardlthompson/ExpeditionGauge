package dev.foss.expeditiongauge.gearestimate

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class GearEstimateTest {
    @Test
    fun bandsFromRpmAndSpeed() {
        assertEquals(1, GearEstimate.estimate(2_500f, 20f / 3.6f))
        assertEquals(2, GearEstimate.estimate(2_000f, 30f / 3.6f))
        assertEquals(3, GearEstimate.estimate(2_000f, 40f / 3.6f))
        assertEquals(4, GearEstimate.estimate(2_000f, 58f / 3.6f))
        assertEquals(5, GearEstimate.estimate(2_000f, 68f / 3.6f))
        assertEquals(6, GearEstimate.estimate(2_000f, 100f / 3.6f))
    }

    @Test
    fun hidesWhenIdleOrMissing() {
        assertNull(GearEstimate.estimate(null, 10f))
        assertNull(GearEstimate.estimate(600f, 10f))
        assertNull(GearEstimate.estimate(2_000f, 0f))
        assertNull(GearEstimate.line(2_000f, 0f))
        val line = GearEstimate.line(2_000f, 60f / 3.6f)
        assertEquals("Gear 4", line)
        assertTrue(GearEstimate.matches(line!!))
    }
}
