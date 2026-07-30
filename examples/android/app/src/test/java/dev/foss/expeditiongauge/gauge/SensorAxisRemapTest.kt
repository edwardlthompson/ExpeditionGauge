package dev.foss.expeditiongauge.gauge

import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Regression lock for ADR-0013 — landscape inclinometer after portrait Zero.
 * Changing these matrices without device validation will re-break the horizon.
 */
class SensorAxisRemapTest {
    @Test
    fun rotation0_identity() {
        val (x, y, z) = SensorAxisRemap.remap(1f, 2f, 3f, 0)
        assertEquals(1f, x, 0f)
        assertEquals(2f, y, 0f)
        assertEquals(3f, z, 0f)
    }

    @Test
    fun rotation90_mapsUprightGravityToPortraitY() {
        // Landscape ROTATION_90 (phone CCW): gravity along device +X → screen-up +Y
        val (x, y, z) = SensorAxisRemap.remap(9.81f, 0f, 0f, 1)
        assertEquals(0f, x, 0.001f)
        assertEquals(9.81f, y, 0.001f)
        assertEquals(0f, z, 0.001f)
    }

    @Test
    fun rotation270_mapsUprightGravityToPortraitY() {
        val (x, y, z) = SensorAxisRemap.remap(-9.81f, 0f, 0f, 3)
        assertEquals(0f, x, 0.001f)
        assertEquals(9.81f, y, 0.001f)
        assertEquals(0f, z, 0.001f)
    }

    @Test
    fun rotation180_negatesXY() {
        val (x, y, z) = SensorAxisRemap.remap(1f, 2f, 3f, 2)
        assertEquals(-1f, x, 0f)
        assertEquals(-2f, y, 0f)
        assertEquals(3f, z, 0f)
    }

    @Test
    fun rotation90_preservesScreenRightAsPlusX() {
        // Device +Y (toward former top) becomes screen −X after 90° CCW
        val (x, y, z) = SensorAxisRemap.remap(0f, 9.81f, 0f, 1)
        assertEquals(-9.81f, x, 0.001f)
        assertEquals(0f, y, 0.001f)
        assertEquals(0f, z, 0.001f)
    }

    @Test
    fun rotation90_zUnchanged() {
        val (x, y, z) = SensorAxisRemap.remap(0f, 0f, 9.81f, 1)
        assertEquals(0f, x, 0f)
        assertEquals(0f, y, 0f)
        assertEquals(9.81f, z, 0f)
    }

    @Test
    fun inverseRemap_roundTripsAllRotations() {
        for (rot in 0..3) {
            val remapped = SensorAxisRemap.remap(1.5f, -2.5f, 4f, rot)
            val back = SensorAxisRemap.inverseRemap(remapped.first, remapped.second, remapped.third, rot)
            assertEquals("rot=$rot x", 1.5f, back.first, 0.001f)
            assertEquals("rot=$rot y", -2.5f, back.second, 0.001f)
            assertEquals("rot=$rot z", 4f, back.third, 0.001f)
        }
    }
}
