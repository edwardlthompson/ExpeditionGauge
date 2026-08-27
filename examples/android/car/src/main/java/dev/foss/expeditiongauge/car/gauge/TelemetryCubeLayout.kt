package dev.foss.expeditiongauge.car.gauge

/** Band sizes: original-scale HUD text, 2× pedal bar, lat/lon-sized link icons. */
internal data class TelemetryCubeSlots(
    val inset: Float,
    val gap: Float,
    val primarySize: Float,
    val secondarySize: Float,
    val pedalY: Float,
    val pedalH: Float,
    val linkY: Float,
    val linkH: Float,
)

internal object TelemetryCubeLayout {
    /** 2× the 0.02 cube fraction used after the first shrink. */
    const val PEDAL_H_FRAC = 0.04f
    const val PRIMARY_FRAC = 0.13f
    const val SECONDARY_FRAC = 0.11f
    /** Icon box vs lat/lon em; vector ink fills ~80% so 1.25 matches letter height. */
    const val LINK_H_OVER_SECONDARY = 1.25f

    fun compute(size: Int, lineCount: Int): TelemetryCubeSlots {
        val s = size.toFloat().coerceAtLeast(1f)
        val inset = s * 0.035f
        var gap = s * 0.008f
        val pedalH = s * PEDAL_H_FRAC
        val primary = s * PRIMARY_FRAC
        val secondary = s * SECONDARY_FRAC
        val linkH = secondary * LINK_H_OVER_SECONDARY
        val minGap = s * 0.004f
        repeat(16) {
            val pedalY = s - inset - pedalH
            val linkY = pedalY - gap - linkH
            val budget = (linkY - inset - gap).coerceAtLeast(0f)
            if (textBlockH(lineCount, primary, secondary, gap) <= budget) {
                return TelemetryCubeSlots(
                    inset, gap, primary, secondary, pedalY, pedalH, linkY, linkH,
                )
            }
            if (gap > minGap + 0.1f) {
                gap = (gap * 0.85f).coerceAtLeast(minGap)
            } else {
                return TelemetryCubeSlots(
                    inset, gap, primary, secondary, pedalY, pedalH, linkY, linkH,
                )
            }
        }
        val pedalY = s - inset - pedalH
        return TelemetryCubeSlots(
            inset, gap, primary, secondary, pedalY, pedalH, pedalY - gap - linkH, linkH,
        )
    }

    internal fun lineH(textSize: Float): Float = textSize * 1.15f

    internal fun textBlockH(lineCount: Int, primary: Float, secondary: Float, gap: Float): Float {
        if (lineCount <= 0) return 0f
        var h = lineH(primary)
        repeat((lineCount - 1).coerceAtLeast(0)) { h += gap + lineH(secondary) }
        return h
    }
}
