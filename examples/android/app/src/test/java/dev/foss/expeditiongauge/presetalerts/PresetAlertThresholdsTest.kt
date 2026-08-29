package dev.foss.expeditiongauge.presetalerts

import dev.foss.expeditiongauge.alerts.AlertThresholds
import dev.foss.expeditiongauge.presets.DashboardPresetId
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class PresetAlertThresholdsTest {
    @Test
    fun userValuesWinAndPresetFillsEmpty() {
        val global = AlertThresholds(masterEnabled = true, maxLatG = 0.5f)
        val track = PresetAlertThresholds.resolve(DashboardPresetId.Track, global)
        assertEquals(0.5f, track.maxLatG)
        assertEquals(7000f, track.maxRpm)
        assertEquals(true, track.masterEnabled)
        val emptyDefault = PresetAlertThresholds.resolve(DashboardPresetId.Default, AlertThresholds())
        assertNull(emptyDefault.maxRpm)
    }
}
