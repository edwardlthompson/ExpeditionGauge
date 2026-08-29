package dev.foss.expeditiongauge.settingsjsonbackup

/** Pipe-encoded settings snapshot. No org.json (Robolectric-safe). */
object SettingsJsonBackup {
    val allowed = setOf(
        "speed_unit",
        "log_interval_ms",
        "live_telemetry",
        "pressure_unit",
        "temp_unit",
        "coord_format_decimal",
    )

    fun encode(pairs: Map<String, String>): String =
        pairs.filterKeys { it in allowed }
            .entries
            .sortedBy { it.key }
            .joinToString("|") { "${escape(it.key)}=${escape(it.value)}" }

    fun decode(blob: String): Map<String, String> {
        if (blob.isBlank()) return emptyMap()
        val out = linkedMapOf<String, String>()
        for (part in split(blob, '|')) {
            val eq = part.indexOf('=')
            if (eq <= 0) continue
            val key = unescape(part.substring(0, eq))
            if (key !in allowed) continue
            out[key] = unescape(part.substring(eq + 1))
        }
        return out
    }

    private fun escape(raw: String): String =
        raw.replace("\\", "\\\\").replace("|", "\\|").replace("=", "\\=")

    private fun unescape(raw: String): String {
        val out = StringBuilder()
        var i = 0
        while (i < raw.length) {
            if (raw[i] == '\\' && i + 1 < raw.length) {
                out.append(raw[i + 1])
                i += 2
            } else {
                out.append(raw[i])
                i += 1
            }
        }
        return out.toString()
    }

    private fun split(raw: String, sep: Char): List<String> {
        val parts = mutableListOf<String>()
        val buf = StringBuilder()
        var i = 0
        while (i < raw.length) {
            if (raw[i] == '\\' && i + 1 < raw.length) {
                buf.append(raw[i]).append(raw[i + 1])
                i += 2
                continue
            }
            if (raw[i] == sep) {
                parts.add(buf.toString())
                buf.clear()
            } else {
                buf.append(raw[i])
            }
            i += 1
        }
        parts.add(buf.toString())
        return parts
    }
}
