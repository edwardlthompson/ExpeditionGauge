package dev.foss.expeditiongauge.drivingline

import dev.foss.expeditiongauge.data.db.entities.SampleEntity
import kotlin.math.abs

enum class DrivingLinePointType {
    APEX,
    BRAKE_ZONE,
    ENTRY,
    EXIT,
}

data class DrivingLinePoint(
    val sampleIndex: Int,
    val latitude: Double,
    val longitude: Double,
    val type: DrivingLinePointType,
    val latG: Float,
)

data class DrivingLineSegment(
    val startIndex: Int,
    val endIndex: Int,
    val offsetM: Float,
    val brakeIntensity: Float = 0f,
)

data class DrivingLineAnalysis(
    val apexPoints: List<DrivingLinePoint>,
    val brakeZones: List<DrivingLinePoint>,
    val offsetSegments: List<DrivingLineSegment>,
)

class DrivingLineAnalyzer(
    private val brakeThreshold: Float = -0.3f,
    private val apexMinLatG: Float = 0.4f,
) {
    fun analyze(samples: List<SampleEntity>): DrivingLineAnalysis {
        if (samples.size < 3) {
            return DrivingLineAnalysis(emptyList(), emptyList(), emptyList())
        }
        val apexPoints = findApexPoints(samples)
        val brakeZones = findBrakeZones(samples)
        val offsetSegments = computeOffsetSegments(samples)
        return DrivingLineAnalysis(apexPoints, brakeZones, offsetSegments)
    }

    private fun findApexPoints(samples: List<SampleEntity>): List<DrivingLinePoint> {
        val points = mutableListOf<DrivingLinePoint>()
        for (i in 1 until samples.lastIndex) {
            val prev = abs(samples[i - 1].latG)
            val curr = abs(samples[i].latG)
            val next = abs(samples[i + 1].latG)
            if (curr >= apexMinLatG && curr >= prev && curr >= next) {
                val s = samples[i]
                if (s.latitude != null && s.longitude != null) {
                    points += DrivingLinePoint(i, s.latitude, s.longitude, DrivingLinePointType.APEX, s.latG)
                }
            }
        }
        return points
    }

    private fun findBrakeZones(samples: List<SampleEntity>): List<DrivingLinePoint> {
        return samples.mapIndexedNotNull { index, sample ->
            if (sample.lonAccel <= brakeThreshold &&
                sample.latitude != null &&
                sample.longitude != null
            ) {
                DrivingLinePoint(index, sample.latitude, sample.longitude, DrivingLinePointType.BRAKE_ZONE, sample.latG)
            } else {
                null
            }
        }.distinctBy { it.sampleIndex / 5 }
    }

    private fun computeOffsetSegments(samples: List<SampleEntity>): List<DrivingLineSegment> {
        val segments = mutableListOf<DrivingLineSegment>()
        var start = 0
        for (i in 1 until samples.size) {
            val prevSign = samples[i - 1].latG.sign()
            val currSign = samples[i].latG.sign()
            if (prevSign != currSign && abs(samples[i].latG) > 0.2f) {
                segments += DrivingLineSegment(
                    startIndex = start,
                    endIndex = i,
                    offsetM = samples[i].latG * 2f,
                    brakeIntensity = kotlin.math.abs(samples[i].lonAccel.coerceAtMost(0f)),
                )
                start = i
            }
        }
        return segments
    }

    private fun Float.sign(): Int = when {
        this > 0.05f -> 1
        this < -0.05f -> -1
        else -> 0
    }
}
