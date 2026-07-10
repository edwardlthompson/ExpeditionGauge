package dev.foss.expeditiongauge.gauge

/**
 * Legacy Euler phone-rotation unwrap (device frame → pre-swap).
 *
 * Prefer [SensorAxisRemap] before Madgwick (ADR-0013). Fusion normally passes
 * rotationDelta=0 after axis remap. Kept for [VehicleAttitudeLogic] / tests.
 */
object PhoneRotationUnwrap {
    fun unwrapPhoneRotation(
        pitchDeg: Float,
        rollDeg: Float,
        rotationDelta: Int,
    ): Pair<Float, Float> =
        when (rotationDelta.mod(4)) {
            1, 3 -> unwrapLandscapeBank(pitchDeg, rollDeg)
            2 -> -pitchDeg to -rollDeg
            else -> pitchDeg to rollDeg
        }

    /** Gimbal-fragile; prefer [SensorAxisRemap]. */
    fun unwrapLandscapeBank(pitchDeg: Float, rollDeg: Float): Pair<Float, Float> =
        when {
            kotlin.math.abs(rollDeg) > 90f -> pitchDeg to wrapSigned180(rollDeg + 180f)
            pitchDeg > 45f -> wrapSigned180(pitchDeg - 90f) to rollDeg
            pitchDeg < -45f -> wrapSigned180(pitchDeg + 90f) to rollDeg
            else -> pitchDeg to rollDeg
        }

    fun wrapSigned180(deg: Float): Float {
        var a = deg % 360f
        if (a > 180f) a -= 360f
        if (a <= -180f) a += 360f
        return a
    }
}
