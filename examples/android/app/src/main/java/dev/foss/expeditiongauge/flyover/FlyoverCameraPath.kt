package dev.foss.expeditiongauge.flyover

import dev.foss.expeditiongauge.data.db.entities.SampleEntity
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

data class FlyoverKeyframe(
    val sampleIndex: Int,
    val latitude: Double,
    val longitude: Double,
    val bearingDeg: Double,
    val pitchDeg: Double = FlyoverCameraPath.DEFAULT_PITCH,
    val zoom: Double = FlyoverCameraPath.DEFAULT_ZOOM,
)

object FlyoverCameraPath {
    const val DEFAULT_PITCH = 55.0
    const val DEFAULT_ZOOM = 15.0

    fun build(samples: List<SampleEntity>, maxKeyframes: Int = 300): List<FlyoverKeyframe> {
        if (samples.isEmpty()) return emptyList()
        if (samples.size == 1) {
            val s = samples.first()
            return listOf(keyframeForSample(s, 0, bearingForSample(samples, 0)))
        }
        val step = (samples.size - 1).toDouble() / (maxKeyframes - 1).coerceAtLeast(1)
        return (0 until maxKeyframes.coerceAtMost(samples.size)).map { frame ->
            val index = (frame * step).toInt().coerceIn(0, samples.lastIndex)
            keyframeForSample(samples[index], index, bearingForSample(samples, index))
        }
    }

    internal fun bearingForSample(samples: List<SampleEntity>, index: Int): Double {
        val current = samples[index]
        val nextIndex = (index + 1).coerceAtMost(samples.lastIndex)
        val prevIndex = (index - 1).coerceAtLeast(0)
        val from = samples[if (index < samples.lastIndex) index else prevIndex]
        val to = samples[if (index < samples.lastIndex) nextIndex else index]
        val lat1 = from.latitude ?: current.latitude ?: return current.headingDeg.toDouble()
        val lon1 = from.longitude ?: current.longitude ?: return current.headingDeg.toDouble()
        val lat2 = to.latitude ?: lat1
        val lon2 = to.longitude ?: lon1
        val y = sin(Math.toRadians(lon2 - lon1)) * cos(Math.toRadians(lat2))
        val x = cos(Math.toRadians(lat1)) * sin(Math.toRadians(lat2)) -
            sin(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) * cos(Math.toRadians(lon2 - lon1))
        return (Math.toDegrees(atan2(y, x)) + 360.0) % 360.0
    }

    private fun keyframeForSample(sample: SampleEntity, index: Int, bearing: Double): FlyoverKeyframe =
        FlyoverKeyframe(
            sampleIndex = index,
            latitude = sample.latitude ?: 0.0,
            longitude = sample.longitude ?: 0.0,
            bearingDeg = bearing,
        )
}
