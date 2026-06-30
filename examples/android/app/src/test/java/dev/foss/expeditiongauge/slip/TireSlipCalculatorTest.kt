package dev.foss.expeditiongauge.slip

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class TireSlipCalculatorTest {
    @Test
    fun computesSlipRatioDistinctFromDrift() {
        val calc = TireSlipCalculator()
        val sample = calc.compute(gpsSpeedMps = 10f, wheelSpeedMps = 11f)
        assertNotNull(sample.slipRatio)
        assertEquals(0.1f, sample.slipRatio!!, 0.01f)
    }

    @Test
    fun suppressesBelowSpeedThreshold() {
        val calc = TireSlipCalculator()
        val sample = calc.compute(gpsSpeedMps = 0.5f, wheelSpeedMps = 1f)
        assertEquals(null, sample.slipRatio)
    }

    @Test
    fun computesRearAxleSlipWhenWheelPidsAvailable() {
        val calc = TireSlipCalculator()
        val sample = calc.compute(
            gpsSpeedMps = 10f,
            wheelSpeedMps = 10f,
            rearLeftMps = 11f,
            rearRightMps = 11f,
        )
        assertEquals(0f, sample.slipRatio!!, 0.01f)
        assertEquals(0.1f, sample.rearSlipRatio!!, 0.01f)
        assertEquals("rear_axle", sample.source)
    }
}
