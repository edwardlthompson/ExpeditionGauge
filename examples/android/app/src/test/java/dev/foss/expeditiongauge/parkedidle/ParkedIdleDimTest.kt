package dev.foss.expeditiongauge.parkedidle

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ParkedIdleDimTest {
    @Test
    fun dimsWhenParkedAndLeavesMovingUnchanged() {
        assertTrue(ParkedIdleDim.parked(null))
        assertTrue(ParkedIdleDim.parked(0.1f))
        assertFalse(ParkedIdleDim.parked(8f))
        assertEquals(ParkedIdleDim.DIM, ParkedIdleDim.apply(0.92f, parked = true), 0.001f)
        assertEquals(ParkedIdleDim.DIM, ParkedIdleDim.apply(-1f, parked = true), 0.001f)
        assertEquals(0.92f, ParkedIdleDim.apply(0.92f, parked = false), 0.001f)
    }
}
