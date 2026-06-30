package dev.foss.expeditiongauge.fusion

import dev.foss.expeditiongauge.ble.ImuDeviceSession
import dev.foss.expeditiongauge.ble.ImuPlacement
import kotlin.math.abs

data class MultiImuFusionOutput(
    val bodyYawDeg: Float,
    val pitchDeg: Float,
    val rollDeg: Float,
    val latG: Float,
    val lonG: Float,
    val yawRateDegPerSec: Float,
    val chassisTwistDeg: Float,
    val source: String,
    val activeCount: Int,
)

/**
 * Weighted fusion of 1–4 corner IMU sessions with phone fallback.
 */
class MultiImuYawFusion {
    fun fuse(sessions: List<ImuDeviceSession>, phoneYawDeg: Float): MultiImuFusionOutput {
        val now = System.currentTimeMillis()
        val active = sessions.filter { session ->
            session.placement != ImuPlacement.Unassigned &&
                (session.connected || now - session.lastSeenMs <= STALE_MS)
        }
        if (active.isEmpty()) {
            return MultiImuFusionOutput(
                bodyYawDeg = phoneYawDeg,
                pitchDeg = 0f,
                rollDeg = 0f,
                latG = 0f,
                lonG = 0f,
                yawRateDegPerSec = 0f,
                chassisTwistDeg = 0f,
                source = "phone",
                activeCount = 0,
            )
        }
        var yawSum = 0f
        var pitchSum = 0f
        var rollSum = 0f
        var latSum = 0f
        var lonSum = 0f
        var rateSum = 0f
        var weightSum = 0f
        active.forEach { session ->
            val w = session.filter.quality()
            yawSum += session.filter.yawDeg() * w
            pitchSum += session.filter.pitchDeg() * w
            rollSum += session.filter.rollDeg() * w
            latSum += session.filter.latG() * w
            lonSum += session.filter.lonG() * w
            rateSum += session.filter.yawRateDegPerSec() * w
            weightSum += w
        }
        val inv = if (weightSum > 0f) 1f / weightSum else 1f
        val rear = active.filter {
            it.placement == ImuPlacement.RearLeft || it.placement == ImuPlacement.RearRight
        }
        val twist = if (rear.size >= 2) {
            abs(rear[0].filter.yawDeg() - rear[1].filter.yawDeg())
        } else {
            0f
        }
        return MultiImuFusionOutput(
            bodyYawDeg = yawSum * inv,
            pitchDeg = pitchSum * inv,
            rollDeg = rollSum * inv,
            latG = latSum * inv,
            lonG = lonSum * inv,
            yawRateDegPerSec = rateSum * inv,
            chassisTwistDeg = twist,
            source = if (active.size == 1) "external_imu" else "multi_imu",
            activeCount = active.size,
        )
    }

    companion object {
        /** Grace period after notify dropout before excluding a corner IMU. */
        const val STALE_MS = 2_000L
    }
}
