package dev.foss.expeditiongauge.playbackgamepad

import androidx.compose.ui.input.key.Key
import dev.foss.expeditiongauge.playback.PlaybackInputAction

/** Map gamepad / extra keys onto Relive transport actions. */
object PlaybackGamepadMap {
    fun fromKey(key: Key): PlaybackInputAction = when (key) {
        Key.ButtonA, Key.ButtonStart -> PlaybackInputAction.TogglePlayPause
        Key.ButtonL1, Key.ButtonThumbLeft -> PlaybackInputAction.SeekBack
        Key.ButtonR1, Key.ButtonThumbRight -> PlaybackInputAction.SeekForward
        Key.Minus, Key.NumPadSubtract -> PlaybackInputAction.SpeedDown
        Key.Plus, Key.NumPadAdd -> PlaybackInputAction.SpeedUp
        else -> PlaybackInputAction.None
    }
}
