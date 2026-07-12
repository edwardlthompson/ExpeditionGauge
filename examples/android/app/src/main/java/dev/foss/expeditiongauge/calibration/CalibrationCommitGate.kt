package dev.foss.expeditiongauge.calibration

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/** Serializes Zero commits; blocks autocal soon after manual Zero (critique C4). */
class CalibrationCommitGate(
    private val manualGraceMs: Long = 2_000L,
) {
    private val mutex = Mutex()
    @Volatile
    private var lastManualZeroMs: Long = 0L
    @Volatile
    private var calibrating: Boolean = false

    suspend fun <T> withCommit(block: suspend () -> T): T = mutex.withLock {
        calibrating = true
        try {
            block()
        } finally {
            calibrating = false
        }
    }

    fun markManualZero(nowMs: Long = System.currentTimeMillis()) {
        lastManualZeroMs = nowMs
    }

    fun isBusy(): Boolean = calibrating

    fun blocksAutocal(nowMs: Long = System.currentTimeMillis()): Boolean =
        calibrating || (nowMs - lastManualZeroMs) < manualGraceMs
}
