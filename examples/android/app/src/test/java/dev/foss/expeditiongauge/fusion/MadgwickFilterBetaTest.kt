package dev.foss.expeditiongauge.fusion

import org.junit.Assert.assertNotEquals
import org.junit.Test

class MadgwickFilterBetaTest {
    @Test
    fun differentBetaProducesDifferentAttitudeAfterUpdates() {
        val high = MadgwickFilter(beta = 0.4f)
        val low = MadgwickFilter(beta = 0.05f)
        repeat(40) {
            high.update(0.12f, 0.02f, 0f, 0f, 0f, 9.81f)
            low.update(0.12f, 0.02f, 0f, 0f, 0f, 9.81f)
        }
        assertNotEquals(high.pitchDeg(), low.pitchDeg())
    }
}
