package dev.foss.expeditiongauge.playback

import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [26])
class PlaybackInputHandlerTest {
    @Test
    fun seekForwardAdvancesIndex() {
        val engine = PlaybackEngine()
        val samples = (0..9).map { i ->
            dev.foss.expeditiongauge.data.db.entities.SampleEntity(
                id = i.toLong(),
                sessionId = 1L,
                timestampMs = i * 1_000L,
            )
        }
        engine.loadSession(1L, samples)
        assertEquals(0, engine.state.value.currentIndex)
        PlaybackInputHandler.apply(PlaybackInputAction.SeekForward, engine)
        assertEquals(1, engine.state.value.currentIndex)
    }

    @Test
    fun seekBackClampsAtStart() {
        val engine = PlaybackEngine()
        val samples = listOf(
            dev.foss.expeditiongauge.data.db.entities.SampleEntity(id = 1, sessionId = 1L, timestampMs = 0),
            dev.foss.expeditiongauge.data.db.entities.SampleEntity(id = 2, sessionId = 1L, timestampMs = 1_000),
        )
        engine.loadSession(1L, samples)
        PlaybackInputHandler.apply(PlaybackInputAction.SeekBack, engine)
        assertEquals(0, engine.state.value.currentIndex)
    }

    @Test
    fun togglePlayPauseFlipsState() {
        val engine = PlaybackEngine()
        PlaybackInputHandler.apply(PlaybackInputAction.TogglePlayPause, engine)
        assertEquals(true, engine.state.value.isPlaying)
        PlaybackInputHandler.apply(PlaybackInputAction.TogglePlayPause, engine)
        assertEquals(false, engine.state.value.isPlaying)
    }

    @Test
    fun speedKeysAdjustMultiplier() {
        val engine = PlaybackEngine()
        PlaybackInputHandler.apply(PlaybackInputAction.SpeedUp, engine)
        assertEquals(1.25f, engine.state.value.speedMultiplier)
        PlaybackInputHandler.apply(PlaybackInputAction.SpeedDown, engine)
        assertEquals(1f, engine.state.value.speedMultiplier)
    }
}
