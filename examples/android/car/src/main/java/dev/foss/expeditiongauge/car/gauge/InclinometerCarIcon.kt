package dev.foss.expeditiongauge.car.gauge

import android.graphics.Bitmap
import androidx.car.app.model.CarIcon
import androidx.core.graphics.drawable.IconCompat

object InclinometerCarIcon {
    private val renderer = InclinometerBitmapRenderer()

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
    ): CarIcon {
        val bitmap = renderer.render(
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
    ): Bitmap = renderer.render(
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
    )
}
