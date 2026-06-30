package dev.foss.expeditiongauge.playback

import androidx.compose.ui.graphics.Color
import dev.foss.expeditiongauge.data.db.entities.SampleEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class ScrubberMarkerType {
    HIGH_BETA,
    HIGH_SLIP,
    ALERT,
    LAP_CROSSING,
}

data class ScrubberMarker(
    val sampleIndex: Int,
    val timestampMs: Long,
    val type: ScrubberMarkerType,
    val label: String? = null,
)

data class PlaybackState(
    val samples: List<SampleEntity> = emptyList(),
    val currentIndex: Int = 0,
    val speedMultiplier: Float = 1f,
    val isPlaying: Boolean = false,
    val markers: List<ScrubberMarker> = emptyList(),
    val ghostSamples: List<SampleEntity> = emptyList(),
    val ghostDeltaMs: Long? = null,
    val showDriftAnalysis: Boolean = false,
    val mapWeight: Float = 0.6f,
) {
    val currentSample: SampleEntity?
        get() = samples.getOrNull(currentIndex)

    val current: SampleEntity?
        get() = currentSample

    val playing: Boolean
        get() = isPlaying

    val index: Int
        get() = currentIndex

    val progress: Float
        get() = if (samples.size <= 1) 0f else currentIndex.toFloat() / (samples.size - 1)

    val durationMs: Long
        get() = if (samples.size < 2) 0L else samples.last().timestampMs - samples.first().timestampMs
}

class PlaybackEngine {
    private val _state = MutableStateFlow(PlaybackState())
    val state: StateFlow<PlaybackState> = _state.asStateFlow()

    fun loadSession(samples: List<SampleEntity>, markers: List<ScrubberMarker> = emptyList()) {
        _state.value = PlaybackState(
            samples = samples,
            currentIndex = 0,
            markers = markers,
        )
    }

    fun loadSamples(samples: List<SampleEntity>, markers: List<ScrubberMarker> = emptyList()) {
        loadSession(samples, markers)
    }

    fun loadGhost(samples: List<SampleEntity>) {
        _state.value = _state.value.copy(ghostSamples = samples)
        updateGhostDelta()
    }

    fun clearGhost() {
        _state.value = _state.value.copy(ghostSamples = emptyList(), ghostDeltaMs = null)
    }

    fun seekToIndex(index: Int) {
        val clamped = index.coerceIn(0, (_state.value.samples.size - 1).coerceAtLeast(0))
        _state.value = _state.value.copy(currentIndex = clamped)
        updateGhostDelta()
    }

    fun seekToTimestamp(timestampMs: Long) {
        val index = _state.value.samples.indexOfFirst { it.timestampMs >= timestampMs }
        seekToIndex(if (index < 0) _state.value.samples.lastIndex else index)
    }

    fun seekProgress(progress: Float) {
        val size = _state.value.samples.size
        if (size <= 1) return
        seekToIndex((progress.coerceIn(0f, 1f) * (size - 1)).toInt())
    }

    fun seekBy(deltaMs: Long) {
        val current = _state.value.currentSample ?: return
        seekToTimestamp(current.timestampMs + deltaMs)
    }

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

    fun advanceFrame(deltaMs: Long = 50L) {
        val state = _state.value
        if (!state.isPlaying || state.samples.isEmpty()) return
        val current = state.currentSample ?: return
        val targetTs = current.timestampMs + (deltaMs * state.speedMultiplier).toLong()
        seekToTimestamp(targetTs)
    }

    fun driftColorForBeta(beta: Float?): Long {
        if (beta == null) return Color.White.value.toLong()
        val abs = kotlin.math.abs(beta)
        return when {
            abs >= 30f -> Color.Red.value.toLong()
            abs >= 15f -> Color.Yellow.value.toLong()
            else -> Color.Green.value.toLong()
        }
    }

    private fun updateGhostDelta() {
        val state = _state.value
        val primary = state.currentSample ?: return
        val ghost = state.ghostSamples
        if (ghost.isEmpty()) {
            _state.value = state.copy(ghostDeltaMs = null)
            return
        }
        val ghostIndex = ghost.indexOfFirst { it.timestampMs >= primary.timestampMs }
        val ghostSample = ghost.getOrNull(ghostIndex) ?: ghost.last()
        _state.value = state.copy(ghostDeltaMs = primary.timestampMs - ghostSample.timestampMs)
    }

    companion object {
        fun computeMarkers(
            samples: List<SampleEntity>,
            alertTimestamps: List<Long> = emptyList(),
            betaThreshold: Float = 15f,
            slipThreshold: Float = 0.15f,
        ): List<ScrubberMarker> {
            val markers = mutableListOf<ScrubberMarker>()
            samples.forEachIndexed { index, sample ->
                sample.driftAngleDeg?.let { beta ->
                    if (kotlin.math.abs(beta) >= betaThreshold) {
                        markers += ScrubberMarker(index, sample.timestampMs, ScrubberMarkerType.HIGH_BETA)
                    }
                }
                sample.slipRatio?.let { slip ->
                    if (slip >= slipThreshold) {
                        markers += ScrubberMarker(index, sample.timestampMs, ScrubberMarkerType.HIGH_SLIP)
                    }
                }
            }
            alertTimestamps.forEach { ts ->
                val index = samples.indexOfFirst { it.timestampMs >= ts }
                if (index >= 0) {
                    markers += ScrubberMarker(index, ts, ScrubberMarkerType.ALERT)
                }
            }
            return markers.distinctBy { it.sampleIndex to it.type }
        }
    }
}
