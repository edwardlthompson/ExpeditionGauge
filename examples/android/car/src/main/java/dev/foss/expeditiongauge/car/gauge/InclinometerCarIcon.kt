package dev.foss.expeditiongauge.car.gauge

import android.graphics.Bitmap
import androidx.car.app.model.CarIcon
import androidx.core.graphics.drawable.IconCompat
import dev.foss.expeditiongauge.car.AaDisplaySpec
import java.util.concurrent.ConcurrentHashMap

/**
 * Inclinometer bitmaps for AA and phone. Size-keyed renderer pool; always returns an
 * immutable [Bitmap.copy] so hosts/Compose never share the live canvas buffer.
 */
object InclinometerCarIcon {
    private val pool = ConcurrentHashMap<Int, InclinometerBitmapRenderer>()

    /** Phone Compose / Surface HUD may exceed the AA Grid [AaDisplaySpec.MAX_BITMAP_PX] cap. */
    const val PHONE_MAX_BITMAP_PX = AaDisplaySpec.MAX_SURFACE_CUBE_PX

    private fun rendererFor(sizePx: Int, maxPx: Int): InclinometerBitmapRenderer {
        val clamped = sizePx.coerceIn(AaDisplaySpec.MIN_BITMAP_PX, maxPx)
        return pool.getOrPut(clamped) { InclinometerBitmapRenderer(clamped) }
    }

    private fun renderCopy(
        pitchDeg: Float,
        rollDeg: Float,
        style: InclinometerStyle,
        pitchAlert: Boolean,
        rollAlert: Boolean,
        maxPitchThresholdDeg: Float?,
        maxRollThresholdDeg: Float?,
        labelPitchDeg: Float?,
        labelRollDeg: Float?,
        yawDeg: Float?,
        latG: Float?,
        lonG: Float?,
        sizePx: Int,
        maxPx: Int,
        darkBackground: Boolean,
    ): Bitmap {
        val rendered = rendererFor(sizePx, maxPx).render(
            pitchDeg = pitchDeg,
            rollDeg = rollDeg,
            style = style,
            pitchAlert = pitchAlert,
            rollAlert = rollAlert,
            maxPitchThresholdDeg = maxPitchThresholdDeg,
            maxRollThresholdDeg = maxRollThresholdDeg,
            labelPitchDeg = labelPitchDeg,
            labelRollDeg = labelRollDeg,
            yawDeg = yawDeg,
            latG = latG,
            lonG = lonG,
            darkBackground = darkBackground,
        )
        return rendered.copy(Bitmap.Config.ARGB_8888, false)
    }

    fun fromAttitude(
        pitchDeg: Float,
        rollDeg: Float,
        style: InclinometerStyle = InclinometerStyle.LADDER,
        pitchAlert: Boolean = false,
        rollAlert: Boolean = false,
        maxPitchThresholdDeg: Float? = null,
        maxRollThresholdDeg: Float? = null,
        labelPitchDeg: Float? = null,
        labelRollDeg: Float? = null,
        yawDeg: Float? = null,
        latG: Float? = null,
        lonG: Float? = null,
        sizePx: Int = InclinometerBitmapRenderer.DEFAULT_SIZE_PX,
        darkBackground: Boolean = true,
    ): CarIcon {
        val bitmap = renderCopy(
            pitchDeg, rollDeg, style, pitchAlert, rollAlert,
            maxPitchThresholdDeg, maxRollThresholdDeg,
            labelPitchDeg, labelRollDeg, yawDeg, latG, lonG,
            sizePx, AaDisplaySpec.MAX_BITMAP_PX, darkBackground,
        )
        return CarIcon.Builder(IconCompat.createWithBitmap(bitmap)).build()
    }

    fun renderBitmap(
        pitchDeg: Float,
        rollDeg: Float,
        style: InclinometerStyle = InclinometerStyle.LADDER,
        pitchAlert: Boolean = false,
        rollAlert: Boolean = false,
        maxPitchThresholdDeg: Float? = null,
        maxRollThresholdDeg: Float? = null,
        labelPitchDeg: Float? = null,
        labelRollDeg: Float? = null,
        yawDeg: Float? = null,
        latG: Float? = null,
        lonG: Float? = null,
        sizePx: Int = InclinometerBitmapRenderer.DEFAULT_SIZE_PX,
        darkBackground: Boolean = true,
    ): Bitmap = renderCopy(
        pitchDeg, rollDeg, style, pitchAlert, rollAlert,
        maxPitchThresholdDeg, maxRollThresholdDeg,
        labelPitchDeg, labelRollDeg, yawDeg, latG, lonG,
        sizePx, PHONE_MAX_BITMAP_PX, darkBackground,
    )
}
