package dev.foss.expeditiongauge.playback

import dev.foss.expeditiongauge.data.db.entities.SampleEntity
import org.junit.Assert.assertEquals
import org.junit.Test
import kotlin.math.abs

class LonGHeatmapTest {
    @Test
    fun usesAbsLonAccel() {
        val sample = SampleEntity(sessionId = 1, timestampMs = 1, lonAccel = -1.2f)
        assertEquals(abs(-1.2f), RouteHeatmapLayer.sampleIntensity(sample, HeatmapMetric.LON_G))
        assertEquals(2, RouteHeatmapLayer.heatmapColorBucket(1.2f, HeatmapMetric.LON_G))
    }
}
