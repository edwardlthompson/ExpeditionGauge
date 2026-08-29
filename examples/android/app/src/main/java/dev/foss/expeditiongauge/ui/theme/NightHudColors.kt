package dev.foss.expeditiongauge.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.ui.graphics.Color
import dev.foss.expeditiongauge.nighthud.NightHudPalette

val NightHudExpeditionGaugeColors = darkColorScheme(
    primary = Color(NightHudPalette.AMBER),
    onPrimary = Color(NightHudPalette.BACKGROUND),
    primaryContainer = Color(NightHudPalette.SURFACE),
    onPrimaryContainer = Color(NightHudPalette.AMBER),
    secondary = Color(NightHudPalette.AMBER),
    onSecondary = Color(NightHudPalette.BACKGROUND),
    background = Color(NightHudPalette.BACKGROUND),
    onBackground = Color(NightHudPalette.AMBER),
    surface = Color(NightHudPalette.SURFACE),
    onSurface = Color(NightHudPalette.AMBER),
    surfaceVariant = Color(NightHudPalette.SURFACE),
    onSurfaceVariant = Color(NightHudPalette.AMBER),
    error = Color(NightHudPalette.AMBER),
    onError = Color(NightHudPalette.BACKGROUND),
)
