package dev.foss.expeditiongauge.calibration

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow

data class AutocalPending(
    val pitchDeg: Float,
    val rollDeg: Float,
    val yawDeg: Float,
    val includeYaw: Boolean,
    val magSkipped: Boolean,
)

/**
 * App-scoped auto-calibrate controller (still → confirm → commit).
 */
class AutocalibrationController(
    private val calibrationStore: CalibrationStore,
    private val commitGate: CalibrationCommitGate = CalibrationCommitGate(),
    private val autocalibrator: StationaryAutocalibrator = StationaryAutocalibrator(),
) {
    private val _pending = MutableStateFlow<AutocalPending?>(null)
    val pending: StateFlow<AutocalPending?> = _pending.asStateFlow()

    private val _messages = MutableSharedFlow<String>(extraBufferCapacity = 1)
    val messages: SharedFlow<String> = _messages.asSharedFlow()

    @Volatile
    var enabled: Boolean = true

    @Volatile
    private var lastAccel: Triple<Float, Float, Float>? = null

    @Volatile
    private var lastGyro: Triple<Float, Float, Float>? = null

    @Volatile
    private var magAvailable: Boolean = false

    fun onAccel(x: Float, y: Float, z: Float) {
        lastAccel = Triple(x, y, z)
    }

    fun onGyro(x: Float, y: Float, z: Float) {
        lastGyro = Triple(x, y, z)
    }

    fun onMag(x: Float, y: Float, z: Float) {
        magAvailable = true
        autocalibrator.onMag(x, y, z)
    }

    fun onFusionTick(
        nowMs: Long,
        displayPitchDeg: Float,
        displayRollDeg: Float,
        displayYawDeg: Float,
    ) {
        if (_pending.value != null) return
        val a = lastAccel
        val g = lastGyro
        val proposal = autocalibrator.onSample(
            nowMs = nowMs,
            enabled = enabled,
            accelX = a?.first,
            accelY = a?.second,
            accelZ = a?.third,
            gyroX = g?.first,
            gyroY = g?.second,
            gyroZ = g?.third,
            displayPitchDeg = displayPitchDeg,
            displayRollDeg = displayRollDeg,
            displayYawDeg = displayYawDeg,
            magAvailable = magAvailable,
            commitGateBlocks = commitGate.blocksAutocal(nowMs),
        )
        if (proposal is StationaryAutocalibrator.Proposal.PendingConfirm) {
            _pending.value = AutocalPending(
                pitchDeg = proposal.pitchDeg,
                rollDeg = proposal.rollDeg,
                yawDeg = proposal.yawDeg,
                includeYaw = proposal.includeYaw,
                magSkipped = proposal.magSkipped,
            )
        }
    }

    suspend fun acceptPending(displayRotation: Int) {
        val p = _pending.value ?: return
        commitGate.withCommit {
            calibrationStore.zeroToCurrentDisplay(
                displayPitchDeg = p.pitchDeg,
                displayRollDeg = p.rollDeg,
                displayYawDeg = p.yawDeg,
                displayRotation = displayRotation,
                includeYaw = p.includeYaw,
            )
        }
        _pending.value = null
        val msg = if (p.magSkipped) {
            "Level set (compass skipped — interference)"
        } else {
            "Level set"
        }
        _messages.tryEmit(msg)
    }

    suspend fun dismissPending() {
        _pending.value = null
        autocalibrator.armCooldown(System.currentTimeMillis())
    }

    suspend fun manualZero(
        pitchDeg: Float,
        rollDeg: Float,
        yawDeg: Float,
        displayRotation: Int,
    ) {
        commitGate.markManualZero()
        commitGate.withCommit {
            calibrationStore.zeroToCurrentDisplay(
                displayPitchDeg = pitchDeg,
                displayRollDeg = rollDeg,
                displayYawDeg = yawDeg,
                displayRotation = displayRotation,
                includeYaw = true,
            )
        }
        _pending.value = null
    }

    fun commitGate(): CalibrationCommitGate = commitGate
}
