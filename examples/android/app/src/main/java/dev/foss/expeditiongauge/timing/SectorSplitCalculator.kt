package dev.foss.expeditiongauge.timing

import dev.foss.expeditiongauge.data.db.entities.LapEntity
import dev.foss.expeditiongauge.data.db.entities.SampleEntity
import dev.foss.expeditiongauge.data.db.entities.SectorSplitEntity

data class SectorTimingResult(
    val splits: List<SectorSplitEntity>,
    val sectorDurationsMs: List<Long>,
)

class SectorSplitCalculator(
    private val sectorLines: List<LineSegment>,
) {
    fun computeForLap(
        lap: LapEntity,
        samples: List<SampleEntity>,
    ): SectorTimingResult {
        if (sectorLines.isEmpty() || samples.isEmpty()) {
            return SectorTimingResult(emptyList(), emptyList())
        }
        val lapSamples = samples.filter { it.id in lap.startSampleId..lap.endSampleId }
        if (lapSamples.size < 2) return SectorTimingResult(emptyList(), emptyList())

        val splits = mutableListOf<SectorSplitEntity>()
        val durations = mutableListOf<Long>()
        var sectorStartMs = lapSamples.first().timestampMs
        var sectorIndex = 0

        for (i in 1 until lapSamples.size) {
            val prev = lapSamples[i - 1]
            val curr = lapSamples[i]
            val line = sectorLines.getOrNull(sectorIndex) ?: break
            if (line.crosses(prev, curr)) {
                val splitMs = curr.timestampMs - sectorStartMs
                durations += splitMs
                splits += SectorSplitEntity(
                    lapId = lap.id,
                    sectorIndex = sectorIndex,
                    splitMs = splitMs,
                    sampleId = curr.id,
                )
                sectorStartMs = curr.timestampMs
                sectorIndex++
            }
        }
        return SectorTimingResult(splits, durations)
    }

    fun theoreticalBest(sectorBestMs: List<Long>): Long = sectorBestMs.sum()
}

fun parseSectorLinesFromGeoJson(geoJson: String?): List<LineSegment> {
    if (geoJson.isNullOrBlank()) return emptyList()
    val segments = mutableListOf<LineSegment>()
    val lineRegex = """\[\[(-?\d+\.?\d*),\s*(-?\d+\.?\d*)\],\s*\[(-?\d+\.?\d*),\s*(-?\d+\.?\d*)\]\]"""
        .toRegex()
    lineRegex.findAll(geoJson).forEach { match ->
        val (lon1, lat1, lon2, lat2) = match.destructured
        segments += LineSegment(lat1.toDouble(), lon1.toDouble(), lat2.toDouble(), lon2.toDouble())
    }
    return segments.take(9)
}

fun bestSectorTimes(allSplits: List<SectorSplitEntity>): List<Long> {
    return allSplits
        .groupBy { it.sectorIndex }
        .toSortedMap()
        .values
        .map { splits -> splits.minOf { it.splitMs } }
}
