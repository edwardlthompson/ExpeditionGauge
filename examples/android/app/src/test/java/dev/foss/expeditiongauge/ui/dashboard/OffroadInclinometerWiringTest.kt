package dev.foss.expeditiongauge.ui.dashboard

import dev.foss.expeditiongauge.gauge.AttitudeGaugeMode
import dev.foss.expeditiongauge.presets.DashboardPresetId
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class OffroadInclinometerWiringTest {
    @Test
    fun offroadPreset_selectsInclinometerMode() {
        assertEquals(AttitudeGaugeMode.INCLINOMETER, gaugeModeForPreset(DashboardPresetId.Offroad))
    }

    @Test
    fun driftPreset_doesNotChangeGaugeMode() {
        assertNull(gaugeModeForPreset(DashboardPresetId.Drift))
    }
}
