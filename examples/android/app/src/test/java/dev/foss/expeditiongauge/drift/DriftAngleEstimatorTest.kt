package dev.foss.expeditiongauge.drift

import org.junit.Assert.assertEquals
import org.junit.Test

class DriftAngleEstimatorTest {
    @Test
    fun reportsBetaAfterGpsUpdate() {
        val estimator = DriftAngleEstimator()
        estimator.onFusionSample(yawDeg = 100f, yawRateDegPerSec = 0f, timestampMs = 1000L)
        estimator.onGpsSample(velocityHeadingDeg = 80f, speedMps = 10f)
        val sample = estimator.currentSample()
        assertEquals(20f, sample.driftAngleDeg, 0.001f)
    }
}
