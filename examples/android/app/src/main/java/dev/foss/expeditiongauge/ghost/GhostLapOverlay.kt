package dev.foss.expeditiongauge.ghost

import dev.foss.expeditiongauge.data.db.entities.LapEntity
import dev.foss.expeditiongauge.data.db.entities.SampleEntity
import dev.foss.expeditiongauge.timing.haversineM
import dev.foss.expeditiongauge.timing.parseStartFinishFromGeoJson

data class GhostLapState(
    val primarySamples: List<SampleEntity>,
    val ghostSamples: List<SampleEntity>,
    val deltaMsAtIndex: Long? = null,
    val trackMismatch: Boolean = false,
    val ghostLapNumber: Int? = null,
)

class GhostLapOverlay {
    fun buildState(
        primary: List<SampleEntity>,
        ghost: List<SampleEntity>,
        primaryStartFinishGeoJson: String?,
        ghostStartFinishGeoJson: String?,
        ghostLap: LapEntity? = null,
        currentIndex: Int = 0,
    ): GhostLapState {
        val mismatch = detectTrackMismatch(primaryStartFinishGeoJson, ghostStartFinishGeoJson)
        val delta = if (!mismatch && primary.isNotEmpty() && ghost.isNotEmpty()) {
            computeDeltaAtIndex(primary, ghost, currentIndex)
        } else {
            null
        }
        return GhostLapState(
            primarySamples = primary,
            ghostSamples = if (mismatch) emptyList() else ghost,
            deltaMsAtIndex = delta,
            trackMismatch = mismatch,
            ghostLapNumber = ghostLap?.lapNumber,
        )
    }

    fun samplesForLap(allSamples: List<SampleEntity>, lap: LapEntity): List<SampleEntity> =
        allSamples.filter { it.id in lap.startSampleId..lap.endSampleId }

    fun formatDelta(deltaMs: Long?): String {
        if (deltaMs == null) return "--"
        val sign = if (deltaMs >= 0) "+" else "-"
        val abs = kotlin.math.abs(deltaMs)
        return "$sign${abs / 1000}.${(abs % 1000) / 100}s"
    }

    private fun detectTrackMismatch(primaryGeo: String?, ghostGeo: String?): Boolean {
        if (primaryGeo.isNullOrBlank() || ghostGeo.isNullOrBlank()) return false
        val p = parseStartFinishFromGeoJson(primaryGeo) ?: return false
        val g = parseStartFinishFromGeoJson(ghostGeo) ?: return false
        val dist = haversineM(p.startLat, p.startLon, g.startLat, g.startLon)
        return dist > 50.0
    }

    private fun computeDeltaAtIndex(
        primary: List<SampleEntity>,
        ghost: List<SampleEntity>,
        index: Int,
    ): Long? {
        val p = primary.getOrNull(index) ?: return null
        val ghostIndex = ghost.indexOfFirst { it.timestampMs >= p.timestampMs }
        val g = ghost.getOrNull(ghostIndex) ?: ghost.lastOrNull() ?: return null
        return p.timestampMs - g.timestampMs
    }
}
