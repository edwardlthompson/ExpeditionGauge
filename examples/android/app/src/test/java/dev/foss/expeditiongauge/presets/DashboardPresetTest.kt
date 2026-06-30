package dev.foss.expeditiongauge.presets

import dev.foss.expeditiongauge.recording.RecordingMode
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class DashboardPresetTest {
    @Test
    fun offroadLinksToCrawlingMode() {
        val preset = DashboardPreset.fromId(DashboardPresetId.Offroad)
        assertEquals(RecordingMode.CRAWLING, preset.recordingMode)
        assertTrue(preset.weights.attitude > preset.weights.center)
    }

    @Test
    fun minimalHidesAttitudePanel() {
        val preset = DashboardPreset.Minimal
        assertFalse(preset.showAttitude)
        assertFalse(preset.showGps)
    }

    @Test
    fun driftEmphasizesBeta() {
        val preset = DashboardPreset.Drift
        assertTrue(preset.emphasizeDrift)
        assertTrue(preset.showDriftAngle)
    }
}
