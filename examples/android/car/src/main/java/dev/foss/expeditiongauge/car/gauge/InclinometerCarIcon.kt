package dev.foss.expeditiongauge.car.gauge

import android.graphics.Bitmap
import androidx.car.app.model.CarIcon
import androidx.core.graphics.drawable.IconCompat
import dev.foss.expeditiongauge.car.AaDisplaySpec

object InclinometerCarIcon {
    private val rendererLock = Any()
    private var cachedSizePx: Int = InclinometerBitmapRenderer.DEFAULT_SIZE_PX
    private var renderer: InclinometerBitmapRenderer =
        InclinometerBitmapRenderer(InclinometerBitmapRenderer.DEFAULT_SIZE_PX)

    private fun rendererFor(sizePx: Int): InclinometerBitmapRenderer {
        val clamped = sizePx.coerceIn(AaDisplaySpec.MIN_BITMAP_PX, AaDisplaySpec.MAX_BITMAP_PX)
        synchronized(rendererLock) {
            if (clamped != cachedSizePx) {
                cachedSizePx = clamped
                renderer = InclinometerBitmapRenderer(clamped)
            }
            return renderer
        }
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
        val bitmap = rendererFor(sizePx).render(
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
    ): Bitmap = rendererFor(sizePx).render(
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
}
