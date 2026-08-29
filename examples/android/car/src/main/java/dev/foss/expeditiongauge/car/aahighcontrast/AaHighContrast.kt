package dev.foss.expeditiongauge.car.aahighcontrast

import android.content.Context
import android.provider.Settings
import dev.foss.expeditiongauge.car.gauge.DriveHudTheme

/** AA Drive HUD palettes when high-contrast is on (phone pref or system). */
object AaHighContrast {
    const val SECURE_KEY = "high_text_contrast_enabled"

    fun fromCarUi(context: Context): Boolean =
        runCatching {
            Settings.Secure.getInt(context.contentResolver, SECURE_KEY, 0) == 1
        }.getOrDefault(false)

    fun theme(dark: Boolean, textScale: Float = 1f, enabled: Boolean = false): DriveHudTheme {
        val base = when {
            enabled && dark -> DARK
            enabled -> LIGHT
            dark -> DriveHudTheme.DARK
            else -> DriveHudTheme.LIGHT
        }
        return if (textScale == 1f) base else base.copy(textScale = textScale)
    }

    val DARK = DriveHudTheme(
        background = 0xFF000000.toInt(),
        cubeFill = 0xFF000000.toInt(),
        cubeStroke = 0xFFFFFFFF.toInt(),
        primaryText = 0xFFFFFFFF.toInt(),
        secondaryText = 0xFFFFFF00.toInt(),
        divider = 0xFFFFFFFF.toInt(),
        alertText = 0xFFFF0000.toInt(),
        dimText = 0xFFE0E0E0.toInt(),
    )

    val LIGHT = DriveHudTheme(
        background = 0xFFFFFFFF.toInt(),
        cubeFill = 0xFFFFFFFF.toInt(),
        cubeStroke = 0xFF000000.toInt(),
        primaryText = 0xFF000000.toInt(),
        secondaryText = 0xFF000000.toInt(),
        divider = 0xFF000000.toInt(),
        alertText = 0xFFCC0000.toInt(),
        dimText = 0xFF222222.toInt(),
    )
}
