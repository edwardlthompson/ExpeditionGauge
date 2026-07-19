package dev.foss.expeditiongauge.car.gauge

import android.graphics.Bitmap
import dev.foss.expeditiongauge.car.AaDisplaySpec
import java.util.concurrent.ConcurrentHashMap

object CompassBallCarIcon {
    private val pool = ConcurrentHashMap<Int, CompassBallBitmapRenderer>()

    fun renderBitmap(
        pitchDeg: Float,
        rollDeg: Float,
        yawDeg: Float,
        cardinalsTrusted: Boolean,
        sizePx: Int,
        darkBackground: Boolean = true,
    ): Bitmap {
        val clamped = sizePx.coerceIn(AaDisplaySpec.MIN_BITMAP_PX, AaDisplaySpec.MAX_SURFACE_CUBE_PX)
        val rendered = pool.getOrPut(clamped) { CompassBallBitmapRenderer(clamped) }.render(
            pitchDeg = pitchDeg,
            rollDeg = rollDeg,
            yawDeg = yawDeg,
            cardinalsTrusted = cardinalsTrusted,
            darkBackground = darkBackground,
        )
        return rendered.copy(Bitmap.Config.ARGB_8888, false)
    }
}
