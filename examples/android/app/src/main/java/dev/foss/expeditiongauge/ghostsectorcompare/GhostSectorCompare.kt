package dev.foss.expeditiongauge.ghostsectorcompare

import dev.foss.expeditiongauge.data.db.entities.SectorSplitEntity
import dev.foss.expeditiongauge.ghost.GhostLapComparer
import dev.foss.expeditiongauge.ghost.SectorDeltaRow

/** Compare primary vs ghost sector splits by index. */
object GhostSectorCompare {
    fun rows(
        primarySplits: List<SectorSplitEntity>,
        ghostSplits: List<SectorSplitEntity>,
    ): List<SectorDeltaRow> = GhostLapComparer.sectorDeltas(primarySplits, ghostSplits)

    fun netDeltaMs(rows: List<SectorDeltaRow>): Long = rows.sumOf { it.deltaMs }

    fun fastestSectorCount(rows: List<SectorDeltaRow>): Int = rows.count { it.deltaMs < 0 }
}
