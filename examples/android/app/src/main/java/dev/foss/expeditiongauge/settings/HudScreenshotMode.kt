package dev.foss.expeditiongauge.settings

/** How the HUD camera button captures images. */
enum class HudScreenshotMode {
    /** One image of the full Activity window. */
    FULL_SCREEN,
    /** One square JPEG per HUD cube. */
    EACH_CUBE,
}
