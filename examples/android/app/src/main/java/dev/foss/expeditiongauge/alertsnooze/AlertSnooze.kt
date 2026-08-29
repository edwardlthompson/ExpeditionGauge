package dev.foss.expeditiongauge.alertsnooze

import dev.foss.expeditiongauge.alerts.AlertType

object AlertSnooze {
    const val DURATION_MS = 5 * 60 * 1000L

    fun suppressed(untilMs: Long?, nowMs: Long): Boolean = untilMs != null && nowMs < untilMs

    fun untilMs(nowMs: Long, durationMs: Long = DURATION_MS): Long = nowMs + durationMs

    fun encode(untilByType: Map<AlertType, Long>): String =
        untilByType.entries.joinToString(",") { "${it.key.name}:${it.value}" }

    fun decode(raw: String?): Map<AlertType, Long> {
        if (raw.isNullOrBlank()) return emptyMap()
        return raw.split(',').mapNotNull { part ->
            val bits = part.split(':', limit = 2)
            if (bits.size != 2) return@mapNotNull null
            val type = runCatching { AlertType.valueOf(bits[0]) }.getOrNull() ?: return@mapNotNull null
            val until = bits[1].toLongOrNull() ?: return@mapNotNull null
            type to until
        }.toMap()
    }
}
