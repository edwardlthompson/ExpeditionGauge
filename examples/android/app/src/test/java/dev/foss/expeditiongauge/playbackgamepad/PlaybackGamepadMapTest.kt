package dev.foss.expeditiongauge.playbackgamepad

import androidx.compose.ui.input.key.Key
import dev.foss.expeditiongauge.playback.PlaybackInputAction
import org.junit.Assert.assertEquals
import org.junit.Test

class PlaybackGamepadMapTest {
    @Test
    fun mapsFaceAndShoulderButtons() {
        assertEquals(PlaybackInputAction.TogglePlayPause, PlaybackGamepadMap.fromKey(Key.ButtonA))
        assertEquals(PlaybackInputAction.SeekBack, PlaybackGamepadMap.fromKey(Key.ButtonL1))
        assertEquals(PlaybackInputAction.SeekForward, PlaybackGamepadMap.fromKey(Key.ButtonR1))
        assertEquals(PlaybackInputAction.SpeedDown, PlaybackGamepadMap.fromKey(Key.Minus))
        assertEquals(PlaybackInputAction.None, PlaybackGamepadMap.fromKey(Key.A))
    }
}
