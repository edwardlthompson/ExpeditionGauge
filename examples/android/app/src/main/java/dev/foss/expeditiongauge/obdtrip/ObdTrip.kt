package dev.foss.expeditiongauge.obdtrip

data class ObdTripSinceClear(
    val distanceKm: Int? = null,
    val warmups: Int? = null,
    val timeMin: Int? = null,
)

object ObdTrip {
    fun parseDistanceKm(raw: String?): Int? = parseU16(raw, "4131")
    fun parseWarmups(raw: String?): Int? = parseU8(raw, "4130")
    fun parseTimeMin(raw: String?): Int? = parseU16(raw, "414E")

    fun line(trip: ObdTripSinceClear?): String? {
        if (trip == null) return null
        val parts = buildList {
            trip.distanceKm?.let { add("$it km") }
            trip.warmups?.let { add("$it wu") }
            trip.timeMin?.let { add("$it min") }
        }
        return if (parts.isEmpty()) null else "Since clear: " + parts.joinToString(" · ")
    }

    private fun hexPayload(raw: String?): String? {
        if (raw.isNullOrBlank()) return null
        val upper = raw.uppercase()
        if (upper.contains("NO DATA") || upper.contains("UNABLE")) return null
        if (upper.contains("7F30") || upper.contains("7F31") || upper.contains("7F4E")) {
            return null
        }
        val hex = raw.filter { it.isDigit() || it in 'A'..'F' || it in 'a'..'f' }.uppercase()
        return hex.ifBlank { null }
    }

    private fun parseU8(raw: String?, header: String): Int? {
        val hex = hexPayload(raw) ?: return null
        val idx = evenIndex(hex, header)
        if (idx < 0 || idx + header.length + 2 > hex.length) return null
        return hex.substring(idx + header.length, idx + header.length + 2).toIntOrNull(16)
    }

    private fun parseU16(raw: String?, header: String): Int? {
        val hex = hexPayload(raw) ?: return null
        val idx = evenIndex(hex, header)
        if (idx < 0 || idx + header.length + 4 > hex.length) return null
        return hex.substring(idx + header.length, idx + header.length + 4).toIntOrNull(16)
    }

    private fun evenIndex(hex: String, token: String): Int {
        var i = 0
        while (i + token.length <= hex.length) {
            if (hex.regionMatches(i, token, 0, token.length)) return i
            i += 2
        }
        return -1
    }
}
