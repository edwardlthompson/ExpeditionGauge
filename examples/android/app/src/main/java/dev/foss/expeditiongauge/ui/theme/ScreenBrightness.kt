package dev.foss.expeditiongauge.ui.theme

import android.view.WindowManager

internal fun screenBrightnessFor(mode: BrightnessMode): Float = when (mode) {
    BrightnessMode.Day -> 0.92f
    BrightnessMode.Night -> 0.35f
    BrightnessMode.Auto -> WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE
}
