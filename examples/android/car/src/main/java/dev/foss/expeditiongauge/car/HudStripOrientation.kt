package dev.foss.expeditiongauge.car

/** Surface Drive HUD strip layout: wide 3×1 row vs tall 1×2 column (no TPMS). */
enum class HudStripOrientation {
    ROW,
    COLUMN,
    ;

    companion object {
        /** Enter COLUMN only when clearly taller than wide. */
        const val ENTER_COLUMN_RATIO = 1.15f
        /** Leave COLUMN only when no longer taller (hysteresis band). */
        const val LEAVE_COLUMN_RATIO = 0.95f

        fun cubeCount(orientation: HudStripOrientation): Int = when (orientation) {
            ROW -> AaDisplaySpec.CUBE_COUNT
            COLUMN -> 2
        }

        /**
         * Stable orientation with hysteresis. Empty / non-positive size → [ROW].
         * Enter COLUMN when `h > w * ENTER_COLUMN_RATIO`; leave when `h < w * LEAVE_COLUMN_RATIO`.
         */
        fun stable(
            visibleW: Int,
            visibleH: Int,
            previous: HudStripOrientation = ROW,
        ): HudStripOrientation {
            if (visibleW <= 0 || visibleH <= 0) return ROW
            val ratio = visibleH.toFloat() / visibleW.toFloat()
            return when (previous) {
                COLUMN -> if (ratio < LEAVE_COLUMN_RATIO) ROW else COLUMN
                ROW -> if (ratio > ENTER_COLUMN_RATIO) COLUMN else ROW
            }
        }
    }
}
