package dev.foss.expeditiongauge.gauge

/**
 * Orientation-stable vehicle pitch/roll for calibration + inclinometer.
 *
 * IMU is remapped to a screen-stable frame in [SensorFusionEngine] before Madgwick
 * ([SensorAxisRemap]), so [displayRotation] here is normally 0.
 *
 * Portrait @ ROTATION_0 identity (locked): swap(device) — vehicle pitch ← device roll,
 * vehicle roll ← device pitch. Do not change that contract.
 */
object VehicleAttitudeLogic {
    fun fromDevice(
        devicePitchDeg: Float,
        deviceRollDeg: Float,
        displayRotation: Int,
    ): Pair<Float, Float> {
        val (p, r) = GaugeDisplayRotation.unwrapPhoneRotation(
            devicePitchDeg,
            deviceRollDeg,
            displayRotation.mod(4),
        )
        // Locked portrait swap: vehicle pitch ← device roll, vehicle roll ← device pitch
        return r to p
    }
}
