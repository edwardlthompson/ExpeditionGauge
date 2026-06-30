package dev.foss.expeditiongauge.fusion

import dev.foss.expeditiongauge.ble.ImuDeviceSession
import dev.foss.expeditiongauge.ble.ImuPlacement
import dev.foss.expeditiongauge.ble.WitMotionSample
import org.junit.Assert.assertEquals
import org.junit.Test

class MultiImuYawFusionTest {
    @Test
    fun fusesTwoCornerYaw() {
        val fl = ImuDeviceSession("fl", "FL", ImuPlacement.FrontLeft, connected = true)
        val fr = ImuDeviceSession("fr", "FR", ImuPlacement.FrontRight, connected = true)
        fl.filter.onSample(sample(yaw = 90f))
        fr.filter.onSample(sample(yaw = 100f))
        val output = MultiImuYawFusion().fuse(listOf(fl, fr), phoneYawDeg = 0f)
        assertEquals(95f, output.bodyYawDeg, 0.1f)
        assertEquals("multi_imu", output.source)
        assertEquals(2, output.activeCount)
    }

    @Test
    fun fallsBackToPhoneWhenNoImu() {
        val output = MultiImuYawFusion().fuse(emptyList(), phoneYawDeg = 45f)
        assertEquals(45f, output.bodyYawDeg, 0.01f)
        assertEquals("phone", output.source)
    }

    @Test
    fun rearCornerDifferentialSetsChassisTwist() {
        val rl = ImuDeviceSession("rl", "RL", ImuPlacement.RearLeft, connected = true)
        val rr = ImuDeviceSession("rr", "RR", ImuPlacement.RearRight, connected = true)
        rl.filter.onSample(sample(yaw = 80f))
        rr.filter.onSample(sample(yaw = 100f))
        val output = MultiImuYawFusion().fuse(listOf(rl, rr), phoneYawDeg = 0f)
        assertEquals(20f, output.chassisTwistDeg, 0.1f)
        assertEquals("multi_imu", output.source)
    }

    @Test
    fun toleratesRecentDropoutWithinStaleWindow() {
        val fl = ImuDeviceSession(
            "fl",
            "FL",
            ImuPlacement.FrontLeft,
            connected = false,
            lastSeenMs = System.currentTimeMillis(),
        )
        fl.filter.onSample(sample(yaw = 120f))
        val output = MultiImuYawFusion().fuse(listOf(fl), phoneYawDeg = 10f)
        assertEquals(120f, output.bodyYawDeg, 0.1f)
        assertEquals("external_imu", output.source)
    }

    private fun sample(yaw: Float) = WitMotionSample(0f, 0f, 1f, 0f, 0f, 0f, 0f, 0f, yaw)
}
