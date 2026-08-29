package dev.foss.expeditiongauge.obdtrip

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ObdTripTest {
    @Test
    fun parseDistanceWarmupsAndTime() {
        assertEquals(42, ObdTrip.parseDistanceKm("41 31 00 2A"))
        assertEquals(3, ObdTrip.parseWarmups("41 30 03"))
        assertEquals(90, ObdTrip.parseTimeMin("41 4E 00 5A"))
    }

    @Test
    fun lineJoinsPresentFields() {
        val trip = ObdTripSinceClear(distanceKm = 42, warmups = 3, timeMin = 90)
        assertEquals("Since clear: 42 km · 3 wu · 90 min", ObdTrip.line(trip))
        assertNull(ObdTrip.line(ObdTripSinceClear()))
        assertNull(ObdTrip.parseDistanceKm("NO DATA"))
    }
}
