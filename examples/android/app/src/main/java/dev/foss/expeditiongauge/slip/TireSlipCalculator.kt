package dev.foss.expeditiongauge.slip

data class SlipSample(
    val slipRatio: Float?,
    val rearSlipRatio: Float? = null,
    val source: String = "none",
)

/**
 * Computes tire slip ratio from wheel speed vs GPS speed.
 * Distinct from drift angle β (sideslip).
 */
class TireSlipCalculator {
    fun compute(
        gpsSpeedMps: Float,
        wheelSpeedMps: Float?,
        rearLeftMps: Float? = null,
        rearRightMps: Float? = null,
    ): SlipSample {
        if (gpsSpeedMps < MIN_GPS_SPEED_MPS) {
            return SlipSample(slipRatio = null, rearSlipRatio = null, source = "below_threshold")
        }
        val primary = wheelSpeedMps?.let { ratio(it, gpsSpeedMps) }
        val rear = when {
            rearLeftMps != null && rearRightMps != null ->
                ratio((rearLeftMps + rearRightMps) / 2f, gpsSpeedMps)
            rearLeftMps != null -> ratio(rearLeftMps, gpsSpeedMps)
            rearRightMps != null -> ratio(rearRightMps, gpsSpeedMps)
            else -> null
        }
        val source = when {
            rear != null -> "rear_axle"
            primary != null -> "wheel_speed"
            else -> "none"
        }
        return SlipSample(slipRatio = primary, rearSlipRatio = rear, source = source)
    }

    private fun ratio(wheelMps: Float, gpsMps: Float): Float =
        (wheelMps - gpsMps) / gpsMps.coerceAtLeast(0.1f)

    companion object {
        const val MIN_GPS_SPEED_MPS = 1.4f // ~5 km/h
    }
}
