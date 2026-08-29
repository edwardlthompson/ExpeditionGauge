package dev.foss.expeditiongauge.parkedidle

object ParkedIdleDim {
    const val PARKED_MPS = 0.5f
    const val DIM = 0.12f

    fun parked(speedMps: Float?): Boolean = speedMps == null || speedMps < PARKED_MPS

    fun apply(base: Float, parked: Boolean): Float {
        if (!parked) return base
        return if (base < 0f) DIM else minOf(base, DIM)
    }
}
