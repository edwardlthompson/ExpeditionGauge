package dev.foss.expeditiongauge.flyover

import dev.foss.expeditiongauge.data.db.entities.SampleEntity
import dev.foss.expeditiongauge.data.db.entities.SessionMediaEntity
import dev.foss.expeditiongauge.video.VideoBurnInEncoder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import kotlin.math.max

class FlyoverVideoExporter {
    suspend fun export(
        samples: List<SampleEntity>,
        media: List<SessionMediaEntity>,
        settings: FlyoverVideoExportSettings,
        outputFile: File,
        onProgress: (Int) -> Unit = {},
        frameDelayMs: suspend () -> Long = { 0L },
    ): Result<File> = withContext(Dispatchers.Default) {
        runCatching {
            require(samples.isNotEmpty()) { "No samples to export" }
            val sessionDurationMs = (samples.last().timestampMs - samples.first().timestampMs).coerceAtLeast(1L)
            val clipMs = minOf(sessionDurationMs, settings.clipDurationMs)
            val totalFrames = max(1, ((clipMs * settings.frameRate) / 1000L).toInt())
            val keyframes = FlyoverCameraPath.build(samples, maxKeyframes = totalFrames)
            val mediaMarkers = media.mapNotNull { item ->
                nearestSampleIndex(samples, item.timestampMs)?.let { FlyoverMediaMarker(it) }
            }
            val frames = ArrayList<android.graphics.Bitmap>(keyframes.size)
            try {
                keyframes.forEachIndexed { index, keyframe ->
                    frames.add(
                        MapLibreFlyoverRenderer.renderFrame(
                            samples = samples,
                            keyframe = keyframe,
                            width = settings.width,
                            height = settings.height,
                            mediaMarkers = mediaMarkers,
                            enhancedOverlay = settings.enhancedOverlay,
                        ),
                    )
                    onProgress(((index + 1) * 100) / keyframes.size.coerceAtLeast(1))
                    val delay = frameDelayMs()
                    if (delay > 0L) kotlinx.coroutines.delay(delay)
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

    internal fun nearestSampleIndex(samples: List<SampleEntity>, timestampMs: Long): Int? {
        if (samples.isEmpty()) return null
        var bestIndex = 0
        var bestDelta = kotlin.math.abs(samples.first().timestampMs - timestampMs)
        samples.forEachIndexed { index, sample ->
            val delta = kotlin.math.abs(sample.timestampMs - timestampMs)
            if (delta < bestDelta) {
                bestDelta = delta
                bestIndex = index
            }
        }
        return bestIndex
    }
}
