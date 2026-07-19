package dev.foss.expeditiongauge.media

import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Rect
import android.os.Build
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.view.PixelCopy
import android.view.Window
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

internal object HudScreenshotIo {
    fun clampRect(src: Rect?, winW: Int, winH: Int): Rect? {
        if (src == null || winW <= 0 || winH <= 0) return null
        return Rect(
            src.left.coerceIn(0, winW - 1),
            src.top.coerceIn(0, winH - 1),
            src.right.coerceIn(1, winW),
            src.bottom.coerceIn(1, winH),
        ).takeIf { it.width() > 8 && it.height() > 8 }
    }

    fun toSquare(src: Rect): Rect {
        val edge = minOf(src.width(), src.height())
        val left = src.left + (src.width() - edge) / 2
        val top = src.top + (src.height() - edge) / 2
        return Rect(left, top, left + edge, top + edge)
    }

    fun copyRect(window: Window, src: Rect, onDone: (Bitmap?) -> Unit) {
        val bitmap = Bitmap.createBitmap(src.width(), src.height(), Bitmap.Config.ARGB_8888)
        try {
            val handler = Handler(Looper.getMainLooper())
            PixelCopy.request(window, src, bitmap, { copyResult ->
                if (copyResult == PixelCopy.SUCCESS) onDone(bitmap) else {
                    bitmap.recycle()
                    onDone(null)
                }
            }, handler)
        } catch (_: Exception) {
            if (!bitmap.isRecycled) bitmap.recycle()
            onDone(null)
        }
    }

    fun insertBitmap(
        activity: Activity,
        bitmap: Bitmap,
        suffix: String?,
        stamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date()),
    ): Boolean = insertBitmap(activity as Context, bitmap, suffix, stamp)

    fun insertBitmap(
        context: Context,
        bitmap: Bitmap,
        suffix: String?,
        stamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date()),
    ): Boolean {
        val name = "ExpeditionGauge_${stamp}${suffix.orEmpty()}.jpg"
        val values = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, name)
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.Images.Media.RELATIVE_PATH, "${Environment.DIRECTORY_PICTURES}/ExpeditionGauge")
                put(MediaStore.Images.Media.IS_PENDING, 1)
            }
        }
        val resolver = context.contentResolver
        val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values) ?: return false
        return try {
            resolver.openOutputStream(uri)?.use { out ->
                if (!bitmap.compress(Bitmap.CompressFormat.JPEG, 92, out)) {
                    resolver.delete(uri, null, null)
                    return false
                }
            } ?: run {
                resolver.delete(uri, null, null)
                return false
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                values.clear()
                values.put(MediaStore.Images.Media.IS_PENDING, 0)
                resolver.update(uri, values, null, null)
            }
            true
        } catch (_: Exception) {
            runCatching { resolver.delete(uri, null, null) }
            false
        }
    }
}
