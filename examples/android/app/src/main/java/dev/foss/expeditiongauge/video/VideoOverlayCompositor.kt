package dev.foss.expeditiongauge.video

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import dev.foss.expeditiongauge.data.db.entities.SampleEntity

object VideoOverlayCompositor {
    fun nearestSample(samples: List<SampleEntity>, timestampMs: Long): SampleEntity? {
        if (samples.isEmpty()) return null
        var best = samples.first()
        var bestDelta = kotlin.math.abs(best.timestampMs - timestampMs)
        for (sample in samples) {
            val delta = kotlin.math.abs(sample.timestampMs - timestampMs)
            if (delta < bestDelta) {
                best = sample
                bestDelta = delta
            }
        }
        return best
    }

    fun overlayLines(sample: SampleEntity?): List<String> {
        if (sample == null) return listOf("ExpeditionGauge")
        val speedKmh = (sample.speedMps ?: 0f) * 3.6f
        val beta = sample.driftAngleDeg ?: 0f
        val latG = sample.latG ?: 0f
        return listOf(
            "Speed %.0f km/h".format(speedKmh),
            "β %.1f°".format(beta),
            "latG %.2f".format(latG),
        )
    }

    fun playbackExportLines(sample: SampleEntity?): List<String> {
        if (sample == null) return listOf("ExpeditionGauge")
        val base = overlayLines(sample)
        return base + listOf(
            "pitch %.1f°".format(sample.pitchDeg),
            "roll %.1f°".format(sample.rollDeg),
        )
    }

    fun drawPlaybackExportOverlay(canvas: Canvas, sample: SampleEntity?) {
        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE
            textSize = (canvas.width.coerceAtLeast(1) / 24f).coerceIn(24f, 64f)
            typeface = Typeface.create(Typeface.MONOSPACE, Typeface.BOLD)
            setShadowLayer(4f, 2f, 2f, Color.BLACK)
        }
        var y = paint.textSize * 1.5f
        playbackExportLines(sample).forEach { line ->
            canvas.drawText(line, 24f, y, paint)
            y += paint.textSize * 1.2f
        }
    }

    fun drawOverlay(canvas: Canvas, sample: SampleEntity?) {
        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE
            textSize = (canvas.width.coerceAtLeast(1) / 24f).coerceIn(24f, 64f)
            typeface = Typeface.create(Typeface.MONOSPACE, Typeface.BOLD)
            setShadowLayer(4f, 2f, 2f, Color.BLACK)
        }
        var y = paint.textSize * 1.5f
        overlayLines(sample).forEach { line ->
            canvas.drawText(line, 24f, y, paint)
            y += paint.textSize * 1.2f
        }
    }
}
