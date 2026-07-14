package dev.foss.expeditiongauge.car

/**
 * Head-unit display geometry for Android Auto layout.
 *
 * Independent of phone [android.view.Display] rotation — phone may be portrait
 * while the HU is landscape (and vice versa). Attitude numbers stay vehicle-frame;
 * only bitmap size / tile budget come from this spec.
 */
data class AaDisplaySpec(
    val isLandscape: Boolean,
    val attitudeSizeDp: Float,
    val density: Float,
    val maxGridItems: Int,
    val isDarkMode: Boolean,
    val isUltraWide: Boolean,
) {
    val bitmapSizePx: Int
        get() = (attitudeSizeDp * density).toInt().coerceIn(MIN_BITMAP_PX, MAX_BITMAP_PX)

    companion object {
        const val PORTRAIT_ATTITUDE_DP = 148f
        const val LANDSCAPE_ATTITUDE_DP = 180f
        const val ULTRA_WIDE_RATIO = 2.0f
        const val DEFAULT_GRID_LIMIT = 3
        const val MIN_BITMAP_PX = 96
        const val MAX_BITMAP_PX = 512

        val DEFAULT = AaDisplaySpec(
            isLandscape = true,
            attitudeSizeDp = LANDSCAPE_ATTITUDE_DP,
            density = 2f,
            maxGridItems = DEFAULT_GRID_LIMIT,
            isDarkMode = true,
            isUltraWide = false,
        )

        fun from(
            widthDp: Int,
            heightDp: Int,
            density: Float,
            maxGridItems: Int = DEFAULT_GRID_LIMIT,
            isDarkMode: Boolean = true,
        ): AaDisplaySpec {
            val w = widthDp.coerceAtLeast(1)
            val h = heightDp.coerceAtLeast(1)
            val isLandscape = w > h
            val isUltraWide = isLandscape && w.toFloat() / h.toFloat() >= ULTRA_WIDE_RATIO
            val attitudeSizeDp = if (isLandscape) LANDSCAPE_ATTITUDE_DP else PORTRAIT_ATTITUDE_DP
            return AaDisplaySpec(
                isLandscape = isLandscape,
                attitudeSizeDp = attitudeSizeDp,
                density = density.coerceAtLeast(0.5f),
                maxGridItems = maxGridItems.coerceAtLeast(1),
                isDarkMode = isDarkMode,
                isUltraWide = isUltraWide,
            )
        }
    }
}
