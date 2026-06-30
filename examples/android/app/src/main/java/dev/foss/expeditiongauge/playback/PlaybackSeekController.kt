package dev.foss.expeditiongauge.playback

import dev.foss.expeditiongauge.data.db.entities.SampleEntity
import dev.foss.expeditiongauge.ghost.GhostLapOverlay

internal class PlaybackSeekController(
    private val getState: () -> PlaybackState,
    private val setState: (PlaybackState) -> Unit,
) {
    fun seekToIndex(index: Int) {
        val clamped = index.coerceIn(0, (getState().samples.size - 1).coerceAtLeast(0))
        setState(getState().copy(currentIndex = clamped))
        updateGhostDelta()
    }

    fun seekToTimestamp(timestampMs: Long) {
        val index = getState().samples.indexOfFirst { it.timestampMs >= timestampMs }
        seekToIndex(if (index < 0) getState().samples.lastIndex else index)
    }

    fun seekProgress(progress: Float) {
        val size = getState().samples.size
        if (size <= 1) return
        seekToIndex((progress.coerceIn(0f, 1f) * (size - 1)).toInt())
    }

    fun seekBy(deltaMs: Long) {
        val current = getState().currentSample ?: return
        seekToTimestamp(current.timestampMs + deltaMs)
    }

    fun advanceFrame(deltaMs: Long = 50L) {
        val state = getState()
        if (!state.isPlaying || state.samples.isEmpty()) return
        val current = state.currentSample ?: return
        val targetTs = current.timestampMs + (deltaMs * state.speedMultiplier).toLong()
        seekToTimestamp(targetTs)
    }

    private fun updateGhostDelta() {
        val state = getState()
        if (state.ghostSamples.isEmpty() || state.ghostTrackMismatch) {
            setState(state.copy(ghostDeltaMs = null))
            return
        }
        val delta = GhostLapOverlay().computeDeltaByDistance(
            state.samples,
            state.ghostSamples,
            state.currentIndex,
        )
        setState(state.copy(ghostDeltaMs = delta))
    }
}
