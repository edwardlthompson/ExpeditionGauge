package dev.foss.expeditiongauge.dtcclear

object DtcClear {
    const val MAX_PARKED_SPEED_MPS = 0.5f

    fun canClear(speedMps: Float?, recording: Boolean): Boolean =
        !recording && (speedMps == null || speedMps < MAX_PARKED_SPEED_MPS)

    fun parseAck(raw: String?): Boolean {
        if (raw.isNullOrBlank()) return false
        val hex = raw.filter { it.isDigit() || it in 'A'..'F' || it in 'a'..'f' }.uppercase()
        return hex.contains("44") && !hex.contains("7F04") && !hex.contains("7F44")
    }
}
