package dev.foss.expeditiongauge.ui.dashboard

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.widget.Toast
import dev.foss.expeditiongauge.R
import dev.foss.expeditiongauge.media.HudScreenshotSaver
import dev.foss.expeditiongauge.settings.HudScreenshotMode

internal fun Context.findActivity(): Activity? {
    var ctx: Context? = this
    while (ctx is ContextWrapper) {
        if (ctx is Activity) return ctx
        ctx = ctx.baseContext
    }
    return null
}

/** Capture using the persisted HUD screenshot mode. */
internal fun Context.captureHudScreenshot(mode: HudScreenshotMode) {
    when (mode) {
        HudScreenshotMode.FULL_SCREEN -> captureHudFullScreen()
        HudScreenshotMode.EACH_CUBE -> captureHudCubesSeparately()
    }
}

internal fun Context.captureHudFullScreen() {
    val activity = findActivity() ?: return
    HudScreenshotSaver.captureToGallery(activity) { result ->
        if (result == HudScreenshotSaver.Result.Busy) return@captureToGallery
        val msg = when (result) {
            HudScreenshotSaver.Result.Success -> activity.getString(R.string.screenshot_saved)
            is HudScreenshotSaver.Result.Partial -> activity.getString(
                R.string.screenshot_cubes_partial,
                result.saved,
                result.total,
            )
            else -> activity.getString(R.string.screenshot_failed)
        }
        Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show()
    }
}

internal fun Context.captureHudCubesSeparately() {
    val activity = findActivity() ?: return
    HudScreenshotSaver.captureEachCubeToGallery(activity) { result ->
        if (result == HudScreenshotSaver.Result.Busy) return@captureEachCubeToGallery
        val msg = when (result) {
            HudScreenshotSaver.Result.Success -> activity.getString(R.string.screenshot_cubes_saved)
            is HudScreenshotSaver.Result.Partial -> activity.getString(
                R.string.screenshot_cubes_partial,
                result.saved,
                result.total,
            )
            else -> activity.getString(R.string.screenshot_failed)
        }
        Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show()
    }
}
