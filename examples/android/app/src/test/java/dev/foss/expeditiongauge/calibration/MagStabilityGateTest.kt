package dev.foss.expeditiongauge.calibration

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class MagStabilityGateTest {
    @Test
    fun stableField_passes() {
        val gate = MagStabilityGate(windowSize = 8, maxVariance = 80f)
        repeat(8) { gate.onSample(25f, 5f, 40f) }
        assertTrue(gate.isStable())
    }

    @Test
    fun highVariance_rejects() {
        val gate = MagStabilityGate(windowSize = 8, maxVariance = 5f)
        repeat(4) { gate.onSample(20f, 0f, 40f) }
        repeat(4) { gate.onSample(80f, 50f, 10f) }
        assertFalse(gate.isStable())
    }

    @Test
    fun tooFewSamples_rejects() {
        val gate = MagStabilityGate(windowSize = 12)
        gate.onSample(25f, 5f, 40f)
        assertFalse(gate.isStable())
    }
}
