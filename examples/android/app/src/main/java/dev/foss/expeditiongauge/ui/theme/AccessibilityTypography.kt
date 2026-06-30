package dev.foss.expeditiongauge.ui.theme

import androidx.compose.runtime.staticCompositionLocalOf

val LocalTextScale = staticCompositionLocalOf { 1f }

fun scaledTypography(scale: Float) = ExpeditionGaugeTypography.copy(
    titleLarge = ExpeditionGaugeTypography.titleLarge.copy(
        fontSize = ExpeditionGaugeTypography.titleLarge.fontSize * scale,
        lineHeight = ExpeditionGaugeTypography.titleLarge.lineHeight * scale,
    ),
    headlineMedium = ExpeditionGaugeTypography.headlineMedium.copy(
        fontSize = ExpeditionGaugeTypography.headlineMedium.fontSize * scale,
        lineHeight = ExpeditionGaugeTypography.headlineMedium.lineHeight * scale,
    ),
    bodyLarge = ExpeditionGaugeTypography.bodyLarge.copy(
        fontSize = ExpeditionGaugeTypography.bodyLarge.fontSize * scale,
        lineHeight = ExpeditionGaugeTypography.bodyLarge.lineHeight * scale,
    ),
    bodyMedium = ExpeditionGaugeTypography.bodyMedium.copy(
        fontSize = ExpeditionGaugeTypography.bodyMedium.fontSize * scale,
        lineHeight = ExpeditionGaugeTypography.bodyMedium.lineHeight * scale,
    ),
    labelLarge = ExpeditionGaugeTypography.labelLarge.copy(
        fontSize = ExpeditionGaugeTypography.labelLarge.fontSize * scale,
        lineHeight = ExpeditionGaugeTypography.labelLarge.lineHeight * scale,
    ),
)

fun scaledGaugeSpeedTextStyle(scale: Float) = GaugeSpeedTextStyle.copy(
    fontSize = GaugeSpeedTextStyle.fontSize * scale,
    lineHeight = GaugeSpeedTextStyle.lineHeight * scale,
)
