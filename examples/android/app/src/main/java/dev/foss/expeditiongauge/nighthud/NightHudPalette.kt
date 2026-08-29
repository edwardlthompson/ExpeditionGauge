package dev.foss.expeditiongauge.nighthud

object NightHudPalette {
    const val AMBER = 0xFFE09B3D
    const val BACKGROUND = 0xFF000000
    const val SURFACE = 0xFF080808

    fun active(nightBrightness: Boolean, enabled: Boolean): Boolean = nightBrightness && enabled
}
