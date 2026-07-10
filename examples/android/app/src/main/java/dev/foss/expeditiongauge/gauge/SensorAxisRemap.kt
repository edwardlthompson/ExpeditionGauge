package dev.foss.expeditiongauge.gauge

/**
 * Remap device-frame IMU vectors into a **screen-stable** frame where +Y is up the
 * current UI and +X is right — same as portrait device axes at [Surface.ROTATION_0].
 *
 * **LOCKED (ADR-0013 / 2026-07-09):** Feed remapped accel/gyro into Madgwick so
 * landscape never looks like a ±90° bank. Do not replace this with post-fusion
 * Euler unwrap. Do not change the ROTATION_90 matrix without updating
 * [SensorAxisRemapTest] and `docs/design/GMETER_HUD_ROTATION.md`.
 *
 * ROTATION_90 = 90° CCW from portrait (nav bar on right on OnePlus 12).
 */
object SensorAxisRemap {
    fun remap(x: Float, y: Float, z: Float, displayRotation: Int): Triple<Float, Float, Float> =
        when (displayRotation.mod(4)) {
            1 -> Triple(-y, x, z) // ROTATION_90
            2 -> Triple(-x, -y, z) // ROTATION_180
            3 -> Triple(y, -x, z) // ROTATION_270
            else -> Triple(x, y, z)
        }
}
