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
import kotlin.math.abs

/** Builds AA grid tile images from vehicle-frame snapshot + HU display spec. */
class AaHudComposer(appContext: Context) {
    private val telemetryIcon = resourceIcon(appContext, R.drawable.ic_elevation)
    private val tpmsIcon = resourceIcon(appContext, R.drawable.ic_tire_topdown)
    private val attitudeFallback = resourceIcon(appContext, R.drawable.ic_wheel)

    private var cachedIcon: CarIcon? = null
    private var cachedKey: AttitudeIconKey? = null

    fun compose(
        snapshot: TelemetrySnapshot,
        style: InclinometerStyle,
        alerts: Set<AlertType>,
        thresholds: AlertThresholds,
        displaySpec: AaDisplaySpec,
        built: CarHudTiles,
    ): CarHudTiles {
        val key = AttitudeIconKey(
            pitchDeg = snapshot.pitchDeg,
            rollDeg = snapshot.rollDeg,
            style = style,
            pitchAlert = AlertType.PITCH in alerts,
            rollAlert = AlertType.ROLL in alerts,
            maxPitch = thresholds.maxPitchDeg,
            maxRoll = thresholds.maxRollDeg,
            sizePx = displaySpec.bitmapSizePx,
            dark = displaySpec.isDarkMode,
        )
        val icon = reuseOrBuild(key, snapshot)
        return built.copy(
            gMeter = built.gMeter.copy(image = icon),
            telemetry = built.telemetry.copy(image = telemetryIcon),
            tpms = built.tpms.copy(image = tpmsIcon),
        )
    }

    private fun reuseOrBuild(key: AttitudeIconKey, snapshot: TelemetrySnapshot): CarIcon {
        val prev = cachedKey
        val prevIcon = cachedIcon
        if (prev != null && prevIcon != null && prev.sameForReuse(key)) {
            return prevIcon
        }
        val icon = runCatching {
            InclinometerCarIcon.fromAttitude(
                pitchDeg = snapshot.pitchDeg,
                rollDeg = snapshot.rollDeg,
                style = key.style,
                pitchAlert = key.pitchAlert,
                rollAlert = key.rollAlert,
                maxPitchThresholdDeg = key.maxPitch,
                maxRollThresholdDeg = key.maxRoll,
                yawDeg = snapshot.bodyYawDeg ?: snapshot.headingDeg,
                latG = snapshot.latG,
                lonG = snapshot.lonG,
                sizePx = key.sizePx,
                darkBackground = key.dark,
            )
        }.getOrNull() ?: attitudeFallback
        cachedKey = key
        cachedIcon = icon
        return icon
    }

    private fun resourceIcon(context: Context, drawableRes: Int): CarIcon =
        CarIcon.Builder(IconCompat.createWithResource(context, drawableRes)).build()

    private data class AttitudeIconKey(
        val pitchDeg: Float,
        val rollDeg: Float,
        val style: InclinometerStyle,
        val pitchAlert: Boolean,
        val rollAlert: Boolean,
        val maxPitch: Float?,
        val maxRoll: Float?,
        val sizePx: Int,
        val dark: Boolean,
    ) {
        fun sameForReuse(other: AttitudeIconKey): Boolean =
            style == other.style &&
                pitchAlert == other.pitchAlert &&
                rollAlert == other.rollAlert &&
                maxPitch == other.maxPitch &&
                maxRoll == other.maxRoll &&
                sizePx == other.sizePx &&
                dark == other.dark &&
                abs(pitchDeg - other.pitchDeg) <= ATTITUDE_EPS_DEG &&
                abs(rollDeg - other.rollDeg) <= ATTITUDE_EPS_DEG
    }

    companion object {
        const val ATTITUDE_EPS_DEG = 0.1f
    }
}
