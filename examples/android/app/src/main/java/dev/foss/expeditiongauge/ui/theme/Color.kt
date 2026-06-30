// GENERATED — do not edit; run scripts/sync-design-tokens.py
// source-hash: 2110b67bfb84
package dev.foss.expeditiongauge.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

// Raw palette
private val GpLightPrimary = Color(0xFFB3263E)
private val GpDarkPrimary = Color(0xFFE94560)
private val GpLightOnPrimary = Color(0xFFFFFFFF)
private val GpDarkOnPrimary = Color(0xFFFFFFFF)
private val GpLightPrimaryContainer = Color(0xFFFFDAD6)
private val GpDarkPrimaryContainer = Color(0xFF8B1A35)
private val GpLightOnPrimaryContainer = Color(0xFF410009)
private val GpDarkOnPrimaryContainer = Color(0xFFFFDAD6)
private val GpLightSecondary = Color(0xFF4A5F82)
private val GpDarkSecondary = Color(0xFF9BB8E8)
private val GpLightOnSecondary = Color(0xFFFFFFFF)
private val GpDarkOnSecondary = Color(0xFF1A1A2E)
private val GpLightSecondaryContainer = Color(0xFFD8E3FF)
private val GpDarkSecondaryContainer = Color(0xFF2A3A5C)
private val GpLightOnSecondaryContainer = Color(0xFF1A1A2E)
private val GpDarkOnSecondaryContainer = Color(0xFFD8E3FF)
private val GpLightTertiary = Color(0xFF6B5778)
private val GpDarkTertiary = Color(0xFFD4BEE8)
private val GpLightOnTertiary = Color(0xFFFFFFFF)
private val GpDarkOnTertiary = Color(0xFF1A1A2E)
private val GpLightError = Color(0xFFBA1A1A)
private val GpDarkError = Color(0xFFFFB4AB)
private val GpLightOnError = Color(0xFFFFFFFF)
private val GpDarkOnError = Color(0xFF690005)
private val GpLightBackground = Color(0xFFFFFFFF)
private val GpDarkBackground = Color(0xFF12121F)
private val GpLightOnBackground = Color(0xFF1A1A2E)
private val GpDarkOnBackground = Color(0xFFE6E6F0)
private val GpLightSurface = Color(0xFFF5F5FA)
private val GpDarkSurface = Color(0xFF1A1A2E)
private val GpLightOnSurface = Color(0xFF1A1A2E)
private val GpDarkOnSurface = Color(0xFFE6E6F0)
private val GpLightSurfaceVariant = Color(0xFFE2E2EC)
private val GpDarkSurfaceVariant = Color(0xFF2A2A42)
private val GpLightOnSurfaceVariant = Color(0xFF45465A)
private val GpDarkOnSurfaceVariant = Color(0xFFC4C4D4)
private val GpLightOutline = Color(0xFF767680)
private val GpDarkOutline = Color(0xFF8E8EA0)

val LightExpeditionGaugeColors = lightColorScheme(
    primary = GpLightPrimary,
    onPrimary = GpLightOnPrimary,
    primaryContainer = GpLightPrimaryContainer,
    onPrimaryContainer = GpLightOnPrimaryContainer,
    secondary = GpLightSecondary,
    onSecondary = GpLightOnSecondary,
    secondaryContainer = GpLightSecondaryContainer,
    onSecondaryContainer = GpLightOnSecondaryContainer,
    tertiary = GpLightTertiary,
    onTertiary = GpLightOnTertiary,
    error = GpLightError,
    onError = GpLightOnError,
    background = GpLightBackground,
    onBackground = GpLightOnBackground,
    surface = GpLightSurface,
    onSurface = GpLightOnSurface,
    surfaceVariant = GpLightSurfaceVariant,
    onSurfaceVariant = GpLightOnSurfaceVariant,
    outline = GpLightOutline,
)

val DarkExpeditionGaugeColors = darkColorScheme(
    primary = GpDarkPrimary,
    onPrimary = GpDarkOnPrimary,
    primaryContainer = GpDarkPrimaryContainer,
    onPrimaryContainer = GpDarkOnPrimaryContainer,
    secondary = GpDarkSecondary,
    onSecondary = GpDarkOnSecondary,
    secondaryContainer = GpDarkSecondaryContainer,
    onSecondaryContainer = GpDarkOnSecondaryContainer,
    tertiary = GpDarkTertiary,
    onTertiary = GpDarkOnTertiary,
    error = GpDarkError,
    onError = GpDarkOnError,
    background = GpDarkBackground,
    onBackground = GpDarkOnBackground,
    surface = GpDarkSurface,
    onSurface = GpDarkOnSurface,
    surfaceVariant = GpDarkSurfaceVariant,
    onSurfaceVariant = GpDarkOnSurfaceVariant,
    outline = GpDarkOutline,
)

// Manual automotive gauge tokens (not synced from design-tokens.json)
val GaugeBackground = Color(0xFF000000)
val GaugeScaleWhite = Color(0xFFFFFFFF)
val GaugeGreen = Color(0xFF33CC33)
val GaugeYellow = Color(0xFFFFCC00)
val GaugeRed = Color(0xFFFF3333)
val GaugeBall = Color(0xFFFFFFFF)

// High-contrast accessibility palette (Sprint 17)
val HighContrastExpeditionGaugeColors = darkColorScheme(
    primary = Color(0xFFFFFFFF),
    onPrimary = Color(0xFF000000),
    primaryContainer = Color(0xFF000000),
    onPrimaryContainer = Color(0xFFFFFFFF),
    secondary = Color(0xFFFFFF00),
    onSecondary = Color(0xFF000000),
    secondaryContainer = Color(0xFF000000),
    onSecondaryContainer = Color(0xFFFFFF00),
    tertiary = Color(0xFFFFFFFF),
    onTertiary = Color(0xFF000000),
    error = Color(0xFFFF0000),
    onError = Color(0xFFFFFFFF),
    background = Color(0xFF000000),
    onBackground = Color(0xFFFFFFFF),
    surface = Color(0xFF000000),
    onSurface = Color(0xFFFFFFFF),
    surfaceVariant = Color(0xFF1A1A1A),
    onSurfaceVariant = Color(0xFFFFFFFF),
    outline = Color(0xFFFFFFFF),
)

// Day brightness: higher contrast for outdoor readability
val DayExpeditionGaugeColors = darkColorScheme(
    primary = GpDarkPrimary,
    onPrimary = GpDarkOnPrimary,
    primaryContainer = GpDarkPrimaryContainer,
    onPrimaryContainer = GpDarkOnPrimaryContainer,
    secondary = Color(0xFFB8D4FF),
    onSecondary = Color(0xFF000000),
    secondaryContainer = GpDarkSecondaryContainer,
    onSecondaryContainer = GpDarkOnSecondaryContainer,
    tertiary = GpDarkTertiary,
    onTertiary = GpDarkOnTertiary,
    error = GpDarkError,
    onError = GpDarkOnError,
    background = Color(0xFF000000),
    onBackground = Color(0xFFFFFFFF),
    surface = Color(0xFF0A0A0A),
    onSurface = Color(0xFFFFFFFF),
    surfaceVariant = GpDarkSurfaceVariant,
    onSurfaceVariant = Color(0xFFE0E0E0),
    outline = Color(0xFFCCCCCC),
)
