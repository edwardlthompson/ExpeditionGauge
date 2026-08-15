package dev.foss.expeditiongauge.car

import dev.foss.expeditiongauge.car.gauge.AaAttitudeMode
import kotlin.math.abs

/** Cache key for Drive HUD bitmap reuse (attitude + labels + theme + DTC frame). */
internal data class AaHudDriveKey(
    val pitchDeg: Float,
    val rollDeg: Float,
    val attitudeMode: AaAttitudeMode,
    val pitchAlert: Boolean,
    val rollAlert: Boolean,
    val maxPitch: Float?,
    val maxRoll: Float?,
    val speed: String,
    val heading: String,
    val alt: String,
    val coords: String,
    val fl: String,
    val fr: String,
    val rl: String,
    val rr: String,
    val cubePx: Int,
    val dark: Boolean,
    val gpsLinked: Boolean = false,
    val obdLinked: Boolean = false,
    val tpmsLinked: Boolean = false,
    val imuLinked: Boolean = false,
    val speedAlert: Boolean = false,
    val orientation: HudStripOrientation = HudStripOrientation.ROW,
    /** Carousel line for ROW footer; null/blank → no footer. */
    val dtcFooterLine: String? = null,
    val pedalQ: Int = 0,
    val pedalFlashOn: Boolean = true,
) {
    fun sameForReuse(other: AaHudDriveKey): Boolean =
        attitudeMode == other.attitudeMode &&
            pitchAlert == other.pitchAlert &&
            rollAlert == other.rollAlert &&
            maxPitch == other.maxPitch &&
            maxRoll == other.maxRoll &&
            speed == other.speed &&
            heading == other.heading &&
            alt == other.alt &&
            coords == other.coords &&
            fl == other.fl && fr == other.fr && rl == other.rl && rr == other.rr &&
            cubePx == other.cubePx &&
            dark == other.dark &&
            gpsLinked == other.gpsLinked &&
            obdLinked == other.obdLinked &&
            tpmsLinked == other.tpmsLinked &&
            imuLinked == other.imuLinked &&
            speedAlert == other.speedAlert &&
            orientation == other.orientation &&
            dtcFooterLine == other.dtcFooterLine &&
            pedalQ == other.pedalQ &&
            pedalFlashOn == other.pedalFlashOn &&
            abs(pitchDeg - other.pitchDeg) <= AaHudComposer.ATTITUDE_EPS_DEG &&
            abs(rollDeg - other.rollDeg) <= AaHudComposer.ATTITUDE_EPS_DEG
}
