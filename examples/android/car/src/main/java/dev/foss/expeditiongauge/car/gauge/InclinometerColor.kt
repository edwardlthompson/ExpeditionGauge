package dev.foss.expeditiongauge.car.gauge

/**
 * Progressive green → yellow → red by angle magnitude (same curve as phone [GmeterBallColor]).
 */
object InclinometerColor {
    const val MAX_DEG = 45f

    private const val GREEN = 0xFF33FF33.toInt()
    private const val YELLOW = 0xFFFFDD00.toInt()
    private const val RED = 0xFFFF3333.toInt()
    const val BACKGROUND = 0xFF000000.toInt()
    const val BACKGROUND_LIGHT = 0xFFF0F0F0.toInt()
    const val SCALE_TICK = 0x99FFFFFF.toInt()
    const val SCALE_RAIL = 0x55FFFFFF.toInt()
    const val SEGMENT_DIM = 0xFF1A1A1A.toInt()
    const val READOUT = 0xFFFFFFFF.toInt()
    const val POINTER = 0xFFFF3333.toInt()
    const val THRESHOLD_MARKER = 0xAAFFFF00.toInt()
    const val ALERT_FRAME = 0xFFFF3333.toInt()

    fun argbForAngleMagnitude(magnitudeDeg: Float): Int =
        argbForNormalized((kotlin.math.abs(magnitudeDeg) / MAX_DEG).coerceIn(0f, 1f))

    fun argbForNormalized(normalized: Float): Int {
        val d = normalized.coerceIn(0f, 1f)
        return when {
            d <= 0.5f -> lerpArgb(GREEN, YELLOW, d / 0.5f)
            else -> lerpArgb(YELLOW, RED, (d - 0.5f) / 0.5f)
        }
    }

    private fun lerpArgb(from: Int, to: Int, t: Float): Int {
        val clamped = t.coerceIn(0f, 1f)
        val a = ((from ushr 24) and 0xFF) + (((to ushr 24) and 0xFF) - ((from ushr 24) and 0xFF)) * clamped
        val r = ((from shr 16) and 0xFF) + (((to shr 16) and 0xFF) - ((from shr 16) and 0xFF)) * clamped
        val g = ((from shr 8) and 0xFF) + (((to shr 8) and 0xFF) - ((from shr 8) and 0xFF)) * clamped
        val b = (from and 0xFF) + ((to and 0xFF) - (from and 0xFF)) * clamped
        return (a.toInt() shl 24) or (r.toInt() shl 16) or (g.toInt() shl 8) or b.toInt()
    }
}
