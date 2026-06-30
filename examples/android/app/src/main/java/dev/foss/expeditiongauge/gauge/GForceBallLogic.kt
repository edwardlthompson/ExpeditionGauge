package dev.foss.expeditiongauge.gauge

/**
 * Stub for Sprint 11 G-Force / Hybrid ball mapping (latG + lonG → ball position).
 */
object GForceBallLogic {
    const val MAX_G = 1.5f
    const val RING_05_G = 0.5f
    const val RING_10_G = 1.0f
    const val RING_15_G = 1.5f

    fun mapLatLonG(latG: Float, lonG: Float): BallPosition {
        val normX = (latG / MAX_G).coerceIn(-1f, 1f)
        val normY = (-lonG / MAX_G).coerceIn(-1f, 1f)
        val magnitude = kotlin.math.hypot(latG.toDouble(), lonG.toDouble()).toFloat()
        val zone = zoneForG(magnitude)
        return BallPosition(normX, normY, zone)
    }

    fun ringRadiusFraction(ringG: Float): Float = (ringG / MAX_G).coerceIn(0f, 1f)

    fun zoneForG(magnitudeG: Float): GaugeZone = when {
        magnitudeG >= RING_15_G -> GaugeZone.Critical
        magnitudeG >= RING_10_G -> GaugeZone.Caution
        else -> GaugeZone.Safe
    }
}
