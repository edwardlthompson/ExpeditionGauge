package dev.foss.expeditiongauge.ghost

import dev.foss.expeditiongauge.data.db.entities.SectorSplitEntity
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class GhostLapComparerTest {
    @Test
    fun sectorDeltasAlignByIndex() {
        val primary = listOf(
            SectorSplitEntity(lapId = 1, sectorIndex = 0, splitMs = 1200, sampleId = 1),
            SectorSplitEntity(lapId = 1, sectorIndex = 1, splitMs = 900, sampleId = 2),
        )
        val ghost = listOf(
            SectorSplitEntity(lapId = 2, sectorIndex = 0, splitMs = 1100, sampleId = 3),
            SectorSplitEntity(lapId = 2, sectorIndex = 1, splitMs = 1000, sampleId = 4),
        )
        val rows = GhostLapComparer.sectorDeltas(primary, ghost)
        assertEquals(2, rows.size)
        assertEquals(100L, rows[0].deltaMs)
        assertEquals(-100L, rows[1].deltaMs)
    }

    @Test
    fun distanceDeltaUsesGhostSampleAtSameDistance() {
        val primary = listOf(
            dev.foss.expeditiongauge.data.db.entities.SampleEntity(
                id = 1, sessionId = 1L, timestampMs = 0,
                latitude = 0.0, longitude = 0.0,
            ),
            dev.foss.expeditiongauge.data.db.entities.SampleEntity(
                id = 2, sessionId = 1L, timestampMs = 1000,
                latitude = 0.0, longitude = 0.001,
            ),
        )
        val ghost = listOf(
            dev.foss.expeditiongauge.data.db.entities.SampleEntity(
                id = 3, sessionId = 1L, timestampMs = 0,
                latitude = 0.0, longitude = 0.0,
            ),
            dev.foss.expeditiongauge.data.db.entities.SampleEntity(
                id = 4, sessionId = 1L, timestampMs = 800,
                latitude = 0.0, longitude = 0.001,
            ),
        )
        val delta = GhostLapOverlay().computeDeltaByDistance(primary, ghost, 1)
        assertEquals(200L, delta)
    }
}
