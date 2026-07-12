package dev.foss.expeditiongauge.gauge

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.math.abs

class CompassBallLogicTest {
    @Test
    fun resolveYaw_prefersBodyYaw() {
        assertEquals(42f, CompassBallLogic.resolveYawDeg(42f, 10f))
    }

    @Test
    fun resolveYaw_fallsBackToHeading() {
        assertEquals(90f, CompassBallLogic.resolveYawDeg(null, 90f))
    }

    @Test
    fun resolveYaw_nullWhenNeitherFinite() {
        assertNull(CompassBallLogic.resolveYawDeg(null, Float.NaN))
    }

    @Test
    fun project_levelEquatorNearCenterHorizontally() {
        val p = CompassBallLogic.project(
            CompassBallLogic.spherePoint(0f, 0f),
            pitchDeg = 0f,
            rollDeg = 0f,
            yawDeg = 0f,
        )
        assertTrue(abs(p.x) < 0.15f)
        assertTrue(abs(p.y) < 0.15f)
        assertTrue(p.depth > 0f)
    }

    @Test
    fun project_yaw90MovesNorthCardinalSideways() {
        val before = CompassBallLogic.project(
            CompassBallLogic.spherePoint(0f, CompassBallLogic.cardinalLonDeg('N')),
            0f, 0f, 0f,
        )
        val after = CompassBallLogic.project(
            CompassBallLogic.spherePoint(0f, CompassBallLogic.cardinalLonDeg('N')),
            0f, 0f, 90f,
        )
        assertTrue(abs(after.x - before.x) > 0.3f || abs(after.depth - before.depth) > 0.3f)
    }

    @Test
    fun lerpYaw_crossesWrap() {
        val mid = CompassBallLogic.lerpYawDeg(350f, 10f, 0.5f)
        assertTrue(mid in 0f..20f || mid in 350f..360f || mid in -10f..10f)
    }

    @Test
    fun forAndroidAuto_mapsCompassToInclinometer() {
        assertEquals(
            AttitudeGaugeMode.INCLINOMETER_LADDER,
            AttitudeGaugeMode.COMPASS_BALL.forAndroidAuto(),
        )
        assertEquals(
            AttitudeGaugeMode.INCLINOMETER_LADDER,
            AttitudeGaugeMode.G_FORCE.forAndroidAuto(),
        )
    }
}
