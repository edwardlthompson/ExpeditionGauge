package dev.foss.expeditiongauge.obdtemps

import java.util.Locale

data class ObdTempsVoltageSnapshot(
    val coolantC: Float? = null,
    val oilC: Float? = null,
    val voltage: Float? = null,
)

object ObdTempsVoltage {
    const val PARKED_MPS = 0.5f

    fun parked(speedMps: Float?): Boolean = speedMps == null || speedMps < PARKED_MPS

    fun parseCoolantC(raw: String?): Float? = parseTempC(raw, "4105")

    fun parseOilC(raw: String?): Float? = parseTempC(raw, "415C")

    fun parseVoltage(raw: String?): Float? {
        val hex = hexPayload(raw) ?: return null
        val idx = evenIndex(hex, "4142")
        if (idx < 0 || idx + 8 > hex.length) return null
        val a = hex.substring(idx + 4, idx + 6).toIntOrNull(16) ?: return null
        val b = hex.substring(idx + 6, idx + 8).toIntOrNull(16) ?: return null
        return (a * 256 + b) / 1000f
    }

    fun line(snap: ObdTempsVoltageSnapshot?, parked: Boolean = true): String? {
        if (snap == null || !parked) return null
        val parts = buildList {
            snap.coolantC?.let { add("ECT ${it.toInt()}°C") }
            snap.oilC?.let { add("Oil ${it.toInt()}°C") }
            snap.voltage?.let { add("Batt ${"%.1f".format(Locale.US, it)}V") }
        }
        return parts.joinToString(" · ").ifBlank { null }
    }

    fun matches(line: String): Boolean =
        line.startsWith("ECT") || line.startsWith("Oil") || line.startsWith("Batt")

    private fun parseTempC(raw: String?, header: String): Float? {
        val hex = hexPayload(raw) ?: return null
        val idx = evenIndex(hex, header)
        if (idx < 0 || idx + header.length + 2 > hex.length) return null
        return hex.substring(idx + header.length, idx + header.length + 2)
            .toIntOrNull(16)
            ?.minus(40f)
    }

    private fun hexPayload(raw: String?): String? {
        if (raw.isNullOrBlank()) return null
        val upper = raw.uppercase()
        if (upper.contains("NO DATA") || upper.contains("UNABLE")) return null
        return raw.filter { it.isDigit() || it in 'A'..'F' || it in 'a'..'f' }.uppercase()
            .ifBlank { null }
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
