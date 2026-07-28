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
    /** Edge length of one HUD cube (px). Pane image is 3×1 cubes. */
    val cubeSizePx: Int
        get() = (CUBE_TARGET_DP * density).toInt().coerceIn(MIN_CUBE_PX, MAX_CUBE_PX)

    val paneWidthPx: Int
        get() = cubeSizePx * CUBE_COUNT

    /** Native 3×1 strip height (one cube). */
    val paneHeightPx: Int
        get() = cubeSizePx

    /** Pane fallback square edge (= strip width). */
    val paneBitmapSizePx: Int
        get() = paneWidthPx

    val bitmapSizePx: Int
        get() = cubeSizePx

    companion object {
        const val PORTRAIT_ATTITUDE_DP = 148f
        const val LANDSCAPE_ATTITUDE_DP = 180f
        /** Per-cube edge so 3×1 width ≈ Pane 480 dp guidance box. */
        const val CUBE_TARGET_DP = 160f
        const val CUBE_COUNT = 3
        const val ULTRA_WIDE_RATIO = 2.0f
        const val DEFAULT_GRID_LIMIT = 3
        const val MIN_BITMAP_PX = 96
        const val MIN_CUBE_PX = 120
        /** Pane / Grid cube cap (host image slots stay modest). */
        const val MAX_CUBE_PX = 320
        /**
         * Surface HUD cube cap — match host visible height so we do not upscale a
         * 320 px strip across a 720+ px Surface (looks pixelated).
         */
        const val MAX_SURFACE_CUBE_PX = 720
        const val MAX_BITMAP_PX = 256
        /** Max width for 3×1 pane (3 × MAX_CUBE_PX). */
        const val MAX_PANE_BITMAP_PX = MAX_CUBE_PX * CUBE_COUNT

        /**
         * Cube edge for Surface paint. [ROW] prefers visible height (3×1 strip);
         * [COLUMN] prefers `min(w, h/2)` so two stacked cubes fill a tall pane.
         * Floor at [MAX_CUBE_PX]; empty rect → [DEFAULT.cubeSizePx].
         */
        fun surfaceCubePx(visibleW: Int, visibleH: Int): Int =
            surfaceCubePx(
                visibleW,
                visibleH,
                HudStripOrientation.stable(visibleW, visibleH, HudStripOrientation.ROW),
            )

        fun surfaceCubePx(
            visibleW: Int,
            visibleH: Int,
            orientation: HudStripOrientation,
        ): Int {
            if (visibleW <= 0 || visibleH <= 0) return DEFAULT.cubeSizePx
            val edge = when (orientation) {
                // Reserve ~18% for permanent ROW DTC footer so cube+footer fits visibleH.
                HudStripOrientation.ROW -> (visibleH * 100) / 118
                HudStripOrientation.COLUMN -> minOf(visibleW, visibleH / 2)
            }
            // 1.5× supersample — FILTER downscale looks sharper than 1:1.
            val preferred = maxOf(edge, (edge * 3) / 2)
            return preferred.coerceIn(MAX_CUBE_PX, MAX_SURFACE_CUBE_PX)
        }

        /** @deprecated use [CUBE_TARGET_DP] — kept for older tests/docs. */
        const val PANE_TARGET_DP = 480f

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
