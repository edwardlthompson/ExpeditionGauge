package dev.foss.expeditiongauge.car.gauge

import android.graphics.Bitmap
import androidx.car.app.model.CarIcon
import androidx.core.graphics.drawable.IconCompat

object InclinometerCarIcon {
    private val renderer = InclinometerBitmapRenderer()

    fun fromAttitude(
        pitchDeg: Float,
        rollDeg: Float,
        pitchAlert: Boolean = false,
        rollAlert: Boolean = false,
        maxPitchThresholdDeg: Float? = null,
        maxRollThresholdDeg: Float? = null,
    ): CarIcon {
        val bitmap = renderer.render(
            pitchDeg = pitchDeg,
            rollDeg = rollDeg,
            pitchAlert = pitchAlert,
            rollAlert = rollAlert,
            maxPitchThresholdDeg = maxPitchThresholdDeg,
            maxRollThresholdDeg = maxRollThresholdDeg,
        )
        return CarIcon.Builder(IconCompat.createWithBitmap(bitmap)).build()
    }

    fun renderBitmap(
        pitchDeg: Float,
        rollDeg: Float,
        pitchAlert: Boolean = false,
        rollAlert: Boolean = false,
        maxPitchThresholdDeg: Float? = null,
        maxRollThresholdDeg: Float? = null,
    ): Bitmap = renderer.render(
        pitchDeg = pitchDeg,
        rollDeg = rollDeg,
        pitchAlert = pitchAlert,
        rollAlert = rollAlert,
        maxPitchThresholdDeg = maxPitchThresholdDeg,
        maxRollThresholdDeg = maxRollThresholdDeg,
    )
}
