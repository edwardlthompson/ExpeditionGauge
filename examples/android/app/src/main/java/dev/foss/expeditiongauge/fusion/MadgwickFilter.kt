package dev.foss.expeditiongauge.fusion

import kotlin.math.asin
import kotlin.math.atan2
import kotlin.math.sqrt

/**
 * Madgwick AHRS filter (simplified 6-DOF when magnetometer absent).
 * Based on Madgwick's open-source IMU/AHRS algorithm.
 */
class MadgwickFilter(
    beta: Float = 0.1f,
    private val samplePeriodSec: Float = 0.02f,
) {
    private var beta: Float = beta
    private var q0 = 1f
    private var q1 = 0f
    private var q2 = 0f
    private var q3 = 0f

    fun reset() {
        q0 = 1f
        q1 = 0f
        q2 = 0f
        q3 = 0f
    }

    fun setBeta(value: Float) {
        beta = value.coerceIn(0.01f, 0.5f)
    }

    fun update(gyroX: Float, gyroY: Float, gyroZ: Float, accelX: Float, accelY: Float, accelZ: Float) {
        var ax = accelX
        var ay = accelY
        var az = accelZ
        val norm = sqrt(ax * ax + ay * ay + az * az)
        if (norm < 1e-6f) return
        ax /= norm
        ay /= norm
        az /= norm

        val f1 = 2f * (q1 * q3 - q0 * q2) - ax
        val f2 = 2f * (q0 * q1 + q2 * q3) - ay
        val f3 = 2f * (0.5f - q1 * q1 - q2 * q2) - az
        val j11or24 = 2f * q2
        val j12or23 = 2f * q3
        val j13or22 = 2f * q0
        val j14or21 = 2f * q1
        val j32 = 2f * j14or21
        val j33 = 2f * j11or24
        val s0 = j14or21 * f2 - j11or24 * f1
        val s1 = j12or23 * f1 + j13or22 * f2 - j32 * f3
        val s2 = j13or22 * f1 - j33 * f3 - j12or23 * f2
        val s3 = j14or21 * f1 + j11or24 * f2
        val sNorm = sqrt(s0 * s0 + s1 * s1 + s2 * s2 + s3 * s3)
        val stepScale = if (sNorm > 1e-6f) beta / sNorm else 0f
        val s0n = s0 * stepScale
        val s1n = s1 * stepScale
        val s2n = s2 * stepScale
        val s3n = s3 * stepScale

        val qDot1 = 0.5f * (-q1 * gyroX - q2 * gyroY - q3 * gyroZ) - s0n
        val qDot2 = 0.5f * (q0 * gyroX + q2 * gyroZ - q3 * gyroY) - s1n
        val qDot3 = 0.5f * (q0 * gyroY - q1 * gyroZ + q3 * gyroX) - s2n
        val qDot4 = 0.5f * (q0 * gyroZ + q1 * gyroY - q2 * gyroX) - s3n

        q0 += qDot1 * samplePeriodSec
        q1 += qDot2 * samplePeriodSec
        q2 += qDot3 * samplePeriodSec
        q3 += qDot4 * samplePeriodSec
        normalizeQuaternion()
    }

    fun pitchDeg(): Float {
        val pitchRad = asin((2f * (q0 * q2 - q3 * q1)).coerceIn(-1f, 1f))
        return Math.toDegrees(pitchRad.toDouble()).toFloat()
    }

    fun rollDeg(): Float {
        val rollRad = atan2(2f * (q0 * q1 + q2 * q3), 1f - 2f * (q1 * q1 + q2 * q2))
        return Math.toDegrees(rollRad.toDouble()).toFloat()
    }

    fun yawDeg(): Float {
        val yawRad = atan2(2f * (q0 * q3 + q1 * q2), 1f - 2f * (q2 * q2 + q3 * q3))
        return Math.toDegrees(yawRad.toDouble()).toFloat()
    }

    private fun normalizeQuaternion() {
        val norm = sqrt(q0 * q0 + q1 * q1 + q2 * q2 + q3 * q3)
        if (norm < 1e-6f) return
        q0 /= norm
        q1 /= norm
        q2 /= norm
        q3 /= norm
    }
}
