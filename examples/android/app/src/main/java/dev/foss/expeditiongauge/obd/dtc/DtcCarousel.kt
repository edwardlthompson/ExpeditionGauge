package dev.foss.expeditiongauge.obd.dtc

/** One carousel frame for the AA ROW DTC footer. */
data class DtcCarouselFrame(
    val index: Int,
    val count: Int,
    val label: String,
    val code: String,
    val description: String,
) {
    /** Full single-line payload before paint truncation. */
    fun line(): String = "$label  $code $description"
}

/**
 * Pure 5 s dwell carousel over stored DTCs. Empty list → null (hide footer).
 * Advance on wall / elapsed clock (`nowMs`); AA Surface ~30 Hz paint stays accurate.
 */
object DtcCarousel {
    const val DWELL_MS: Long = 5_000L

    fun frame(entries: List<DtcEntry>, nowMs: Long): DtcCarouselFrame? {
        if (entries.isEmpty()) return null
        val count = entries.size
        val safeNow = if (nowMs < 0L) 0L else nowMs
        val index = ((safeNow / DWELL_MS) % count).toInt()
        val entry = entries[index]
        return DtcCarouselFrame(
            index = index,
            count = count,
            label = "${index + 1}/$count",
            code = entry.code,
            description = entry.description,
        )
    }

    /**
     * Single-line ellipsis truncate when [measure] width exceeds [maxWidth].
     * [measure] is typically `{ paint.measureText(it) }`.
     */
    fun truncateEllipsis(
        text: String,
        maxWidth: Float,
        measure: (String) -> Float,
    ): String {
        if (maxWidth <= 0f || text.isEmpty()) return ""
        if (measure(text) <= maxWidth) return text
        val ellipsis = "…"
        if (measure(ellipsis) > maxWidth) return ""
        var lo = 0
        var hi = text.length
        while (lo < hi) {
            val mid = (lo + hi + 1) / 2
            if (measure(text.take(mid) + ellipsis) <= maxWidth) lo = mid else hi = mid - 1
        }
        return if (lo <= 0) ellipsis else text.take(lo) + ellipsis
    }
}
