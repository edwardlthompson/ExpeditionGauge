package dev.foss.expeditiongauge.dualdashcam

data class DashcamClip(
    val uri: String,
    val offsetMs: Long = 0L,
)

/** Encode extra dashcam files as uri|offset pairs. */
object DualDashcam {
    fun parse(raw: String?): List<DashcamClip> =
        raw?.split(';')?.mapNotNull { part ->
            val trimmed = part.trim()
            if (trimmed.isEmpty()) return@mapNotNull null
            val bits = trimmed.split('|', limit = 2)
            val uri = bits[0].trim()
            if (uri.isEmpty()) return@mapNotNull null
            DashcamClip(uri, bits.getOrNull(1)?.toLongOrNull() ?: 0L)
        }.orEmpty()

    fun encode(clips: List<DashcamClip>): String =
        clips.filter { it.uri.isNotBlank() }.joinToString(";") { "${it.uri}|${it.offsetMs}" }

    fun plus(existing: String?, uri: String, offsetMs: Long = 0L): String =
        encode(parse(existing) + DashcamClip(uri, offsetMs))
}
