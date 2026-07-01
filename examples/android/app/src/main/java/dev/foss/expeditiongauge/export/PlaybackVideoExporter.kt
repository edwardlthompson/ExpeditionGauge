package dev.foss.expeditiongauge.export

import android.content.Context
import dev.foss.expeditiongauge.data.db.entities.SampleEntity
import dev.foss.expeditiongauge.video.VideoBurnInEncoder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

class PlaybackVideoExporter {
    suspend fun export(
        samples: List<SampleEntity>,
        settings: PlaybackVideoExportSettings,
        outputFile: File,
        onProgress: (Int) -> Unit = {},
    ): Result<File> = withContext(Dispatchers.Default) {
        runCatching {
            require(samples.isNotEmpty()) { "No samples to export" }
            val sessionDurationMs = (samples.last().timestampMs - samples.first().timestampMs).coerceAtLeast(1L)
            val totalFrames = VideoFrameCapturer.frameCount(sessionDurationMs, settings)
            val frames = ArrayList<android.graphics.Bitmap>(totalFrames)
            try {
                repeat(totalFrames) { index ->
                    frames.add(
                        VideoFrameCapturer.captureFrame(
                            samples = samples,
                            frameIndex = index,
                            totalFrames = totalFrames,
                            settings = settings,
                        ),
                    )
                    onProgress(((index + 1) * 100) / totalFrames)
                }
                VideoBurnInEncoder.encodeFrames(
                    frames = frames,
                    width = settings.width,
                    height = settings.height,
                    output = outputFile,
                    frameRate = settings.frameRate,
                )
                outputFile
            } finally {
                frames.forEach { it.recycle() }
            }
        }
    }
}
