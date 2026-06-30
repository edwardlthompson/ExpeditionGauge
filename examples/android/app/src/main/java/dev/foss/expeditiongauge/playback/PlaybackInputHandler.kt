package dev.foss.expeditiongauge.playback

import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.type

enum class PlaybackInputAction {
    SeekBack,
    SeekForward,
    TogglePlayPause,
    SpeedDown,
    SpeedUp,
    None,
}

object PlaybackInputHandler {
    fun actionFromKeyEvent(keyEvent: androidx.compose.ui.input.key.KeyEvent): PlaybackInputAction {
        if (keyEvent.type != KeyEventType.KeyDown) return PlaybackInputAction.None
        return when (keyEvent.key) {
            Key.DirectionLeft, Key.MediaPrevious -> PlaybackInputAction.SeekBack
            Key.DirectionRight, Key.MediaNext -> PlaybackInputAction.SeekForward
            Key.Spacebar, Key.MediaPlayPause -> PlaybackInputAction.TogglePlayPause
            Key.LeftBracket -> PlaybackInputAction.SpeedDown
            Key.RightBracket -> PlaybackInputAction.SpeedUp
            else -> PlaybackInputAction.None
        }
    }

    fun apply(action: PlaybackInputAction, engine: PlaybackEngine) {
        when (action) {
            PlaybackInputAction.SeekBack -> engine.seekBy(-1_000L)
            PlaybackInputAction.SeekForward -> engine.seekBy(1_000L)
            PlaybackInputAction.TogglePlayPause -> engine.togglePlayPause()
            PlaybackInputAction.SpeedDown -> engine.adjustSpeed(-0.25f)
            PlaybackInputAction.SpeedUp -> engine.adjustSpeed(0.25f)
            PlaybackInputAction.None -> Unit
        }
    }
}
