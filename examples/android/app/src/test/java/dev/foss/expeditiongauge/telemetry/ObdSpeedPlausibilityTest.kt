package dev.foss.expeditiongauge.telemetry

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ObdSpeedPlausibilityTest {
    @Test
    fun rejectsStationaryGpsWithHighwayObd() {
        // 147 km/h while GPS ~0 — classic mis-parse spike
        assertFalse(ObdSpeedPlausibility.isPlausible(147f, 0f))
        assertEquals(0f, ObdSpeedPlausibility.resolveMps(147f, 0f, 0f), 0.01f)
    }

    @Test
    fun acceptsAgreeingSpeeds() {
        assertTrue(ObdSpeedPlausibility.isPlausible(72f, 20f)) // ~20 m/s
        assertEquals(20f, ObdSpeedPlausibility.resolveMps(72f, 19f, 0f), 0.5f)
    }
}
