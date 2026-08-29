package dev.foss.expeditiongauge.playbackspeed

import org.junit.Assert.assertEquals
import org.junit.Test

class PlaybackSpeedTest {
    @Test
    fun clampsAndStepsWithinRange() {
        assertEquals(0.25f, PlaybackSpeed.clamp(0f))
        assertEquals(4f, PlaybackSpeed.clamp(8f))
        assertEquals(1.25f, PlaybackSpeed.step(1f, PlaybackSpeed.STEP))
        assertEquals(0.25f, PlaybackSpeed.step(0.25f, -PlaybackSpeed.STEP))
    }
}
