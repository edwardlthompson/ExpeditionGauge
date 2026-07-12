package dev.foss.expeditiongauge.calibration

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class StationaryAutocalibratorTest {
    private fun feedStill(
        cal: StationaryAutocalibrator,
        startMs: Long,
        pitch: Float = 4f,
        roll: Float = -3f,
        yaw: Float = 12f,
        magAvailable: Boolean = false,
        samples: Int = 40,
        stepMs: Long = 100L,
    ): StationaryAutocalibrator.Proposal {
        var last: StationaryAutocalibrator.Proposal = StationaryAutocalibrator.Proposal.None
        repeat(samples) { i ->
            last = cal.onSample(
                nowMs = startMs + i * stepMs,
                enabled = true,
                accelX = 0f, accelY = 0f, accelZ = 9.81f,
                gyroX = 0f, gyroY = 0f, gyroZ = 0f,
                displayPitchDeg = pitch,
                displayRollDeg = roll,
                displayYawDeg = yaw,
                magAvailable = magAvailable,
                commitGateBlocks = false,
            )
            if (last is StationaryAutocalibrator.Proposal.PendingConfirm) return last
        }
        return last
    }

    @Test
    fun nullSensors_noProposal() {
        val cal = StationaryAutocalibrator(
            stillDetector = StationaryStillDetector(holdMs = 100L),
            cooldownMs = 1_000L,
        )
        repeat(30) { i ->
            val p = cal.onSample(
                nowMs = i * 100L,
                enabled = true,
                accelX = null, accelY = 0f, accelZ = 9.81f,
                gyroX = 0f, gyroY = 0f, gyroZ = 0f,
                displayPitchDeg = 5f,
                displayRollDeg = 5f,
                displayYawDeg = 10f,
                magAvailable = false,
                commitGateBlocks = false,
            )
            assertEquals(StationaryAutocalibrator.Proposal.None, p)
        }
    }

    @Test
    fun magAbsent_includesRelativeYaw() {
        val cal = StationaryAutocalibrator(
            stillDetector = StationaryStillDetector(holdMs = 100L),
            cooldownMs = 60_000L,
            alreadyLevelHoldMs = 10_000L,
        )
        val p = feedStill(cal, startMs = 0L, magAvailable = false)
        assertTrue(p is StationaryAutocalibrator.Proposal.PendingConfirm)
        val confirm = p as StationaryAutocalibrator.Proposal.PendingConfirm
        assertTrue(confirm.includeYaw)
        assertFalse(confirm.magSkipped)
    }

    @Test
    fun highMagVariance_zerosPitchRollOnly() {
        val magGate = MagStabilityGate(windowSize = 8, maxVariance = 5f)
        repeat(4) { magGate.onSample(20f, 0f, 40f) }
        repeat(4) { magGate.onSample(90f, 60f, 5f) }
        val cal = StationaryAutocalibrator(
            stillDetector = StationaryStillDetector(holdMs = 100L),
            magGate = magGate,
            cooldownMs = 60_000L,
            alreadyLevelHoldMs = 10_000L,
        )
        val p = feedStill(cal, startMs = 0L, magAvailable = true)
        assertTrue(p is StationaryAutocalibrator.Proposal.PendingConfirm)
        val confirm = p as StationaryAutocalibrator.Proposal.PendingConfirm
        assertFalse(confirm.includeYaw)
        assertTrue(confirm.magSkipped)
    }

    @Test
    fun alreadyLevel_skipsAfterHold() {
        val cal = StationaryAutocalibrator(
            stillDetector = StationaryStillDetector(holdMs = 100L),
            cooldownMs = 1L,
            alreadyLevelHoldMs = 200L,
        )
        // First proposal while not level
        val first = feedStill(cal, startMs = 0L, pitch = 5f, roll = 5f)
        assertTrue(first is StationaryAutocalibrator.Proposal.PendingConfirm)
        // Motion clears needMotionBeforeNext
        cal.onSample(
            nowMs = 5_000L, enabled = true,
            accelX = 0f, accelY = 0f, accelZ = 9.81f,
            gyroX = 1f, gyroY = 0f, gyroZ = 0f,
            displayPitchDeg = 0.5f, displayRollDeg = 0.5f, displayYawDeg = 0f,
            magAvailable = false, commitGateBlocks = false,
        )
        // Already level for hold → no proposal
        var sawConfirm = false
        repeat(40) { i ->
            val p = cal.onSample(
                nowMs = 6_000L + i * 100L,
                enabled = true,
                accelX = 0f, accelY = 0f, accelZ = 9.81f,
                gyroX = 0f, gyroY = 0f, gyroZ = 0f,
                displayPitchDeg = 0.5f,
                displayRollDeg = 0.5f,
                displayYawDeg = 0f,
                magAvailable = false,
                commitGateBlocks = false,
            )
            if (p is StationaryAutocalibrator.Proposal.PendingConfirm) sawConfirm = true
        }
        assertFalse(sawConfirm)
    }

    @Test
    fun commitGateBlocks_noProposal() {
        val cal = StationaryAutocalibrator(
            stillDetector = StationaryStillDetector(holdMs = 100L),
        )
        val p = feedStill(cal, startMs = 0L)
        // Force another attempt while blocked
        cal.reset()
        val blocked = cal.onSample(
            nowMs = 10_000L, enabled = true,
            accelX = 0f, accelY = 0f, accelZ = 9.81f,
            gyroX = 0f, gyroY = 0f, gyroZ = 0f,
            displayPitchDeg = 5f, displayRollDeg = 5f, displayYawDeg = 10f,
            magAvailable = false, commitGateBlocks = true,
        )
        assertEquals(StationaryAutocalibrator.Proposal.None, blocked)
        assertTrue(p is StationaryAutocalibrator.Proposal.PendingConfirm)
    }
}
