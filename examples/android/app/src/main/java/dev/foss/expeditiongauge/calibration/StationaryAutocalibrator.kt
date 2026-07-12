package dev.foss.expeditiongauge.calibration

/**
 * Orchestrates still detection → propose auto-Zero (critique C1–C4).
 * Does not write DataStore; caller confirms (C2) then commits via [CalibrationCommitGate].
 */
class StationaryAutocalibrator(
    private val stillDetector: StationaryStillDetector = StationaryStillDetector(),
    private val magGate: MagStabilityGate = MagStabilityGate(),
    private val cooldownMs: Long = 60_000L,
    private val alreadyLevelHoldMs: Long = 1_000L,
) {
    private var cooldownUntilMs: Long = 0L
    private var needMotionBeforeNext: Boolean = false
    private var levelSinceMs: Long? = null
    private var pitchSum = 0f
    private var rollSum = 0f
    private var yawSum = 0f
    private var sampleCount = 0

    sealed class Proposal {
        data class PendingConfirm(
            val pitchDeg: Float,
            val rollDeg: Float,
            val yawDeg: Float,
            val includeYaw: Boolean,
            val magSkipped: Boolean,
        ) : Proposal()

        data object None : Proposal()
    }

    fun reset() {
        stillDetector.reset()
        magGate.clear()
        pitchSum = 0f
        rollSum = 0f
        yawSum = 0f
        sampleCount = 0
        levelSinceMs = null
    }

    fun onMag(mx: Float, my: Float, mz: Float) = magGate.onSample(mx, my, mz)

    fun onSample(
        nowMs: Long,
        enabled: Boolean,
        accelX: Float?,
        accelY: Float?,
        accelZ: Float?,
        gyroX: Float?,
        gyroY: Float?,
        gyroZ: Float?,
        displayPitchDeg: Float,
        displayRollDeg: Float,
        displayYawDeg: Float,
        magAvailable: Boolean,
        commitGateBlocks: Boolean,
    ): Proposal {
        if (!enabled || commitGateBlocks || nowMs < cooldownUntilMs) {
            stillDetector.reset()
            clearAverage()
            return Proposal.None
        }
        if (needMotionBeforeNext) {
            if (stillDetector.isInMotion(accelX, accelY, accelZ, gyroX, gyroY, gyroZ)) {
                needMotionBeforeNext = false
            } else {
                return Proposal.None
            }
        }
        if (CalibrationStore.alreadyLevel(displayPitchDeg, displayRollDeg)) {
            val since = levelSinceMs ?: nowMs.also { levelSinceMs = it }
            if (nowMs - since >= alreadyLevelHoldMs) {
                stillDetector.reset()
                clearAverage()
                return Proposal.None
            }
        } else {
            levelSinceMs = null
        }

        val still = stillDetector.onSample(
            nowMs, accelX, accelY, accelZ, gyroX, gyroY, gyroZ,
        )
        if (!still) {
            clearAverage()
            return Proposal.None
        }
        pitchSum += displayPitchDeg
        rollSum += displayRollDeg
        yawSum += displayYawDeg
        sampleCount++
        if (sampleCount < 5) return Proposal.None

        val magSkipped = magAvailable && !magGate.isStable()
        val includeYaw = !magSkipped // no mag → relative yaw; mag unstable → P/R only
        val fixed = Proposal.PendingConfirm(
            pitchDeg = pitchSum / sampleCount,
            rollDeg = rollSum / sampleCount,
            yawDeg = yawSum / sampleCount,
            includeYaw = includeYaw,
            magSkipped = magSkipped,
        )
        armCooldown(nowMs)
        stillDetector.reset()
        clearAverage()
        needMotionBeforeNext = true
        return fixed
    }

    fun armCooldown(nowMs: Long, durationMs: Long = cooldownMs) {
        cooldownUntilMs = nowMs + durationMs
    }

    private fun clearAverage() {
        pitchSum = 0f
        rollSum = 0f
        yawSum = 0f
        sampleCount = 0
    }
}
