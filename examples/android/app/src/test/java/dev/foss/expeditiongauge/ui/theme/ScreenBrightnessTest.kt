package dev.foss.expeditiongauge.ui.theme

import android.view.WindowManager
import org.junit.Assert.assertEquals
import org.junit.Test

class ScreenBrightnessTest {
    @Test
    fun dayModeUsesHighBrightness() {
        assertEquals(0.92f, screenBrightnessFor(BrightnessMode.Day), 0.001f)
    }

    @Test
    fun nightModeUsesLowBrightness() {
        assertEquals(0.35f, screenBrightnessFor(BrightnessMode.Night), 0.001f)
    }

    @Test
    fun autoModeUsesSystemDefault() {
        assertEquals(
            WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE,
            screenBrightnessFor(BrightnessMode.Auto),
        )
    }

    @Test
    fun autoModeUsesLuxCurveWhenPresent() {
        assertEquals(0.18f, screenBrightnessFor(BrightnessMode.Auto, 0f), 0.001f)
    }
}
