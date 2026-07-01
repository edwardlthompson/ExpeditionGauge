package dev.foss.expeditiongauge.playback

import dev.foss.expeditiongauge.data.db.entities.SampleEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class PlaybackEngine {
    private val _state = MutableStateFlow(PlaybackState())
    val state: StateFlow<PlaybackState> = _state.asStateFlow()
    private val seekController = PlaybackSeekController(
        getState = { _state.value },
        setState = { _state.value = it },
    )

    fun loadSession(sessionId: Long?, samples: List<SampleEntity>, markers: List<ScrubberMarker> = emptyList()) {
        _state.value = PlaybackState(
            sessionId = sessionId,
            samples = samples,
            currentIndex = 0,
            markers = markers,
        )
    }

    fun loadSamples(samples: List<SampleEntity>, markers: List<ScrubberMarker> = emptyList()) {
        loadSession(null, samples, markers)
    }

    fun loadGhost(samples: List<SampleEntity>, lapNumber: Int? = null) {
        _state.value = _state.value.copy(
            ghostSamples = samples,
            ghostLapNumber = lapNumber,
            ghostTrackMismatch = false,
        )
        seekToIndex(_state.value.currentIndex)
    }

    fun clearGhost() {
        _state.value = _state.value.copy(
            ghostSamples = emptyList(),
            ghostDeltaMs = null,
            ghostLapNumber = null,
            ghostTrackMismatch = false,
        )
    }

    fun setGhostTrackMismatch(mismatch: Boolean) {
        _state.value = _state.value.copy(
            ghostTrackMismatch = mismatch,
            ghostSamples = if (mismatch) emptyList() else _state.value.ghostSamples,
            ghostDeltaMs = if (mismatch) null else _state.value.ghostDeltaMs,
        )
    }

    fun setSectorLinesGeoJson(geoJson: String?) {
        _state.value = _state.value.copy(sectorLinesGeoJson = geoJson)
    }

    fun toggleShowRoute() {
        _state.value = _state.value.copy(showRoute = !_state.value.showRoute)
    }

    fun toggleDrivingLine() {
        _state.value = _state.value.copy(showDrivingLine = !_state.value.showDrivingLine)
    }

    fun toggleGhost() {
        _state.value = _state.value.copy(showGhost = !_state.value.showGhost)
    }

    fun setShowGhost(enabled: Boolean) {
        _state.value = _state.value.copy(showGhost = enabled)
    }

    fun seekToIndex(index: Int) = seekController.seekToIndex(index)

    fun seekToTimestamp(timestampMs: Long) = seekController.seekToTimestamp(timestampMs)

    fun seekProgress(progress: Float) = seekController.seekProgress(progress)

    fun seekBy(deltaMs: Long) = seekController.seekBy(deltaMs)

    fun setSpeedMultiplier(multiplier: Float) {
        _state.value = _state.value.copy(speedMultiplier = multiplier.coerceIn(0.25f, 4f))
    }

    fun adjustSpeed(delta: Float) {
        setSpeedMultiplier(_state.value.speedMultiplier + delta)
    }

    fun setPlaying(playing: Boolean) {
        _state.value = _state.value.copy(isPlaying = playing)
    }

    fun play() = setPlaying(true)
    fun pause() = setPlaying(false)

    fun togglePlayPause() {
        setPlaying(!_state.value.isPlaying)
    }

    fun toggleDriftAnalysis() {
        _state.value = _state.value.copy(showDriftAnalysis = !_state.value.showDriftAnalysis)
    }

    fun setMapWeight(weight: Float) {
        _state.value = _state.value.copy(mapWeight = weight.coerceIn(0.2f, 0.8f))
    }

    fun setGraphsExpanded(expanded: Boolean) {
        _state.value = _state.value.copy(graphsExpanded = expanded)
    }

    fun applyLayout(layout: PlaybackLayoutState) {
        _state.value = _state.value.copy(
            mapWeight = layout.mapWeight.coerceIn(0.2f, 0.8f),
            graphsExpanded = layout.graphsExpanded,
        )
    }

    fun advanceFrame(deltaMs: Long = 50L) = seekController.advanceFrame(deltaMs)

    fun driftColorForBeta(beta: Float?): Long = DriftRouteStyling.betaToArgb(beta)

    companion object {
        fun computeMarkers(
            samples: List<SampleEntity>,
            alertTimestamps: List<Long> = emptyList(),
            markEventTimestamps: List<Long> = emptyList(),
            mediaAttachments: List<MediaAttachmentMarker> = emptyList(),
            betaThreshold: Float = 15f,
            slipThreshold: Float = 0.15f,
        ): List<ScrubberMarker> = ScrubberMarkerFactory.computeMarkers(
            samples,
            alertTimestamps,
            markEventTimestamps,
            mediaAttachments,
            betaThreshold,
            slipThreshold,
        )
    }
}
