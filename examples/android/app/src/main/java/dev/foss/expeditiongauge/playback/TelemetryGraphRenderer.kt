package dev.foss.expeditiongauge.playback

import androidx.compose.ui.graphics.Color
import dev.foss.expeditiongauge.data.db.entities.SampleEntity
import dev.foss.expeditiongauge.export.ExportExtrasParser

data class GraphSeries(
    val label: String,
    val values: List<Float>,
    val color: Color,
)

object TelemetryGraphRenderer {
    const val MAX_POINTS = 2000

    fun decimate(samples: List<SampleEntity>, selector: (SampleEntity) -> Float?): GraphSeries? {
        val points = samples.mapNotNull { s -> selector(s)?.let { s.timestampMs to it } }
        if (points.isEmpty()) return null
        val step = kotlin.math.ceil(points.size.toDouble() / MAX_POINTS).toInt().coerceAtLeast(1)
        val decimated = points.filterIndexed { index, _ -> index % step == 0 }
        return GraphSeries(
            label = "",
            values = decimated.map { it.second },
            color = Color.Unspecified,
        )
    }

    fun speedTabSeries(samples: List<SampleEntity>): List<GraphSeries> = listOfNotNull(
        decimate(samples) { it.speedMps * 3.6f }?.copy(label = "speed", color = Color(0xFFFFD700)),
        decimate(samples) { it.driftAngleDeg }?.copy(label = "beta", color = Color.Cyan),
        decimate(samples) { it.throttle }?.copy(label = "throttle", color = Color(0xFF88FF88)),
        decimate(samples) { it.rpm?.toFloat() }?.copy(label = "rpm", color = Color(0xFFFF8888)),
    )

    fun attitudeTabSeries(samples: List<SampleEntity>): List<GraphSeries> = listOfNotNull(
        decimate(samples) { it.pitchDeg }?.copy(label = "pitch", color = Color(0xFFFFD700)),
        decimate(samples) { it.rollDeg }?.copy(label = "roll", color = Color.Cyan),
        decimate(samples) { it.latG }?.copy(label = "latG", color = Color(0xFF88FF88)),
        decimate(samples) { it.lonAccel }?.copy(label = "lonG", color = Color(0xFFFF8888)),
    )

    fun tireTabSeries(samples: List<SampleEntity>): List<GraphSeries> {
        val hasTpms = samples.any { ExportExtrasParser.tpmsColumns(it.extrasJson).hasAnyData }
        if (!hasTpms) return emptyList()
        return listOfNotNull(
            decimate(samples) { s ->
                ExportExtrasParser.tpmsColumns(s.extrasJson).frontLeft.pressureKpa
            }?.copy(label = "fl_kpa", color = Color(0xFFFFD700)),
            decimate(samples) { s ->
                ExportExtrasParser.tpmsColumns(s.extrasJson).frontRight.pressureKpa
            }?.copy(label = "fr_kpa", color = Color.Cyan),
            decimate(samples) { s ->
                ExportExtrasParser.tpmsColumns(s.extrasJson).rearLeft.pressureKpa
            }?.copy(label = "rl_kpa", color = Color(0xFF88FF88)),
            decimate(samples) { s ->
                ExportExtrasParser.tpmsColumns(s.extrasJson).rearRight.pressureKpa
            }?.copy(label = "rr_kpa", color = Color(0xFFFF8888)),
        )
    }
}
