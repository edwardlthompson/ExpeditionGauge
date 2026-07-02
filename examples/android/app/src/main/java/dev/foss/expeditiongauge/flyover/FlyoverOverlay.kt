package dev.foss.expeditiongauge.flyover

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import dev.foss.expeditiongauge.data.db.entities.SampleEntity
import dev.foss.expeditiongauge.gauge.UnitDisplay
import dev.foss.expeditiongauge.playback.ElevationProfileBuilder
import dev.foss.expeditiongauge.settings.SpeedUnit

object FlyoverOverlay {
    fun hudLines(sample: SampleEntity?, speedUnit: SpeedUnit = SpeedUnit.METRIC): List<String> {
        if (sample == null) return listOf("ExpeditionGauge Flyover")
        val useMetric = speedUnit == SpeedUnit.METRIC
        val speed = UnitDisplay.speedMpsToDisplay(sample.speedMps, speedUnit)
        val speedUnitLabel = UnitDisplay.speedUnitLabel(speedUnit)
        val elev = sample.altitudeM?.let {
            "${UnitDisplay.altitudeMToDisplay(it, useMetric)} ${UnitDisplay.altitudeUnitLabel(useMetric)}"
        } ?: "— ${UnitDisplay.altitudeUnitLabel(useMetric)}"
        return listOf(
            "Speed %.0f $speedUnitLabel".format(speed),
            "Elev $elev",
        )
    }

    fun enhancedHudLines(sample: SampleEntity?, speedUnit: SpeedUnit = SpeedUnit.METRIC): List<String> {
        if (sample == null) return hudLines(null, speedUnit)
        val beta = sample.driftAngleDeg ?: 0f
        val latG = sample.latG
        return hudLines(sample, speedUnit) + listOf(
            "β %.1f°".format(beta),
            "latG %.2f".format(latG),
        )
    }

    fun draw(
        canvas: Canvas,
        sample: SampleEntity?,
        enhanced: Boolean = true,
        speedUnit: SpeedUnit = SpeedUnit.METRIC,
    ) {
        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE
            textSize = (canvas.width.coerceAtLeast(1) / 24f).coerceIn(24f, 64f)
            typeface = Typeface.create(Typeface.MONOSPACE, Typeface.BOLD)
            setShadowLayer(4f, 2f, 2f, Color.BLACK)
        }
        var y = paint.textSize * 1.5f
        val lines = if (enhanced) enhancedHudLines(sample, speedUnit) else hudLines(sample, speedUnit)
        lines.forEach { line ->
            canvas.drawText(line, 24f, y, paint)
            y += paint.textSize * 1.2f
        }
    }

    fun routeColorForSample(sample: SampleEntity): Int {
        val latG = sample.latG.coerceIn(0f, 2f)
        val beta = (sample.driftAngleDeg ?: 0f).coerceIn(0f, 45f) / 45f
        val mix = (latG / 2f * 0.6f + beta * 0.4f).coerceIn(0f, 1f)
        val r = (80 + mix * 175).toInt()
        val g = (200 - mix * 120).toInt()
        return Color.rgb(r, g, 80)
    }

    fun elevationOffset(sample: SampleEntity, profile: List<Double>, index: Int, span: Double): Float {
        if (profile.isEmpty() || span <= 0.0) return 0f
        val alt = profile.getOrElse(index) { sample.altitudeM ?: 0.0 }
        val min = profile.minOrNull() ?: alt
        return ((alt - min) / span).toFloat().coerceIn(0f, 1f)
    }

    fun buildElevationProfile(samples: List<SampleEntity>): List<Double> =
        ElevationProfileBuilder.build(samples)?.smoothedAltitudesM.orEmpty()
}
