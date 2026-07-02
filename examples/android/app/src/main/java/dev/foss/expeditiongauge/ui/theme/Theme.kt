package dev.foss.expeditiongauge.ui.theme

import android.app.Activity
import android.view.WindowManager
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

@Composable
fun ExpeditionGaugeTheme(
    themeMode: ThemeMode,
    brightnessMode: BrightnessMode = BrightnessMode.Auto,
    keepScreenAwake: Boolean = true,
    highContrastEnabled: Boolean = false,
    textScale: Float = 1f,
    content: @Composable () -> Unit,
) {
    val systemDark = isSystemInDarkTheme()
    val darkTheme = when (themeMode) {
        ThemeMode.System -> systemDark
        ThemeMode.Light -> false
        ThemeMode.Dark -> true
    }
    val colorScheme = when {
        highContrastEnabled -> HighContrastExpeditionGaugeColors
        brightnessMode == BrightnessMode.Day -> DayExpeditionGaugeColors
        brightnessMode == BrightnessMode.Night -> DarkExpeditionGaugeColors
        brightnessMode == BrightnessMode.Auto -> if (darkTheme) DarkExpeditionGaugeColors else LightExpeditionGaugeColors
        else -> if (darkTheme) DarkExpeditionGaugeColors else LightExpeditionGaugeColors
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.surface.toArgb()
            window.navigationBarColor = colorScheme.surface.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
            val attrs = window.attributes
            attrs.screenBrightness = screenBrightnessFor(brightnessMode)
            window.attributes = attrs
            if (shouldKeepScreenAwake(keepScreenAwake)) {
                window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            } else {
                window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            }
        }
    }

    CompositionLocalProvider(LocalTextScale provides textScale) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = scaledTypography(textScale),
            content = content,
        )
    }
}
