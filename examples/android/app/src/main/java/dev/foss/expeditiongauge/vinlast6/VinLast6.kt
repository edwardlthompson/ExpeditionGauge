package dev.foss.expeditiongauge.vinlast6

object VinLast6 {
    fun parseVin(raw: String?): String? {
        val hex = hexPayload(raw) ?: return null
        val idx = evenIndex(hex, "4902")
        if (idx < 0) return null
        var payload = hex.substring(idx + 4)
        if (payload.length >= 2) {
            val count = payload.substring(0, 2).toIntOrNull(16)
            if (count != null && count in 1..4) payload = payload.substring(2)
        }
        val vin = buildString {
            var i = 0
            while (i + 2 <= payload.length) {
                val c = payload.substring(i, i + 2).toIntOrNull(16)?.toChar()
                if (c != null && c.isLetterOrDigit()) append(c)
                i += 2
            }
        }
        return vin.takeIf { it.length >= 6 }
    }

    fun last6(vin: String?): String? = vin?.takeLast(6)

    fun line(last6: String?): String? =
        last6?.takeIf { it.length == 6 }?.let { "VIN …$it" }

    private fun hexPayload(raw: String?): String? {
        if (raw.isNullOrBlank()) return null
        val upper = raw.uppercase()
        if (upper.contains("NO DATA") || upper.contains("UNABLE") || upper.contains("7F09")) {
            return null
        }
        val hex = raw.filter { it.isDigit() || it in 'A'..'F' || it in 'a'..'f' }.uppercase()
        return hex.ifBlank { null }
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
