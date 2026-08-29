package dev.foss.expeditiongauge.ui.theme

import android.app.Activity
import android.view.WindowManager
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.foss.expeditiongauge.colorblind.ColorblindHudMode
import dev.foss.expeditiongauge.nighthud.NightHudPalette
import dev.foss.expeditiongauge.ui.ambient.rememberAmbientLux
import dev.foss.expeditiongauge.settings.ColorblindHudStore
import dev.foss.expeditiongauge.settings.NightHudStore

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
    val view = LocalView.current
    val ambientLux = rememberAmbientLux()
    val nightHudEnabled by remember { NightHudStore(view.context) }.enabled
        .collectAsStateWithLifecycle(true)
    val colorScheme = when {
        highContrastEnabled -> HighContrastExpeditionGaugeColors
        NightHudPalette.active(brightnessMode == BrightnessMode.Night, nightHudEnabled) ->
            NightHudExpeditionGaugeColors
        brightnessMode == BrightnessMode.Day -> DayExpeditionGaugeColors
        brightnessMode == BrightnessMode.Night -> DarkExpeditionGaugeColors
        brightnessMode == BrightnessMode.Auto -> if (darkTheme) DarkExpeditionGaugeColors else LightExpeditionGaugeColors
        else -> if (darkTheme) DarkExpeditionGaugeColors else LightExpeditionGaugeColors
    }

    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.surface.toArgb()
            window.navigationBarColor = colorScheme.surface.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
            val attrs = window.attributes
            attrs.screenBrightness = screenBrightnessFor(brightnessMode, ambientLux)
            window.attributes = attrs
            if (shouldKeepScreenAwake(keepScreenAwake)) {
                window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            } else {
                window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            }
        }
    }

    val colorblindMode by remember { ColorblindHudStore(view.context) }.mode
        .collectAsStateWithLifecycle(ColorblindHudMode.NONE)
    CompositionLocalProvider(
        LocalTextScale provides textScale,
        LocalColorblindHud provides ColorblindHudColors.from(colorblindMode),
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = scaledTypography(textScale),
            content = content,
        )
    }
}
