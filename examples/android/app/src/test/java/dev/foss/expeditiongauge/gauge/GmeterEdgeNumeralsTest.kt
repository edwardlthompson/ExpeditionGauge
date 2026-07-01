package dev.foss.expeditiongauge.gauge

import org.junit.Assert.assertEquals
import org.junit.Test

class GmeterEdgeNumeralsTest {
    @Test
    fun topPitch_onlyWhenBallAboveCenter() {
        assertEquals("-10°", GmeterEdgeNumerals.topPitchReadout(-10f, -0.3f))
        assertEquals("--", GmeterEdgeNumerals.topPitchReadout(-10f, 0.3f))
    }

    @Test
    fun bottomPitch_onlyWhenBallBelowCenter() {
        assertEquals("+10°", GmeterEdgeNumerals.bottomPitchReadout(10f, 0.3f))
        assertEquals("--", GmeterEdgeNumerals.bottomPitchReadout(10f, -0.3f))
    }

    @Test
    fun roll_onlyOnActiveSide() {
        assertEquals("-5°", GmeterEdgeNumerals.leftRollReadout(-5f, -0.2f))
        assertEquals("--", GmeterEdgeNumerals.rightRollReadout(-5f, -0.2f))
        assertEquals("+5°", GmeterEdgeNumerals.rightRollReadout(5f, 0.2f))
        assertEquals("--", GmeterEdgeNumerals.leftRollReadout(5f, 0.2f))
    }
}
