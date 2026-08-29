package dev.foss.expeditiongauge.fordmode22

enum class FordMode22Kind { THROTTLE, TRANS_TEMP, EGT, OTHER }

data class FordMode22Pid(
    val command: String,
    val header: String,
    val label: String,
    val kind: FordMode22Kind,
    val scale: Float = 1f,
    val offset: Float = 0f,
    val bytes: Int = 1,
)

/** Curated U222 / Expedition Mode 22 PIDs. Temps are A−40 °C until row 10 polls. */
object FordMode22Catalog {
    val entries: List<FordMode22Pid> = listOf(
        FordMode22Pid("2209D4", "6209D4", "APP", FordMode22Kind.THROTTLE, scale = 0.5f),
        FordMode22Pid("220911", "620911", "TP", FordMode22Kind.THROTTLE, scale = 100f / 255f),
        FordMode22Pid("221340", "621340", "APP2", FordMode22Kind.THROTTLE, scale = 100f / 255f),
        FordMode22Pid("221E1C", "621E1C", "TFT", FordMode22Kind.TRANS_TEMP, offset = -40f),
        FordMode22Pid("221145", "621145", "TFT2", FordMode22Kind.TRANS_TEMP, offset = -40f),
        FordMode22Pid("2216B5", "6216B5", "EGT", FordMode22Kind.EGT, offset = -40f),
    )

    fun byCommand(command: String): FordMode22Pid? =
        entries.firstOrNull { it.command == command }

    fun byKind(kind: FordMode22Kind): List<FordMode22Pid> =
        entries.filter { it.kind == kind }

    fun line(pid: FordMode22Pid): String = "${pid.command} ${pid.label}"

    fun summary(): String = entries.joinToString(" · ") { line(it) }

    fun parse(raw: String?, pid: FordMode22Pid): Float? {
        val hex = hexPayload(raw) ?: return null
        val idx = evenIndex(hex, pid.header)
        if (idx < 0 || idx + pid.header.length + pid.bytes * 2 > hex.length) return null
        val start = idx + pid.header.length
        val rawInt = hex.substring(start, start + pid.bytes * 2).toIntOrNull(16) ?: return null
        return rawInt * pid.scale + pid.offset
    }

    private fun hexPayload(raw: String?): String? {
        if (raw.isNullOrBlank()) return null
        val upper = raw.uppercase()
        if (upper.contains("NO DATA") || upper.contains("UNABLE") || upper.contains("7F22")) {
            return null
        }
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
