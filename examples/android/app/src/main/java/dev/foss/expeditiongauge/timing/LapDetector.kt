package dev.foss.expeditiongauge.timing

import dev.foss.expeditiongauge.data.db.entities.LapEntity
import dev.foss.expeditiongauge.data.db.entities.SampleEntity

data class LineSegment(
    val startLat: Double,
    val startLon: Double,
    val endLat: Double,
    val endLon: Double,
) {
    fun crosses(from: SampleEntity, to: SampleEntity, minSpeedMps: Float = 2f): Boolean {
        if (from.latitude == null || from.longitude == null ||
            to.latitude == null || to.longitude == null
        ) {
            return false
        }
        if (to.speedMps < minSpeedMps) return false
        return segmentIntersects(
            from.longitude, from.latitude,
            to.longitude, to.latitude,
            startLon, startLat,
            endLon, endLat,
        )
    }
}

data class LapCrossing(
    val sampleIndex: Int,
    val sampleId: Long,
    val timestampMs: Long,
)

class LapDetector(
    private val startFinish: LineSegment,
    private val minSpeedMps: Float = 2f,
) {
    private var lapStartIndex: Int = 0
    private var lapStartSampleId: Long = 0L
    private var lapStartTimeMs: Long = 0L
    private var lapNumber: Int = 0
    private var hasCrossedOnce: Boolean = false

    fun reset(firstSample: SampleEntity, firstIndex: Int = 0) {
        lapStartIndex = firstIndex
        lapStartSampleId = firstSample.id
        lapStartTimeMs = firstSample.timestampMs
        lapNumber = 0
        hasCrossedOnce = false
    }

    fun process(samples: List<SampleEntity>, sessionId: Long): List<LapEntity> {
        if (samples.size < 2) return emptyList()
        reset(samples.first())
        val laps = mutableListOf<LapEntity>()
        for (i in 1 until samples.size) {
            val prev = samples[i - 1]
            val curr = samples[i]
            if (startFinish.crosses(prev, curr, minSpeedMps)) {
                if (hasCrossedOnce) {
                    lapNumber++
                    val duration = curr.timestampMs - lapStartTimeMs
                    laps += LapEntity(
                        sessionId = sessionId,
                        lapNumber = lapNumber,
                        startSampleId = lapStartSampleId,
                        endSampleId = curr.id,
                        durationMs = duration,
                        isValid = duration > 0,
                        isOutLap = lapNumber == 1,
                    )
                }
                hasCrossedOnce = true
                lapStartIndex = i
                lapStartSampleId = curr.id
                lapStartTimeMs = curr.timestampMs
            }
        }
        return laps
    }

    fun detectCrossings(samples: List<SampleEntity>): List<LapCrossing> {
        val crossings = mutableListOf<LapCrossing>()
        for (i in 1 until samples.size) {
            if (startFinish.crosses(samples[i - 1], samples[i], minSpeedMps)) {
                crossings += LapCrossing(i, samples[i].id, samples[i].timestampMs)
            }
        }
        return crossings
    }
}

internal fun segmentIntersects(
    x1: Double, y1: Double,
    x2: Double, y2: Double,
    x3: Double, y3: Double,
    x4: Double, y4: Double,
): Boolean {
    fun orient(ax: Double, ay: Double, bx: Double, by: Double, cx: Double, cy: Double): Double =
        (bx - ax) * (cy - ay) - (by - ay) * (cx - ax)

    val d1 = orient(x3, y3, x4, y4, x1, y1)
    val d2 = orient(x3, y3, x4, y4, x2, y2)
    val d3 = orient(x1, y1, x2, y2, x3, y3)
    val d4 = orient(x1, y1, x2, y2, x4, y4)
    return ((d1 > 0 && d2 < 0) || (d1 < 0 && d2 > 0)) &&
        ((d3 > 0 && d4 < 0) || (d3 < 0 && d4 > 0))
}

fun parseStartFinishFromGeoJson(geoJson: String): LineSegment? {
    val coordsRegex = """\[(-?\d+\.?\d*),\s*(-?\d+\.?\d*)\]""".toRegex()
    val matches = coordsRegex.findAll(geoJson).take(2).toList()
    if (matches.size < 2) return null
    val (lon1, lat1) = matches[0].destructured
    val (lon2, lat2) = matches[1].destructured
    return LineSegment(lat1.toDouble(), lon1.toDouble(), lat2.toDouble(), lon2.toDouble())
}

fun haversineM(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
    val r = 6_371_000.0
    val dLat = Math.toRadians(lat2 - lat1)
    val dLon = Math.toRadians(lon2 - lon1)
    val a = kotlin.math.sin(dLat / 2).let { it * it } +
        kotlin.math.cos(Math.toRadians(lat1)) * kotlin.math.cos(Math.toRadians(lat2)) *
        kotlin.math.sin(dLon / 2).let { it * it }
    return 2 * r * kotlin.math.asin(kotlin.math.sqrt(a.coerceIn(0.0, 1.0)))
}
