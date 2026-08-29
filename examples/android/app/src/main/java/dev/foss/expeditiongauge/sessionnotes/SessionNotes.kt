package dev.foss.expeditiongauge.sessionnotes

/** Normalize and match session notes for library search. */
object SessionNotes {
    fun normalize(notes: String?): String? = notes?.trim()?.takeIf { it.isNotEmpty() }

    fun matches(notes: String?, query: String): Boolean {
        val q = query.trim()
        if (q.isEmpty()) return true
        return notes?.contains(q, ignoreCase = true) == true
    }
}
