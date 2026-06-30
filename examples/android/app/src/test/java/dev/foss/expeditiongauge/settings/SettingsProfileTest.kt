package dev.foss.expeditiongauge.settings

import dev.foss.expeditiongauge.presets.DashboardPresetId
import dev.foss.expeditiongauge.recording.RecordingMode
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [26])
class SettingsProfileTest {
    @Test
    fun roundTripsJson() {
        val profile = SettingsProfile(
            id = 2L,
            name = "Trail",
            presetId = DashboardPresetId.Offroad,
            recordingMode = RecordingMode.CRAWLING,
            logRateHz = 5,
        )
        val restored = SettingsProfile.fromEntity(profile.toEntity())
        assertEquals(DashboardPresetId.Offroad, restored.presetId)
        assertEquals(RecordingMode.CRAWLING, restored.recordingMode)
        assertEquals(5, restored.logRateHz)
    }

    @Test
    fun dashboardPresetReflectsRecordingModeOverride() {
        val profile = SettingsProfile(
            name = "Custom",
            presetId = DashboardPresetId.Track,
            recordingMode = RecordingMode.CRAWLING,
        )
        assertEquals(RecordingMode.CRAWLING, profile.dashboardPreset.recordingMode)
    }

    @Test
    fun roundTripsPlaybackLayout() {
        val profile = SettingsProfile(
            name = "Playback",
            playbackMapWeight = 0.3f,
            playbackGraphsExpanded = false,
        )
        val restored = SettingsProfile.fromEntity(profile.toEntity())
        assertEquals(0.3f, restored.playbackMapWeight)
        assertEquals(false, restored.playbackGraphsExpanded)
    }
}
