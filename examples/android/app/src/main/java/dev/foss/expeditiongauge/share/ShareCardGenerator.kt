package dev.foss.expeditiongauge.share

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import dev.foss.expeditiongauge.stats.SessionStatsSummary
import java.io.File
import java.io.FileOutputStream

object ShareCardGenerator {
    const val WIDTH = 1080
    const val HEIGHT = 1350

    private const val COLOR_BG = 0xFF121212.toInt()
    private const val COLOR_ROUTE_BG = 0xFF1A1A1A.toInt()
    private const val COLOR_GREEN = 0xFF33CC33.toInt()
    private const val COLOR_YELLOW = 0xFFFFCC00.toInt()
    private const val COLOR_WHITE = 0xFFEEEEEE.toInt()

    fun generate(summary: SessionStatsSummary): Bitmap {
        val bitmap = Bitmap.createBitmap(WIDTH, HEIGHT, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        canvas.drawColor(COLOR_BG)

        val titlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = COLOR_YELLOW
            textSize = 52f
            isFakeBoldText = true
        }
        val bodyPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = COLOR_WHITE
            textSize = 36f
        }
        val brandPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = COLOR_YELLOW
            textSize = 32f
        }

        var y = 72f
        canvas.drawText(summary.name.take(40), 48f, y, titlePaint)
        y += 48f

        val mapTop = y + 16f
        val mapHeight = 420f
        val mapRect = android.graphics.RectF(48f, mapTop, WIDTH - 48f, mapTop + mapHeight)
        canvas.drawRect(mapRect, Paint().apply { color = COLOR_ROUTE_BG })
        drawRoute(canvas, summary.routeThumb, mapRect)
        y = mapRect.bottom + 56f

        val durationSec = summary.durationMs / 1000
        canvas.drawText("Duration: ${durationSec}s", 48f, y, bodyPaint)
        y += 52f
        summary.peakLatG?.let {
            canvas.drawText("Peak latG: %.2f".format(it), 48f, y, bodyPaint)
            y += 52f
        }
        summary.maxBetaDeg?.let {
            canvas.drawText("Max β: %.1f°".format(it), 48f, y, bodyPaint)
            y += 52f
        }
        canvas.drawText("Slip events: ${summary.slipEventCount}", 48f, y, bodyPaint)
        y += 52f
        if (summary.eventCount > 0) {
            canvas.drawText("Marked events: ${summary.eventCount}", 48f, y, bodyPaint)
            y += 52f
        }
        summary.bestLapMs?.let {
            canvas.drawText("Best lap: ${formatLap(it)}", 48f, y, bodyPaint)
        }

        canvas.drawText("ExpeditionGauge", 48f, HEIGHT - 48f, brandPaint)
        return bitmap
    }

    fun writeToFile(summary: SessionStatsSummary, output: File): File {
        output.parentFile?.mkdirs()
        FileOutputStream(output).use { stream ->
            generate(summary).compress(Bitmap.CompressFormat.PNG, 95, stream)
        }
        return output
    }

    private fun drawRoute(canvas: Canvas, routePoints: List<Pair<Float, Float>>, bounds: android.graphics.RectF) {
        if (routePoints.size < 2) return
        val path = Path()
        routePoints.forEachIndexed { index, (nx, ny) ->
            val x = bounds.left + nx * bounds.width()
            val y = bounds.top + ny * bounds.height()
            if (index == 0) path.moveTo(x, y) else path.lineTo(x, y)
        }
        canvas.drawPath(
            path,
            Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = COLOR_GREEN
                style = Paint.Style.STROKE
                strokeWidth = 6f
                strokeJoin = Paint.Join.ROUND
                strokeCap = Paint.Cap.ROUND
            },
        )
    }

    private fun formatLap(ms: Long): String {
        val totalSec = ms / 1000
        val min = totalSec / 60
        val sec = totalSec % 60
        val frac = (ms % 1000) / 10
        return if (min > 0) "%d:%02d.%02d".format(min, sec, frac) else "%d.%02d".format(sec, frac)
    }
}
