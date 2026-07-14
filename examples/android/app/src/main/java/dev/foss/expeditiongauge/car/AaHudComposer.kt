package dev.foss.expeditiongauge.car

import android.content.Context
import androidx.car.app.model.CarIcon
import androidx.core.graphics.drawable.IconCompat
import dev.foss.expeditiongauge.R
import dev.foss.expeditiongauge.alerts.AlertThresholds
import dev.foss.expeditiongauge.alerts.AlertType
import dev.foss.expeditiongauge.car.gauge.InclinometerCarIcon
import dev.foss.expeditiongauge.car.gauge.InclinometerStyle
import dev.foss.expeditiongauge.telemetry.TelemetrySnapshot

/** Builds AA grid tile images from vehicle-frame snapshot + HU display spec. */
class AaHudComposer(appContext: Context) {
    private val telemetryIcon = resourceIcon(appContext, R.drawable.ic_elevation)
    private val tpmsIcon = resourceIcon(appContext, R.drawable.ic_tire_topdown)
    private val attitudeFallback = resourceIcon(appContext, R.drawable.ic_wheel)

    fun compose(
        snapshot: TelemetrySnapshot,
        style: InclinometerStyle,
        alerts: Set<AlertType>,
        thresholds: AlertThresholds,
        displaySpec: AaDisplaySpec,
        built: CarHudTiles,
    ): CarHudTiles {
        val icon = runCatching {
            InclinometerCarIcon.fromAttitude(
                pitchDeg = snapshot.pitchDeg,
                rollDeg = snapshot.rollDeg,
                style = style,
                pitchAlert = AlertType.PITCH in alerts,
                rollAlert = AlertType.ROLL in alerts,
                maxPitchThresholdDeg = thresholds.maxPitchDeg,
                maxRollThresholdDeg = thresholds.maxRollDeg,
                yawDeg = snapshot.bodyYawDeg ?: snapshot.headingDeg,
                latG = snapshot.latG,
                lonG = snapshot.lonG,
                sizePx = displaySpec.bitmapSizePx,
                darkBackground = displaySpec.isDarkMode,
            )
        }.getOrNull() ?: attitudeFallback
        return built.copy(
            gMeter = built.gMeter.copy(image = icon),
            telemetry = built.telemetry.copy(image = telemetryIcon),
            tpms = built.tpms.copy(image = tpmsIcon),
        )
    }

    private fun resourceIcon(context: Context, drawableRes: Int): CarIcon =
        CarIcon.Builder(IconCompat.createWithResource(context, drawableRes)).build()
}
