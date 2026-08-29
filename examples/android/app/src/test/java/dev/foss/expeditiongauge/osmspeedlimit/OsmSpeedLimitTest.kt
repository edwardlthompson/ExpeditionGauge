package dev.foss.expeditiongauge.osmspeedlimit

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class OsmSpeedLimitTest {
    @Test
    fun looksUpNearestPostedLimit() {
        val zones = OsmSpeedLimit.parse("47.0,-122.0,2.0,50\n47.0,-122.0,0.5,30")
        assertEquals(30, OsmSpeedLimit.lookup(47.0, -122.0, zones))
        assertNull(OsmSpeedLimit.lookup(48.0, -122.0, zones))
        assertEquals("30 km/h", OsmSpeedLimit.overlayLabel(30))
    }
}
