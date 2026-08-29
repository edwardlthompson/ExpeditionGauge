package dev.foss.expeditiongauge.externalgpsrate

/** Clamp external GPS baud and NMEA update rate. */
object ExternalGpsRate {
    val BAUDS = listOf(4800, 9600, 19200, 38400, 115200)
    const val MIN_HZ = 1
    const val MAX_HZ = 20

    fun clampBaud(baud: Int): Int = BAUDS.minBy { kotlin.math.abs(it - baud) }

    fun clampHz(hz: Int): Int = hz.coerceIn(MIN_HZ, MAX_HZ)
}
