package dev.foss.expeditiongauge.ui.theme

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import dev.foss.expeditiongauge.colorblind.ColorblindHud
import dev.foss.expeditiongauge.colorblind.ColorblindHudMode

data class ColorblindHudColors(val alertRed: Color, val alertYellow: Color) {
    companion object {
        fun from(mode: ColorblindHudMode) = ColorblindHudColors(
            alertRed = Color(ColorblindHud.alertRed(mode)),
            alertYellow = Color(ColorblindHud.alertYellow(mode)),
        )
    }
}

val LocalColorblindHud = staticCompositionLocalOf {
    ColorblindHudColors(GaugeRed, GaugeYellow)
}
