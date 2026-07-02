package dev.foss.expeditiongauge.map

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class MapRegionBoundsTest {
    @Test
    fun expandAddsMargin() {
        val bounds = MapRegionBounds(47.0, 47.1, -122.0, -121.9)
        val expanded = bounds.expand(0.1)
        assertTrue(expanded.minLat < bounds.minLat)
        assertTrue(expanded.maxLat > bounds.maxLat)
    }

    @Test
    fun fromCenterRadiusBuildsValidBounds() {
        val bounds = MapRegionBounds.fromCenterRadiusKm(45.0, -93.0, 25f)
        assertTrue(bounds.isValid)
        assertEquals(45.0, (bounds.minLat + bounds.maxLat) / 2.0, 0.5)
    }
}
