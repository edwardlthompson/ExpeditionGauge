package dev.foss.expeditiongauge.gps

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class AltitudeNormalizerTest {
    @Test
    fun normalizeMsl_passesThrough() {
        assertEquals(120.0, AltitudeNormalizer.normalizeMsl(120.0), 0.01)
    }

    @Test
    fun normalizeEllipsoid_appliesGeoidCorrection() {
        val msl = AltitudeNormalizer.normalizeEllipsoid(ellipsoidM = -44.0, latitude = 45.0, longitude = -122.0)
        assertTrue("MSL should be higher than ellipsoid near 45N,-122W", msl > -44.0)
        assertTrue("MSL should be plausible for low elevation", msl > -35.0 && msl < 10.0)
    }

    @Test
    fun geoidUndulation_interpolatesBetweenNodes() {
        val n = AltitudeNormalizer.geoidUndulationMeters(0.0, 0.0)
        assertTrue(n >= -15.0 && n <= 15.0)
    }
}
