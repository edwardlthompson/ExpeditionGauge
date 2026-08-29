package dev.foss.expeditiongauge.pidsniffer

object PidSniffer {
    const val MAX_RAW = 120

    fun normalize(command: String): String? {
        val hex = command.filter { it.isDigit() || it in 'A'..'F' || it in 'a'..'f' }.uppercase()
        if (hex.length !in 2..8 || hex.length % 2 != 0) return null
        if (hex == "04" || hex.startsWith("09")) return null
        return hex
    }

    fun sanitize(command: String, raw: String?): String {
        val cmd = normalize(command) ?: return "blocked"
        if (raw.isNullOrBlank()) return "NO DATA"
        val compact = raw.uppercase().filter { it.isLetterOrDigit() }
        if (compact.contains("4902") || cmd.startsWith("09")) return "VIN redacted"
        return raw.replace('\r', ' ').replace('\n', ' ').trim().take(MAX_RAW)
    }
}
