package dev.foss.expeditiongauge.stats

import dev.foss.expeditiongauge.data.db.entities.SampleEntity
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SessionStatsAggregatorTest {
    private val aggregator = SessionStatsAggregator(slipThreshold = 0.15f)

    @Test
    fun computeFromSamplesFindsPeakAndSlip() {
        val samples = listOf(
            SampleEntity(id = 1, sessionId = 1, timestampMs = 0, latG = 0.5f, driftAngleDeg = 10f),
            SampleEntity(id = 2, sessionId = 1, timestampMs = 100, latG = 1.2f, driftAngleDeg = 25f, slipRatio = 0.2f),
        )
        val metrics = aggregator.computeFromSamples(samples)
        assertEquals(1.2f, metrics.peakLatG)
        assertEquals(25f, metrics.maxBetaDeg)
        assertEquals(1, metrics.slipEventCount)
        assertTrue(metrics.sparklineLatG.isNotEmpty())
    }

    @Test
    fun compareComputesSlipDelta() {
        val left = SessionStatsSummary(1, "A", 1000, 20f, 1f, 3, 0)
        val right = SessionStatsSummary(2, "B", 1000, 15f, 0.8f, 1, 0)
        val cmp = aggregator.compare(left, right)
        assertEquals(2, cmp.slipDelta)
    }
}
