package dev.foss.expeditiongauge.multiecu

data class MultiEcuHeader(val id: String, val label: String)

object MultiEcuHeaders {
    const val FUNCTIONAL = "7DF"

    val entries: List<MultiEcuHeader> = listOf(
        MultiEcuHeader(FUNCTIONAL, "Functional"),
        MultiEcuHeader("7E0", "ECM"),
        MultiEcuHeader("7E1", "TCM"),
        MultiEcuHeader("7E2", "ABS"),
        MultiEcuHeader("7E3", "4WD"),
    )

    fun atsh(id: String): String = "ATSH$id"

    fun present(raw: String?): Boolean {
        if (raw.isNullOrBlank()) return false
        val upper = raw.uppercase()
        if (upper.contains("NO DATA") || upper.contains("UNABLE") || upper.contains("7F01")) {
            return false
        }
        return upper.filter { it.isDigit() || it in 'A'..'F' }.contains("4100")
    }

    fun line(ids: List<String>?): String? {
        val shown = ids.orEmpty().filter { it != FUNCTIONAL }
        if (shown.isEmpty()) return null
        return "ECU " + shown.joinToString(" · ")
    }

    fun summary(): String = entries.joinToString(" · ") { "${it.id} ${it.label}" }

    fun matches(text: String): Boolean = text.startsWith("ECU")
}
