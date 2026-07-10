package dev.foss.expeditiongauge.sensors

import android.content.Context
import android.hardware.display.DisplayManager
import android.view.Display
import android.view.Surface

/**
 * Live Surface.ROTATION_* from the default display.
 *
 * Prefer [DisplayManager] over Application [android.view.WindowManager] —
 * on some OEMs (observed OnePlus 12) Application WM reports ROTATION_0 while
 * the Activity display is already ROTATION_90.
 */
internal object DisplayRotationReader {
    fun current(context: Context): Int = try {
        val dm = context.getSystemService(Context.DISPLAY_SERVICE) as DisplayManager
        dm.getDisplay(Display.DEFAULT_DISPLAY)?.rotation ?: Surface.ROTATION_0
    } catch (_: Exception) {
        Surface.ROTATION_0
    }
}
