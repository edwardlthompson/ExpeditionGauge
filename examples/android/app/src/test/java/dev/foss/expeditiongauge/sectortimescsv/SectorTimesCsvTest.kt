package dev.foss.expeditiongauge.sectortimescsv

import dev.foss.expeditiongauge.data.db.entities.SectorSplitEntity
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SectorTimesCsvTest {
    @Test
    fun exportsHeaderAndRows() {
        val csv = SectorTimesCsv.export(
            listOf(SectorSplitEntity(id = 1, lapId = 9, sectorIndex = 0, splitMs = 1200, sampleId = 3)),
        )
        assertTrue(csv.startsWith(SectorTimesCsv.HEADER))
        assertEquals("9,0,1200,3", csv.lines()[1])
    }
}
