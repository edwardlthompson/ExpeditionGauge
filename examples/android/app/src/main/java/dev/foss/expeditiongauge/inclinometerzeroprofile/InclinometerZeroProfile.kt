package dev.foss.expeditiongauge.inclinometerzeroprofile

data class ZeroOffset(val pitchDeg: Float, val rollDeg: Float)

/** Persist inclinometer zero per vehicle id as pitch|roll. */
object InclinometerZeroProfile {
    fun key(vehicleId: String): String = "zero:${vehicleId.trim().lowercase()}"

    fun encode(offset: ZeroOffset): String = "${offset.pitchDeg}|${offset.rollDeg}"

    fun decode(raw: String): ZeroOffset? {
        val parts = raw.split('|')
        if (parts.size != 2) return null
        return runCatching { ZeroOffset(parts[0].toFloat(), parts[1].toFloat()) }.getOrNull()
    }

    fun parseAll(blob: String): Map<String, ZeroOffset> =
        blob.split(';').mapNotNull { entry ->
            val parts = entry.split('=', limit = 2)
            if (parts.size != 2) return@mapNotNull null
            val offset = decode(parts[1]) ?: return@mapNotNull null
            parts[0] to offset
        }.toMap()
}
