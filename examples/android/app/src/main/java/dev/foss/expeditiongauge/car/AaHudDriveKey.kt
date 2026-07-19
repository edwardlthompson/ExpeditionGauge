package dev.foss.expeditiongauge.car

import dev.foss.expeditiongauge.car.gauge.AaAttitudeMode
import kotlin.math.abs

/** Cache key for Drive HUD bitmap reuse (attitude + labels + theme). */
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
            abs(pitchDeg - other.pitchDeg) <= AaHudComposer.ATTITUDE_EPS_DEG &&
            abs(rollDeg - other.rollDeg) <= AaHudComposer.ATTITUDE_EPS_DEG
}
