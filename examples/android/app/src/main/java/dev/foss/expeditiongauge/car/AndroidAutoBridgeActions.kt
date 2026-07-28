package dev.foss.expeditiongauge.car

import android.content.Context
import android.graphics.Bitmap
import dev.foss.expeditiongauge.media.HudScreenshotIo

/** Thin action wrappers for [AndroidAutoBridge] mutators / screenshot. */
internal object AndroidAutoBridgeActions {
    fun captureScreenshot(
        bitmap: Bitmap?,
        appContext: Context,
        toast: ((String) -> Unit)?,
    ): Boolean {
        val bmp = bitmap ?: run {
            toast?.invoke("Screenshot unavailable")
            return false
        }
        val ok = runCatching {
            HudScreenshotIo.insertBitmap(appContext, bmp, suffix = "_AA")
        }.getOrDefault(false)
        toast?.invoke(if (ok) "Screenshot saved" else "Screenshot failed")
        return ok
    }
}
