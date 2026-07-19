package dev.foss.expeditiongauge.gauge

import org.junit.Assert.assertEquals
import org.junit.Test

class AttitudeGaugeModeToggleTest {
    @Test
    fun displayCycleContainsAllModesExactlyOnce() {
        val cycle = AttitudeGaugeMode.DISPLAY_CYCLE
        assertEquals(AttitudeGaugeMode.entries.size, cycle.size)
        assertEquals(AttitudeGaugeMode.entries.toSet(), cycle.toSet())
    }

    @Test
    fun nextAttitudeDisplay_walksFullCycle() {
        var mode = AttitudeGaugeMode.INCLINOMETER_LADDER
        val seen = mutableListOf(mode)
        repeat(AttitudeGaugeMode.DISPLAY_CYCLE.size) {
            mode = mode.nextAttitudeDisplay()
            seen += mode
        }
        assertEquals(
            AttitudeGaugeMode.DISPLAY_CYCLE + AttitudeGaugeMode.INCLINOMETER_LADDER,
            seen,
        )
    }

    @Test
    fun inclinometerStylesMapRoundTrip() {
        AttitudeGaugeMode.DISPLAY_CYCLE.filter { it.isInclinometerStyle() }.forEach { mode ->
            val style = mode.toInclinometerStyle()!!
            assertEquals(mode, style.toAttitudeGaugeMode())
        }
    }

    @Test
    fun forAndroidAuto_keepsAllModes() {
        assertEquals(
            AttitudeGaugeMode.COMPASS_BALL,
            AttitudeGaugeMode.COMPASS_BALL.forAndroidAuto(),
        )
        assertEquals(
            AttitudeGaugeMode.G_FORCE,
            AttitudeGaugeMode.G_FORCE.forAndroidAuto(),
        )
    }
}
