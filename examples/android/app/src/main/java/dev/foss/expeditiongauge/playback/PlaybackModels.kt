package dev.foss.expeditiongauge.playback

import dev.foss.expeditiongauge.data.db.entities.SampleEntity

enum class ScrubberMarkerType {
    HIGH_BETA,
    HIGH_SLIP,
    ALERT,
    LAP_CROSSING,
    MARK_EVENT,
}

data class ScrubberMarker(
    val sampleIndex: Int,
    val timestampMs: Long,
    val type: ScrubberMarkerType,
    val label: String? = null,
)

data class PlaybackState(
    val sessionId: Long? = null,
    val samples: List<SampleEntity> = emptyList(),
    val currentIndex: Int = 0,
    val speedMultiplier: Float = 1f,
    val isPlaying: Boolean = false,
    val markers: List<ScrubberMarker> = emptyList(),
    val ghostSamples: List<SampleEntity> = emptyList(),
    val ghostDeltaMs: Long? = null,
    val ghostLapNumber: Int? = null,
    val ghostTrackMismatch: Boolean = false,
    val showRoute: Boolean = true,
    val showDrivingLine: Boolean = false,
    val showGhost: Boolean = false,
    val sectorLinesGeoJson: String? = null,
    val showDriftAnalysis: Boolean = false,
    val mapWeight: Float = 0.6f,
    val graphsExpanded: Boolean = true,
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

object ScrubberMarkerFactory {
    fun computeMarkers(
        samples: List<SampleEntity>,
        alertTimestamps: List<Long> = emptyList(),
        markEventTimestamps: List<Long> = emptyList(),
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
        markEventTimestamps.forEach { ts ->
            val index = samples.indexOfFirst { it.timestampMs >= ts }
            if (index >= 0) {
                markers += ScrubberMarker(index, ts, ScrubberMarkerType.MARK_EVENT, label = "Mark")
            }
        }
        return markers.distinctBy { it.sampleIndex to it.type }
    }
}
