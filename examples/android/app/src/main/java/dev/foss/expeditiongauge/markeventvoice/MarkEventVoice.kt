package dev.foss.expeditiongauge.markeventvoice

/** Attach a local audio URI to a mark-event payload without org.json. */
object MarkEventVoice {
    private const val KEY = "\"audioUri\":\""

    fun audioUri(payloadJson: String?): String? {
        val raw = payloadJson ?: return null
        val start = raw.indexOf(KEY)
        if (start < 0) return null
        val from = start + KEY.length
        val end = raw.indexOf('"', from)
        return if (end > from) raw.substring(from, end) else null
    }

    fun withAudioUri(payloadJson: String?, uri: String): String {
        val clean = uri.trim()
        if (clean.isEmpty()) return payloadJson.orEmpty()
        val base = payloadJson?.trim().orEmpty()
        if (base.isEmpty() || base == "{}") return "{$KEY$clean\"}"
        if (audioUri(base) != null) {
            return base.replace(Regex("\"audioUri\":\"[^\"]*\""), "\"audioUri\":\"$clean\"")
        }
        val trimmed = base.trim().removeSuffix("}")
        val prefix = if (trimmed.endsWith("{")) trimmed else "$trimmed,"
        return "$prefix$KEY$clean\"}"
    }
}
