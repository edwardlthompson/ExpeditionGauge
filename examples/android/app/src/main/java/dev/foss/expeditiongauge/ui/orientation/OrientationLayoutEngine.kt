package dev.foss.expeditiongauge.ui.orientation

enum class HudTileMode {
    THREE_TILE,
    TWO_TILE,
}

/**
 * Maps window size (dp) to dashboard HUD layout parameters. Pure logic — no Compose types.
 */
data class OrientationLayoutSpec(
    val isLandscape: Boolean,
    val attitudeGaugeSizeDp: Float,
    val useCompactGps: Boolean,
    val tileMode: HudTileMode,
)

object OrientationLayoutEngine {
    private const val PORTRAIT_THREE_TILE_MIN_HEIGHT_DP = 480f
    private const val LANDSCAPE_THREE_TILE_MIN_WIDTH_DP = 360f

    fun spec(widthDp: Float, heightDp: Float): OrientationLayoutSpec {
        require(widthDp > 0f && heightDp > 0f) { "width and height must be positive" }
        val isLandscape = widthDp > heightDp
        val tileMode = when {
            isLandscape && widthDp >= LANDSCAPE_THREE_TILE_MIN_WIDTH_DP -> HudTileMode.THREE_TILE
            !isLandscape && heightDp >= PORTRAIT_THREE_TILE_MIN_HEIGHT_DP -> HudTileMode.THREE_TILE
            else -> HudTileMode.TWO_TILE
        }
        return if (isLandscape) {
            OrientationLayoutSpec(
                isLandscape = true,
                attitudeGaugeSizeDp = 180f,
                useCompactGps = false,
                tileMode = tileMode,
            )
        } else {
            OrientationLayoutSpec(
                isLandscape = false,
                attitudeGaugeSizeDp = 148f,
                useCompactGps = false,
                tileMode = tileMode,
            )
        }
    }
}
