package dev.foss.expeditiongauge.ghost

import dev.foss.expeditiongauge.data.db.entities.LapEntity
import dev.foss.expeditiongauge.data.db.entities.SampleEntity
import dev.foss.expeditiongauge.timing.haversineM
import dev.foss.expeditiongauge.timing.parseStartFinishFromGeoJson
import kotlin.math.abs

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
            computeDeltaByDistance(primary, ghost, currentIndex)
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

    fun samplesForLap(allSamples: List<SampleEntity>, lap: LapEntity): List<SampleEntity> {
        val startIdx = allSamples.indexOfFirst { it.id == lap.startSampleId }
        val endIdx = allSamples.indexOfFirst { it.id == lap.endSampleId }
        if (startIdx < 0 || endIdx < 0 || endIdx < startIdx) return emptyList()
        return allSamples.subList(startIdx, endIdx + 1)
    }

    fun formatDelta(deltaMs: Long?): String {
        if (deltaMs == null) return "--"
        val sign = if (deltaMs >= 0) "+" else "-"
        val absMs = abs(deltaMs)
        return "$sign${absMs / 1000}.${(absMs % 1000) / 100}s"
    }

    fun computeDeltaByDistance(
        primary: List<SampleEntity>,
        ghost: List<SampleEntity>,
        primaryIndex: Int,
    ): Long? {
        val p = primary.getOrNull(primaryIndex) ?: return null
        val targetDistance = cumulativeDistanceM(primary, primaryIndex)
        val ghostIndex = nearestDistanceIndex(ghost, targetDistance)
        val g = ghost.getOrNull(ghostIndex) ?: return null
        return p.timestampMs - g.timestampMs
    }

    private fun detectTrackMismatch(primaryGeo: String?, ghostGeo: String?): Boolean {
        if (primaryGeo.isNullOrBlank() || ghostGeo.isNullOrBlank()) return false
        val p = parseStartFinishFromGeoJson(primaryGeo) ?: return false
        val g = parseStartFinishFromGeoJson(ghostGeo) ?: return false
        val dist = haversineM(p.startLat, p.startLon, g.startLat, g.startLon)
        return dist > 50.0
    }

    private fun cumulativeDistanceM(samples: List<SampleEntity>, endIndex: Int): Double {
        if (endIndex <= 0) return 0.0
        var total = 0.0
        for (i in 1..endIndex.coerceAtMost(samples.lastIndex)) {
            val prev = samples[i - 1]
            val curr = samples[i]
            val lat1 = prev.latitude ?: continue
            val lon1 = prev.longitude ?: continue
            val lat2 = curr.latitude ?: continue
            val lon2 = curr.longitude ?: continue
            total += haversineM(lat1, lon1, lat2, lon2)
        }
        return total
    }

    private fun nearestDistanceIndex(samples: List<SampleEntity>, targetM: Double): Int {
        if (samples.isEmpty()) return 0
        var bestIndex = 0
        var bestDelta = Double.MAX_VALUE
        for (i in samples.indices) {
            val distance = cumulativeDistanceM(samples, i)
            val delta = abs(distance - targetM)
            if (delta < bestDelta) {
                bestDelta = delta
                bestIndex = i
            }
        }
        return bestIndex
    }
}
