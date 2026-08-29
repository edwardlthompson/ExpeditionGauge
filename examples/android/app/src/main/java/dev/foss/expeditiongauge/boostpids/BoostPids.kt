package dev.foss.expeditiongauge.boostpids

import java.util.Locale

data class BoostPidSnapshot(
    val mapKpa: Float? = null,
    val afr: Float? = null,
    val boostKpa: Float? = null,
)

object BoostPids {
    const val STOICH_AFR = 14.7f

    fun parseMapKpa(raw: String?): Float? = parseU8(raw, "410B")

    fun parseBaroKpa(raw: String?): Float? = parseU8(raw, "4133")

    fun parseAfr(raw: String?): Float? {
        val lambda = parseU16(raw, "4134") ?: return null
        return (lambda / 32768f) * STOICH_AFR
    }

    fun boostKpa(mapKpa: Float?, baroKpa: Float?): Float? {
        if (mapKpa == null || baroKpa == null) return null
        return (mapKpa - baroKpa).takeIf { it > 0.5f }
    }

    fun line(snap: BoostPidSnapshot?): String? {
        if (snap == null) return null
        val parts = buildList {
            snap.mapKpa?.let { add("MAP ${it.toInt()} kPa") }
            snap.afr?.let { add("AFR ${"%.1f".format(Locale.US, it)}") }
            snap.boostKpa?.let { add("Boost ${it.toInt()} kPa") }
        }
        return parts.joinToString(" · ").ifBlank { null }
    }

    private fun hexPayload(raw: String?): String? {
        if (raw.isNullOrBlank()) return null
        val upper = raw.uppercase()
        if (upper.contains("NO DATA") || upper.contains("UNABLE")) return null
        return raw.filter { it.isDigit() || it in 'A'..'F' || it in 'a'..'f' }.uppercase()
            .ifBlank { null }
    }

    private fun parseU8(raw: String?, header: String): Float? {
        val hex = hexPayload(raw) ?: return null
        val idx = evenIndex(hex, header)
        if (idx < 0 || idx + header.length + 2 > hex.length) return null
        return hex.substring(idx + header.length, idx + header.length + 2).toIntOrNull(16)?.toFloat()
    }

    private fun parseU16(raw: String?, header: String): Float? {
        val hex = hexPayload(raw) ?: return null
        val idx = evenIndex(hex, header)
        if (idx < 0 || idx + header.length + 4 > hex.length) return null
        return hex.substring(idx + header.length, idx + header.length + 4).toIntOrNull(16)?.toFloat()
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
