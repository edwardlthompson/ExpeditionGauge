package dev.foss.expeditiongauge.ui.components.gauge

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import dev.foss.expeditiongauge.ui.theme.GaugeScaleWhite
import dev.foss.expeditiongauge.ui.theme.ThemeMode

data class TpmsChipColors(
    val background: Color,
    val foreground: Color,
    val border: Color,
)

@Composable
fun tpmsChipColors(themeMode: ThemeMode? = null): TpmsChipColors {
    val dark = when (themeMode) {
        ThemeMode.Dark -> true
        ThemeMode.Light -> false
        ThemeMode.System, null -> isSystemInDarkTheme()
    } || MaterialTheme.colorScheme.background.luminance() < 0.35f
    return if (dark) {
        TpmsChipColors(
            background = Color.Black,
            foreground = GaugeScaleWhite,
            border = GaugeScaleWhite,
        )
    } else {
        TpmsChipColors(
            background = Color.White,
            foreground = Color.Black,
            border = GaugeScaleWhite.copy(alpha = 0.85f),
        )
    }
}
