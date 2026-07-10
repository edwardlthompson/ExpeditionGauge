package dev.foss.expeditiongauge.gauge

import org.junit.Assert.assertEquals
import org.junit.Test

class AttitudeGaugeModeToggleTest {
    @Test
    fun inclinometerTogglesToAttitude() {
        assertEquals(AttitudeGaugeMode.ATTITUDE, AttitudeGaugeMode.INCLINOMETER.toggleGMeterInclinometer())
    }

    @Test
    fun attitudeTogglesToInclinometer() {
        assertEquals(AttitudeGaugeMode.INCLINOMETER, AttitudeGaugeMode.ATTITUDE.toggleGMeterInclinometer())
    }

    @Test
    fun gForceAndHybridToggleToInclinometer() {
        assertEquals(AttitudeGaugeMode.INCLINOMETER, AttitudeGaugeMode.G_FORCE.toggleGMeterInclinometer())
        assertEquals(AttitudeGaugeMode.INCLINOMETER, AttitudeGaugeMode.HYBRID.toggleGMeterInclinometer())
    }
}
