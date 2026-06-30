package dev.foss.expeditiongauge.gauge

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class GForceBallLogicTest {
    @Test
    fun mapsLatLonGToNormalizedPosition() {
        val ball = GForceBallLogic.mapLatLonG(latG = 0.75f, lonG = -0.5f)
        assertEquals(0.5f, ball.normalizedX, 0.01f)
        assertEquals(0.333f, ball.normalizedY, 0.02f)
    }

    @Test
    fun criticalZoneAtHighG() {
        val ball = GForceBallLogic.mapLatLonG(latG = 1.5f, lonG = 0f)
        assertEquals(GaugeZone.Critical, ball.zone)
    }

    @Test
    fun clampsBeyondMaxG() {
        val ball = GForceBallLogic.mapLatLonG(latG = 3f, lonG = 3f)
        assertTrue(ball.normalizedX <= 1f)
        assertTrue(ball.normalizedY >= -1f)
    }
}
