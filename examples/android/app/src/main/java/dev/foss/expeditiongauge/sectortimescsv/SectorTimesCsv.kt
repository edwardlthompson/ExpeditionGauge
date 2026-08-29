package dev.foss.expeditiongauge.sectortimescsv

import dev.foss.expeditiongauge.data.db.entities.SectorSplitEntity

/** CSV export of sector split times. */
object SectorTimesCsv {
    const val HEADER = "lapId,sectorIndex,splitMs,sampleId"

    fun row(split: SectorSplitEntity): String =
        "${split.lapId},${split.sectorIndex},${split.splitMs},${split.sampleId}"

    fun export(splits: List<SectorSplitEntity>): String =
        (listOf(HEADER) + splits.map(::row)).joinToString("\n")
}
