package dev.foss.expeditiongauge.ui.orientation

/**
 * Maps window size (dp) to dashboard HUD layout parameters. Pure logic — no Compose types.
 */
data class OrientationLayoutSpec(
    val isLandscape: Boolean,
    val attitudeGaugeSizeDp: Float,
    val speedometerGaugeSizeDp: Float,
    val useCompactGps: Boolean,
)

object OrientationLayoutEngine {
    fun spec(widthDp: Float, heightDp: Float): OrientationLayoutSpec {
        require(widthDp > 0f && heightDp > 0f) { "width and height must be positive" }
        val isLandscape = widthDp > heightDp
        return if (isLandscape) {
            OrientationLayoutSpec(
                isLandscape = true,
                attitudeGaugeSizeDp = 180f,
                speedometerGaugeSizeDp = 160f,
                useCompactGps = false,
            )
        } else {
            OrientationLayoutSpec(
                isLandscape = false,
                attitudeGaugeSizeDp = 132f,
                speedometerGaugeSizeDp = 120f,
                useCompactGps = true,
            )
        }
    }
}
