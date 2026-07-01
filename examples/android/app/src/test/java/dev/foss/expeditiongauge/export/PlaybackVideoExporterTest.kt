package dev.foss.expeditiongauge.export

import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class PlaybackVideoExporterTest {
    @Test
    fun exportFailsWhenNoSamples() = runBlocking {
        val output = File.createTempFile("playback-export", ".mp4").apply { deleteOnExit() }
        val result = PlaybackVideoExporter().export(
            samples = emptyList(),
            settings = PlaybackVideoExportSettings.PRESET_30S,
            outputFile = output,
        )
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull()?.message?.contains("No samples") == true)
    }
}
