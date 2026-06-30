package dev.foss.expeditiongauge.video

import android.content.Context
import android.graphics.Canvas
import android.media.MediaMetadataRetriever
import dev.foss.expeditiongauge.data.db.entities.SampleEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

/**
 * Local MediaCodec pipeline: sample frames from source video, burn telemetry overlay, mux MP4.
 */
class VideoBurnInExporter(private val context: Context) {
    suspend fun export(
        videoUri: String,
        samples: List<SampleEntity>,
        videoOffsetMs: Long,
        outputFile: File,
    ): Result<File> = withContext(Dispatchers.Default) {
        runCatching {
            val retriever = MediaMetadataRetriever()
            try {
                retriever.setDataSource(context, android.net.Uri.parse(videoUri))
                val durationUs = (retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
                    ?.toLongOrNull() ?: 0L) * 1000L
                val width = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH)
                    ?.toIntOrNull() ?: 1280
                val height = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT)
                    ?.toIntOrNull() ?: 720
                val frameStepUs = 100_000L
                val frames = mutableListOf<android.graphics.Bitmap>()
                var t = 0L
                while (t < durationUs) {
                    val frame = retriever.getFrameAtTime(t, MediaMetadataRetriever.OPTION_CLOSEST)
                    if (frame != null) {
                        val sessionMs = (t / 1000L) - videoOffsetMs
                        val sample = VideoOverlayCompositor.nearestSample(samples, sessionMs)
                        val composed = frame.copy(android.graphics.Bitmap.Config.ARGB_8888, true)
                        VideoOverlayCompositor.drawOverlay(Canvas(composed), sample)
                        frames.add(composed)
                        frame.recycle()
                    }
                    t += frameStepUs
                }
                if (frames.isEmpty()) error("No frames extracted from video")
                VideoBurnInEncoder.encodeFrames(frames, width, height, outputFile)
                outputFile
            } finally {
                retriever.release()
            }
        }
    }
}
