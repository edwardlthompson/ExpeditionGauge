package dev.foss.expeditiongauge.car.gauge

/** Seven equal rows: speed, HDG, elev, lat, lon, links, pedal. */
internal data class TelemetryCubeSlots(
    val inset: Float,
    val rowH: Float,
    val textSize: Float,
    val iconSize: Float,
    val pedalH: Float,
) {
    fun rowTop(index: Int): Float = inset + index * rowH
}

internal object TelemetryCubeLayout {
    const val ROW_COUNT = 7
    const val SPEED_ROW = 0
    const val HEADING_ROW = 1
    const val ELEV_ROW = 2
    const val LAT_ROW = 3
    const val LON_ROW = 4
    const val LINK_ROW = 5
    const val PEDAL_ROW = 6

    /** Glyph box vs row so content stays inside its band. */
    const val CONTENT_IN_ROW = 0.72f
    /** Pedal track vs row (~4 px thicker than the old 0.04 cube fraction). */
    const val PEDAL_IN_ROW = 0.42f

    fun compute(size: Int, textScale: Float = 1f): TelemetryCubeSlots {
        val s = size.toFloat().coerceAtLeast(1f)
        val inset = s * 0.035f
        val rowH = ((s - inset * 2f) / ROW_COUNT).coerceAtLeast(1f)
        val scale = textScale.coerceIn(1f, 1.5f)
        return TelemetryCubeSlots(
            inset = inset,
            rowH = rowH,
            textSize = (rowH * CONTENT_IN_ROW * scale).coerceAtMost(rowH * 0.92f),
            iconSize = (rowH * CONTENT_IN_ROW * scale).coerceAtMost(rowH * 0.92f),
            pedalH = rowH * PEDAL_IN_ROW,
        )
    }
}
