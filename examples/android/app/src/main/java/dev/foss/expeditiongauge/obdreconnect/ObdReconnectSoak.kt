package dev.foss.expeditiongauge.obdreconnect

object ObdReconnectSoak {
    const val CYCLES = 8

    fun passed(scans: Int, cycles: Int = CYCLES): Boolean =
        cycles > 0 && scans == cycles

    fun line(scans: Int, cycles: Int = CYCLES): String = "Soak $scans/$cycles"
}
