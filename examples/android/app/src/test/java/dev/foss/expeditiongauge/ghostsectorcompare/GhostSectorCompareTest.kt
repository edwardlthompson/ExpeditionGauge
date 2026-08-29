package dev.foss.expeditiongauge.ghostsectorcompare

import dev.foss.expeditiongauge.data.db.entities.SectorSplitEntity
import org.junit.Assert.assertEquals
import org.junit.Test

class GhostSectorCompareTest {
    @Test
    fun alignsAndNetsSectorDeltas() {
        val primary = listOf(
            SectorSplitEntity(lapId = 1, sectorIndex = 0, splitMs = 1200, sampleId = 1),
            SectorSplitEntity(lapId = 1, sectorIndex = 1, splitMs = 900, sampleId = 2),
        )
        val ghost = listOf(
            SectorSplitEntity(lapId = 2, sectorIndex = 0, splitMs = 1100, sampleId = 3),
            SectorSplitEntity(lapId = 2, sectorIndex = 2, splitMs = 800, sampleId = 4),
        )
        val rows = GhostSectorCompare.rows(primary, ghost)
        assertEquals(1, rows.size)
        assertEquals(100L, rows[0].deltaMs)
        assertEquals(100L, GhostSectorCompare.netDeltaMs(rows))
        assertEquals(0, GhostSectorCompare.fastestSectorCount(rows))
    }
}
