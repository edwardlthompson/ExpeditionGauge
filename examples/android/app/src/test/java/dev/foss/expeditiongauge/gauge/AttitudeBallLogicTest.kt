package dev.foss.expeditiongauge.gauge

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.math.abs

class AttitudeBallLogicTest {
    @Test
    fun levelSurfaceMapsToCenter() {
        val ball = AttitudeBallLogic.mapPitchRoll(0f, 0f)
        assertEquals(0f, ball.normalizedX, 0.001f)
        assertEquals(0f, ball.normalizedY, 0.001f)
        assertEquals(GaugeZone.Safe, ball.zone)
    }

    @Test
    fun positiveRollMovesBallRight() {
        val ball = AttitudeBallLogic.mapPitchRoll(0f, 15f)
        assertTrue(ball.normalizedX > 0f)
        assertEquals(GaugeZone.Caution, ball.zone)
    }

    @Test
    fun extremeAnglesClampToRing() {
        val ball = AttitudeBallLogic.mapPitchRoll(45f, 45f)
        val distance = kotlin.math.hypot(ball.normalizedX.toDouble(), ball.normalizedY.toDouble())
        assertEquals(1.0, distance, 0.001)
        assertEquals(GaugeZone.Critical, ball.zone)
    }

    @Test
    fun ringRadiusFractionScalesWithThreshold() {
        assertEquals(1f / 3f, AttitudeBallLogic.ringRadiusFraction(10f), 0.001f)
        assertEquals(1f, AttitudeBallLogic.ringRadiusFraction(30f), 0.001f)
    }

    @Test
    fun negativePitchMovesBallTowardTop() {
        val ball = AttitudeBallLogic.mapPitchRoll(-10f, 0f)
        assertTrue(ball.normalizedY < 0f)
    }

    @Test
    fun positivePitchMovesBallTowardBottom() {
        val ball = AttitudeBallLogic.mapPitchRoll(10f, 0f)
        assertTrue(ball.normalizedY > 0f)
    }
}
