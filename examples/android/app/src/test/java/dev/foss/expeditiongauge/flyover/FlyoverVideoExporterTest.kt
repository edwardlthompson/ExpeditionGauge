package dev.foss.expeditiongauge.flyover

import dev.foss.expeditiongauge.data.db.entities.SampleEntity
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class FlyoverVideoExporterTest {
    private val samples = listOf(
        SampleEntity(id = 1, sessionId = 1, timestampMs = 0, latitude = 0.0, longitude = 0.0),
        SampleEntity(id = 2, sessionId = 1, timestampMs = 1000, latitude = 0.001, longitude = 0.001),
    )

    @Test
    fun nearestSampleIndexReturnsNullForEmptySamples() {
        assertNull(FlyoverVideoExporter().nearestSampleIndex(emptyList(), 500L))
    }

    @Test
    fun exportFailsWhenNoSamples() = runBlocking {
        val output = File.createTempFile("flyover-export", ".mp4").apply { deleteOnExit() }
        val result = FlyoverVideoExporter().export(
            samples = emptyList(),
            media = emptyList(),
            settings = FlyoverVideoExportSettings(),
            outputFile = output,
        )
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull()?.message?.contains("No samples") == true)
    }

    @Test
    fun nearestSampleIndexPicksClosestTimestamp() {
        assertTrue(FlyoverVideoExporter().nearestSampleIndex(samples, 900L) == 1)
    }
}
