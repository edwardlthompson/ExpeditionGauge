package dev.foss.expeditiongauge.car.gauge

import android.graphics.Bitmap
import dev.foss.expeditiongauge.car.AaDisplaySpec
import java.util.concurrent.ConcurrentHashMap

object GMeterCarIcon {
    private val pool = ConcurrentHashMap<Int, GMeterBitmapRenderer>()

    fun renderBitmap(
        pitchDeg: Float,
        rollDeg: Float,
        pitchAlert: Boolean,
        rollAlert: Boolean,
        sizePx: Int,
        yawDeg: Float? = null,
        latG: Float? = null,
        lonG: Float? = null,
        darkBackground: Boolean = true,
    ): Bitmap {
        val clamped = sizePx.coerceIn(AaDisplaySpec.MIN_BITMAP_PX, AaDisplaySpec.MAX_SURFACE_CUBE_PX)
        val rendered = pool.getOrPut(clamped) { GMeterBitmapRenderer(clamped) }.render(
            pitchDeg = pitchDeg,
            rollDeg = rollDeg,
            pitchAlert = pitchAlert,
            rollAlert = rollAlert,
            yawDeg = yawDeg,
            latG = latG,
            lonG = lonG,
            darkBackground = darkBackground,
        )
        return rendered.copy(Bitmap.Config.ARGB_8888, false)
    }
}
