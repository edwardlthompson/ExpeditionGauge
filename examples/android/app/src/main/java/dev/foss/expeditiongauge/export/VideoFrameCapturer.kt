package dev.foss.expeditiongauge.export

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import dev.foss.expeditiongauge.data.db.entities.SampleEntity
import dev.foss.expeditiongauge.video.VideoOverlayCompositor
import kotlin.math.max

object VideoFrameCapturer {
    const val DEFAULT_WIDTH = 1280
    const val DEFAULT_HEIGHT = 720

    fun frameCount(sessionDurationMs: Long, settings: PlaybackVideoExportSettings): Int {
        val clipMs = minOf(sessionDurationMs.coerceAtLeast(1L), settings.clipDurationMs)
        return max(1, ((clipMs * settings.frameRate) / 1000L).toInt())
    }

    fun captureFrame(
        samples: List<SampleEntity>,
        frameIndex: Int,
        totalFrames: Int,
        settings: PlaybackVideoExportSettings,
    ): Bitmap {
        val bitmap = Bitmap.createBitmap(settings.width, settings.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        canvas.drawColor(Color.rgb(18, 18, 22))

        if (samples.isEmpty()) {
            VideoOverlayCompositor.drawPlaybackExportOverlay(canvas, null)
            return bitmap
        }

        val endIndex = sampleIndexForFrame(samples.size, frameIndex, totalFrames)
        val routePoints = normalizedRoute(samples, endIndex)
        drawRoute(canvas, routePoints, settings.width, settings.height)
        VideoOverlayCompositor.drawPlaybackExportOverlay(canvas, samples[endIndex])
        return bitmap
    }

    internal fun sampleIndexForFrame(sampleCount: Int, frameIndex: Int, totalFrames: Int): Int {
        if (sampleCount <= 1 || totalFrames <= 1) return 0
        val ratio = frameIndex.toFloat() / (totalFrames - 1).coerceAtLeast(1)
        return (ratio * (sampleCount - 1)).toInt().coerceIn(0, sampleCount - 1)
    }

    internal fun normalizedRoute(samples: List<SampleEntity>, upToIndex: Int): List<Pair<Float, Float>> {
        val slice = samples.take(upToIndex + 1)
        val coords = slice.mapNotNull { sample ->
            val lat = sample.latitude ?: return@mapNotNull null
            val lon = sample.longitude ?: return@mapNotNull null
            lat to lon
        }
        if (coords.size < 2) return emptyList()

        val minLat = coords.minOf { it.first }
        val maxLat = coords.maxOf { it.first }
        val minLon = coords.minOf { it.second }
        val maxLon = coords.maxOf { it.second }
        val latSpan = max(maxLat - minLat, 1e-9)
        val lonSpan = max(maxLon - minLon, 1e-9)
        val pad = 0.08f
        return coords.map { (lat, lon) ->
            val x = pad + ((lon - minLon) / lonSpan).toFloat() * (1f - 2f * pad)
            val y = pad + (1f - ((lat - minLat) / latSpan).toFloat()) * (1f - 2f * pad)
            x.coerceIn(0f, 1f) to y.coerceIn(0f, 1f)
        }
    }

    private fun drawRoute(
        canvas: Canvas,
        points: List<Pair<Float, Float>>,
        width: Int,
        height: Int,
    ) {
        if (points.size < 2) return
        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.rgb(80, 200, 120)
            strokeWidth = 6f
            style = Paint.Style.STROKE
        }
        var previous: android.graphics.PointF? = null
        points.forEach { (nx, ny) ->
            val point = android.graphics.PointF(nx * width, ny * height)
            previous?.let { canvas.drawLine(it.x, it.y, point.x, point.y, paint) }
            previous = point
        }
    }
}
