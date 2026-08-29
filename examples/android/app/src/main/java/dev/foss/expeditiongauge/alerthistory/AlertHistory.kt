package dev.foss.expeditiongauge.alerthistory

data class AlertHistoryEntry(
    val type: String,
    val value: Float,
    val threshold: Float,
    val timestampMs: Long,
)

object AlertHistory {
    const val MAX = 50

    fun append(existing: List<AlertHistoryEntry>, next: AlertHistoryEntry): List<AlertHistoryEntry> =
        (listOf(next) + existing).take(MAX)

    fun encode(entries: List<AlertHistoryEntry>): String =
        entries.joinToString(";") { "${it.type}|${it.value}|${it.threshold}|${it.timestampMs}" }

    fun decode(raw: String?): List<AlertHistoryEntry> {
        if (raw.isNullOrBlank()) return emptyList()
        return raw.split(';').mapNotNull { part ->
            val bits = part.split('|')
            if (bits.size != 4) return@mapNotNull null
            AlertHistoryEntry(
                type = bits[0],
                value = bits[1].toFloatOrNull() ?: return@mapNotNull null,
                threshold = bits[2].toFloatOrNull() ?: return@mapNotNull null,
                timestampMs = bits[3].toLongOrNull() ?: return@mapNotNull null,
            )
        }
    }
}
