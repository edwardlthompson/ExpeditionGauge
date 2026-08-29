package dev.foss.expeditiongauge.ui.theme

import android.view.WindowManager
import dev.foss.expeditiongauge.ambient.AmbientAutodim

internal fun screenBrightnessFor(mode: BrightnessMode, lux: Float? = null): Float = when (mode) {
    BrightnessMode.Day -> 0.92f
    BrightnessMode.Night -> 0.35f
    BrightnessMode.Auto ->
        AmbientAutodim.brightness(lux) ?: WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE
}
