package dev.foss.expeditiongauge.car.gauge

/** High-contrast colors for AA Drive HUD cubes (dark / light). */
data class DriveHudTheme(
    val background: Int,
    val cubeFill: Int,
    val cubeStroke: Int,
    val primaryText: Int,
    val secondaryText: Int,
    val divider: Int,
    val alertText: Int,
    val dimText: Int,
) {
    companion object {
        fun forDarkMode(dark: Boolean): DriveHudTheme =
            if (dark) DARK else LIGHT

        val DARK = DriveHudTheme(
            background = 0xFF0B0F14.toInt(),
            cubeFill = 0xFF161C24.toInt(),
            cubeStroke = 0xFF3A4654.toInt(),
            primaryText = 0xFFF2F5F8.toInt(),
            // Near-primary so night HDG/alt/coords stay glanceable at distance.
            secondaryText = 0xFFE4EAF0.toInt(),
            divider = 0xFF2A3340.toInt(),
            alertText = 0xFFFF3333.toInt(),
            dimText = 0xFF6A7380.toInt(),
        )

        val LIGHT = DriveHudTheme(
            background = 0xFFE8ECF1.toInt(),
            cubeFill = 0xFFFFFFFF.toInt(),
            cubeStroke = 0xFF9AA6B2.toInt(),
            primaryText = 0xFF101418.toInt(),
            secondaryText = 0xFF3D4752.toInt(),
            divider = 0xFFC5CDD6.toInt(),
            alertText = 0xFFCC2222.toInt(),
            dimText = 0xFF9AA6B2.toInt(),
        )
    }
}
