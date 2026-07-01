package dev.foss.expeditiongauge.gauge

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class GmeterBallColorTest {
    @Test
    fun centerIsGreen() {
        val ball = BallPosition(0f, 0f, GaugeZone.Safe)
        assertEquals(0f, GmeterBallColor.normalizedDistance(ball), 0.001f)
    }

    @Test
    fun edgeTrendsRed() {
        val color = GmeterBallColor.colorForDistance(1f)
        assertTrue(color.red > 0.9f)
        assertTrue(color.green < 0.4f)
    }
}
