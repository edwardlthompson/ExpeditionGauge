package dev.foss.expeditiongauge.lastsessionwidget

/** Home-widget copy for the most recent recording. */
object LastSessionLabel {
    const val EMPTY = "No session"

    fun text(name: String?, startMs: Long?): String {
        if (name.isNullOrBlank()) return EMPTY
        return if (startMs == null || startMs <= 0L) name else name
    }
}
