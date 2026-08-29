package dev.foss.expeditiongauge.car.aaparkedlibrary

import dev.foss.expeditiongauge.car.DriveHudRow

/** Parked-only recorded-session list for Android Auto. */
object AaParkedLibrary {
    const val MAX_ROWS = 6
    const val TITLE = "Sessions"
    const val NEED_PARK = "Park to browse sessions"
    const val EMPTY = "No sessions yet"

    fun canOpen(parked: Boolean): Boolean = parked

    fun durationLabel(startMs: Long, endMs: Long?): String {
        val elapsed = ((endMs ?: startMs) - startMs).coerceAtLeast(0L)
        val sec = elapsed / 1000L
        val min = sec / 60L
        val rem = sec % 60L
        return if (min > 0) "${min}m ${rem}s" else "${rem}s"
    }

    fun rows(sessions: List<Triple<String, Long, Long?>>): List<DriveHudRow> =
        sessions.take(MAX_ROWS).map { (name, start, end) ->
            DriveHudRow(name.ifBlank { "Session" }, durationLabel(start, end))
        }
}
