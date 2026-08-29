package dev.foss.expeditiongauge.calibration

import dev.foss.expeditiongauge.parkedautocaldwell.ParkedAutocalDwell
import kotlin.math.abs
import kotlin.math.sqrt

/** Pure still / motion detector (critique C1/C6). */
class StationaryStillDetector(
    private val gravityMs2: Float = 9.81f,
    private val accelTolMs2: Float = 0.15f * 9.81f,
    private val maxGyroRadPerSec: Float = 0.05f,
    private val holdMs: Long = ParkedAutocalDwell.MOVING_MS,
) {
    private var stillSinceMs: Long? = null

    fun reset() {
        stillSinceMs = null
    }

    /**
     * @return true when continuously still for [holdMs].
     * Null accel/gyro components → never still (C1).
     */
    fun onSample(
        nowMs: Long,
        accelX: Float?,
        accelY: Float?,
        accelZ: Float?,
        gyroX: Float?,
        gyroY: Float?,
        gyroZ: Float?,
    ): Boolean {
        if (accelX == null || accelY == null || accelZ == null ||
            gyroX == null || gyroY == null || gyroZ == null
        ) {
            stillSinceMs = null
            return false
        }
        val aMag = sqrt(accelX * accelX + accelY * accelY + accelZ * accelZ)
        val gMag = sqrt(gyroX * gyroX + gyroY * gyroY + gyroZ * gyroZ)
        val still = abs(aMag - gravityMs2) <= accelTolMs2 && gMag <= maxGyroRadPerSec
        if (!still) {
            stillSinceMs = null
            return false
        }
        val since = stillSinceMs ?: nowMs.also { stillSinceMs = it }
        return nowMs - since >= holdMs
    }

    fun isInMotion(
        accelX: Float?,
        accelY: Float?,
        accelZ: Float?,
        gyroX: Float?,
        gyroY: Float?,
        gyroZ: Float?,
    ): Boolean {
        if (accelX == null || gyroX == null || gyroY == null || gyroZ == null ||
            accelY == null || accelZ == null
        ) {
            return false
        }
        val gMag = sqrt(gyroX * gyroX + gyroY * gyroY + gyroZ * gyroZ)
        return gMag > maxGyroRadPerSec * 2f
    }
}
