package dev.foss.expeditiongauge.playback

import dev.foss.expeditiongauge.data.db.entities.SampleEntity
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class TelemetryGraphRendererTest {
    @Test
    fun decimationCapsPointCount() {
        val samples = (0 until 5000).map { i ->
            SampleEntity(id = i.toLong(), sessionId = 1L, timestampMs = i.toLong(), speedMps = i.toFloat())
        }
        val series = TelemetryGraphRenderer.speedTabSeries(samples).first()
        assertTrue(series.values.size <= TelemetryGraphRenderer.MAX_POINTS)
    }

    @Test
    fun tireTabEmptyWithoutTpms() {
        val samples = listOf(
            SampleEntity(id = 1, sessionId = 1L, timestampMs = 0, speedMps = 10f),
        )
        assertTrue(TelemetryGraphRenderer.tireTabSeries(samples).isEmpty())
    }

    @Test
    fun attitudeTabIncludesLatG() {
        val samples = listOf(
            SampleEntity(id = 1, sessionId = 1L, timestampMs = 0, latG = 1.2f),
            SampleEntity(id = 2, sessionId = 1L, timestampMs = 100, latG = 0.5f),
        )
        val labels = TelemetryGraphRenderer.attitudeTabSeries(samples).map { it.label }.toSet()
        assertTrue(labels.contains("latG"))
    }
}
