package dev.foss.expeditiongauge.imreadiness

data class ImMonitor(val name: String, val ready: Boolean)

data class ImReadinessReport(val monitors: List<ImMonitor>) {
    val incomplete: List<String> get() = monitors.filter { !it.ready }.map { it.name }
    val supported: Int get() = monitors.size
    val readyCount: Int get() = monitors.count { it.ready }
}

object ImReadiness {
    private val continuous = listOf("MIS", "FUEL", "CCM")
    private val spark = listOf("CAT", "HCAT", "EVAP", "AIR", "AC", "O2", "HTR", "EGR")
    private val compression = listOf("NMHC", "NOx", "Boost", "", "EGS", "PM", "EGR", "")

    fun parse(raw: String?): ImReadinessReport? {
        val hex = hexPayload(raw) ?: return null
        val idx = evenIndex(hex, "4101")
        if (idx < 0 || idx + 12 > hex.length) return null
        val b = byteAt(hex, idx + 6) ?: return null
        val c = byteAt(hex, idx + 8) ?: return null
        val d = byteAt(hex, idx + 10) ?: return null
        val monitors = mutableListOf<ImMonitor>()
        continuous.forEachIndexed { i, name ->
            if (b and (1 shl i) != 0) {
                monitors += ImMonitor(name, ready = b and (1 shl (i + 4)) == 0)
            }
        }
        val names = if (b and 0x08 != 0) compression else spark
        names.forEachIndexed { i, name ->
            if (name.isNotEmpty() && c and (1 shl i) != 0) {
                monitors += ImMonitor(name, ready = d and (1 shl i) == 0)
            }
        }
        return ImReadinessReport(monitors).takeIf { it.monitors.isNotEmpty() }
    }

    fun line(report: ImReadinessReport?): String? {
        if (report == null) return null
        val bad = report.incomplete
        return if (bad.isEmpty()) {
            "I/M ready (${report.readyCount}/${report.supported})"
        } else {
            val shown = bad.take(3).joinToString(" ")
            val extra = if (bad.size > 3) " +${bad.size - 3}" else ""
            "I/M not ready: $shown$extra"
        }
    }

    private fun hexPayload(raw: String?): String? {
        if (raw.isNullOrBlank()) return null
        val upper = raw.uppercase()
        if (upper.contains("NO DATA") || upper.contains("UNABLE")) return null
        val hex = raw.filter { it.isDigit() || it in 'A'..'F' || it in 'a'..'f' }.uppercase()
        return hex.ifBlank { null }
    }

    private fun byteAt(hex: String, start: Int): Int? =
        hex.substring(start, start + 2).toIntOrNull(16)

    private fun evenIndex(hex: String, token: String): Int {
        var i = 0
        while (i + token.length <= hex.length) {
            if (hex.regionMatches(i, token, 0, token.length)) return i
            i += 2
        }
        return -1
    }
}
