package dev.foss.expeditiongauge.freezeframe

import dev.foss.expeditiongauge.obd.Elm327Protocol
import dev.foss.expeditiongauge.obd.dtc.DtcEntry

data class FreezeFrameSnapshot(
    val dtc: String? = null,
    val rpm: Float? = null,
    val speedKmh: Float? = null,
    val throttlePct: Float? = null,
    val loadPct: Float? = null,
)

object FreezeFrame {
    fun parseDtc(raw: String?): String? {
        val hex = hexPayload(raw) ?: return null
        val idx = evenIndex(hex, "4202")
        if (idx < 0 || idx + 8 > hex.length) return null
        val b1 = hex.substring(idx + 4, idx + 6).toIntOrNull(16) ?: return null
        val b2 = hex.substring(idx + 6, idx + 8).toIntOrNull(16) ?: return null
        if (b1 == 0 && b2 == 0) return null
        return Elm327Protocol.decodeDtcBytes(b1, b2)
    }

    fun parseRpm(raw: String?): Float? {
        val hex = hexPayload(raw) ?: return null
        val idx = evenIndex(hex, "420C")
        if (idx < 0 || idx + 8 > hex.length) return null
        val a = hex.substring(idx + 4, idx + 6).toIntOrNull(16) ?: return null
        val b = hex.substring(idx + 6, idx + 8).toIntOrNull(16) ?: return null
        return ((a * 256 + b) / 4f).takeIf { it in 0f..20_000f }
    }

    fun parseSpeedKmh(raw: String?): Float? = parseByte(raw, "420D")

    fun parsePct(raw: String?, header: String): Float? =
        parseByte(raw, header)?.times(100f / 255f)

    fun summary(snap: FreezeFrameSnapshot): String? {
        val parts = buildList {
            snap.dtc?.let { add(it) }
            snap.rpm?.let { add("${it.toInt()} rpm") }
            snap.speedKmh?.let { add("${it.toInt()} km/h") }
            snap.throttlePct?.let { add("thr ${it.toInt()}%") }
            snap.loadPct?.let { add("load ${it.toInt()}%") }
        }
        return parts.joinToString(" · ").ifBlank { null }
    }

    fun attach(entries: List<DtcEntry>, snap: FreezeFrameSnapshot?): List<DtcEntry> {
        val text = snap?.let { summary(it) } ?: return entries
        val code = snap.dtc
        val match = code != null && entries.any { it.code == code }
        return entries.mapIndexed { i, e ->
            val hit = if (match) e.code == code else i == 0
            if (hit) e.copy(freezeSummary = text) else e
        }
    }

    private fun hexPayload(raw: String?): String? {
        if (raw.isNullOrBlank()) return null
        val upper = raw.uppercase()
        if (upper.contains("NO DATA") || upper.contains("UNABLE") || upper.contains("7F02")) {
            return null
        }
        return Elm327Protocol.normalizeElmHex(raw).ifBlank { null }
    }

    private fun parseByte(raw: String?, header: String): Float? {
        val hex = hexPayload(raw) ?: return null
        val idx = evenIndex(hex, header)
        if (idx < 0 || idx + header.length + 2 > hex.length) return null
        return hex.substring(idx + header.length, idx + header.length + 2)
            .toIntOrNull(16)
            ?.toFloat()
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
