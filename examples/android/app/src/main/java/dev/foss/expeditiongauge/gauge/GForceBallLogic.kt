package dev.foss.expeditiongauge.gauge

/**
 * Stub for Sprint 11 G-Force / Hybrid ball mapping (latG + lonG → ball position).
 */
object GForceBallLogic {
    const val MAX_G = 1.5f

    fun mapLatLonG(latG: Float, lonG: Float): BallPosition {
        val normX = (latG / MAX_G).coerceIn(-1f, 1f)
        val normY = (-lonG / MAX_G).coerceIn(-1f, 1f)
        val magnitude = kotlin.math.hypot(latG.toDouble(), lonG.toDouble()).toFloat()
        val zone = GaugeLogic.zoneForAngle(magnitude * 10f, 0.5f * 10f, 1.0f * 10f)
        return BallPosition(normX, normY, zone)
    }
}
