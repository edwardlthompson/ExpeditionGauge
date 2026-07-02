package dev.foss.expeditiongauge.car.gauge

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class InclinometerColorTest {
    @Test
    fun greenAtZero_redAtMax() {
        val green = InclinometerColor.argbForAngleMagnitude(0f)
        val red = InclinometerColor.argbForAngleMagnitude(45f)
        assertEquals(0xFF33FF33.toInt(), green)
        assertEquals(0xFFFF3333.toInt(), red)
    }

    @Test
    fun goldenArgb_at15And30Degrees() {
        val at15 = InclinometerColor.argbForAngleMagnitude(15f)
        val at30 = InclinometerColor.argbForAngleMagnitude(30f)
        assertTrue(at15 != 0xFF33FF33.toInt())
        assertTrue(at30 != 0xFFFF3333.toInt())
        assertTrue(at15 != at30)
        assertEquals(0xFFFFDD00.toInt(), InclinometerColor.argbForNormalized(0.5f))
    }

    @Test
    fun maxDeg_is45() {
        assertEquals(45f, InclinometerColor.MAX_DEG)
    }
}
