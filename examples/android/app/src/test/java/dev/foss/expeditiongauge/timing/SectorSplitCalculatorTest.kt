package dev.foss.expeditiongauge.timing

import dev.foss.expeditiongauge.data.db.entities.LapEntity
import dev.foss.expeditiongauge.data.db.entities.SampleEntity
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SectorSplitCalculatorTest {
    /** East-west gate at lat=0.00045. */
    private val sectorLine = LineSegment(0.00045, 0.0, 0.00045, 0.002)

    @Test
    fun computesSectorSplitOnCrossing() {
        val lats = listOf(0.0001, 0.0003, 0.0005, 0.0007, 0.0009)
        val samples = lats.mapIndexed { i, lat ->
            SampleEntity(
                id = i.toLong() + 1,
                sessionId = 1L,
                timestampMs = i * 1000L,
                latitude = lat,
                longitude = 0.001,
                speedMps = 5f,
            )
        }
        val lap = LapEntity(
            sessionId = 1L,
            lapNumber = 1,
            startSampleId = 1,
            endSampleId = 5,
            durationMs = 4000,
        )
        val calc = SectorSplitCalculator(listOf(sectorLine))
        val result = calc.computeForLap(lap, samples)
        assertTrue(result.splits.isNotEmpty())
        assertEquals(0, result.splits.first().sectorIndex)
    }

    @Test
    fun theoreticalBestSumsSectorBests() {
        val calc = SectorSplitCalculator(emptyList())
        assertEquals(5000L, calc.theoreticalBest(listOf(2000L, 3000L)))
    }
}
