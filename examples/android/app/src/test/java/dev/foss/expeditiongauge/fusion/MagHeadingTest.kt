package dev.foss.expeditiongauge.fusion

import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.math.abs

class MagHeadingTest {
    @Test
    fun weakMag_returnsNull() {
        assertNull(MagHeading.yawDeg(0f, 0f, 9.81f, 0.1f, 0.1f, 0.1f))
    }

    @Test
    fun levelNorthish_returnsFiniteYaw() {
        val yaw = MagHeading.yawDeg(0f, 0f, 9.81f, 25f, 0f, 40f)
        assertNotNull(yaw)
        assertTrue(abs(yaw!!) <= 180f)
    }
}
