package dev.foss.expeditiongauge.timing

import dev.foss.expeditiongauge.data.db.entities.LapEntity
import dev.foss.expeditiongauge.data.db.entities.SampleEntity
import dev.foss.expeditiongauge.data.db.entities.SectorSplitEntity

data class PredictiveTimingState(
    val currentSectorIndex: Int = 0,
    val elapsedSectorMs: Long = 0L,
    val bestSectorMs: Long? = null,
    val deltaMs: Long? = null,
    val sessionBestLapMs: Long? = null,
    val currentLapElapsedMs: Long = 0L,
)

class PredictiveTimingEngine(
    private val sectorLines: List<LineSegment> = emptyList(),
) {
    private var sectorStartMs: Long = 0L
    private var currentSector: Int = 0
    private var lapStartMs: Long = 0L

    fun reset(startMs: Long) {
        sectorStartMs = startMs
        lapStartMs = startMs
        currentSector = 0
    }

    fun update(
        sample: SampleEntity,
        prevSample: SampleEntity?,
        bestSplits: List<Long>,
        sessionBestLapMs: Long?,
    ): PredictiveTimingState {
        if (prevSample != null && sectorLines.isNotEmpty()) {
            val line = sectorLines.getOrNull(currentSector)
            if (line != null && line.crosses(prevSample, sample)) {
                currentSector++
                sectorStartMs = sample.timestampMs
            }
        }
        val elapsedSector = sample.timestampMs - sectorStartMs
        val best = bestSplits.getOrNull(currentSector)
        val delta = best?.let { elapsedSector - it }
        return PredictiveTimingState(
            currentSectorIndex = currentSector,
            elapsedSectorMs = elapsedSector,
            bestSectorMs = best,
            deltaMs = delta,
            sessionBestLapMs = sessionBestLapMs,
            currentLapElapsedMs = sample.timestampMs - lapStartMs,
        )
    }

    fun formatDelta(deltaMs: Long?): String {
        if (deltaMs == null) return "--"
        val absMs = kotlin.math.abs(deltaMs)
        val sign = when {
            deltaMs > 0 -> "+"
            deltaMs < 0 -> "-"
            else -> ""
        }
        return "$sign${absMs / 1000}.${(absMs % 1000) / 100}s"
    }

    fun formatLapTime(ms: Long): String {
        val minutes = ms / 60_000
        val seconds = (ms % 60_000) / 1000
        val millis = (ms % 1000) / 10
        return if (minutes > 0) {
            "%d:%02d.%02d".format(minutes, seconds, millis)
        } else {
            "%d.%02d".format(seconds, millis)
        }
    }

    companion object {
        fun theoreticalBestFromLaps(
            laps: List<LapEntity>,
            allSplits: List<SectorSplitEntity>,
        ): Long {
            val validLaps = laps.filter { it.isValid && !it.isOutLap }
            if (validLaps.isEmpty()) return 0L
            val bestBySector = bestSectorTimes(allSplits)
            return bestBySector.sum()
        }
    }
}
