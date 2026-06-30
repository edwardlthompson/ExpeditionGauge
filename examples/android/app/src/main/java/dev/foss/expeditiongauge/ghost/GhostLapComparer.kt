package dev.foss.expeditiongauge.ghost

import dev.foss.expeditiongauge.data.db.entities.LapEntity
import dev.foss.expeditiongauge.data.db.entities.SectorSplitEntity
import dev.foss.expeditiongauge.data.db.entities.SampleEntity
import dev.foss.expeditiongauge.timing.haversineM

data class SectorDeltaRow(
    val sectorIndex: Int,
    val primaryMs: Long,
    val ghostMs: Long,
    val deltaMs: Long,
)

object GhostLapComparer {
    fun sectorDeltas(
        primarySplits: List<SectorSplitEntity>,
        ghostSplits: List<SectorSplitEntity>,
    ): List<SectorDeltaRow> {
        val ghostBySector = ghostSplits.associateBy { it.sectorIndex }
        return primarySplits.mapNotNull { primary ->
            val ghost = ghostBySector[primary.sectorIndex] ?: return@mapNotNull null
            SectorDeltaRow(
                sectorIndex = primary.sectorIndex,
                primaryMs = primary.splitMs,
                ghostMs = ghost.splitMs,
                deltaMs = primary.splitMs - ghost.splitMs,
            )
        }
    }

    fun lapForSample(laps: List<LapEntity>, sample: SampleEntity?): LapEntity? {
        if (sample == null) return null
        return laps.firstOrNull { lap ->
            sample.id in lap.startSampleId..lap.endSampleId && lap.isValid && !lap.isOutLap
        }
    }
}
