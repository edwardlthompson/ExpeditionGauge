package dev.foss.expeditiongauge.calibration

import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class CalibrationCommitGateTest {
    @Test
    fun manualZero_blocksAutocalForGrace() {
        val gate = CalibrationCommitGate(manualGraceMs = 2_000L)
        assertFalse(gate.blocksAutocal(nowMs = 10_000L))
        gate.markManualZero(nowMs = 10_000L)
        assertTrue(gate.blocksAutocal(nowMs = 10_500L))
        assertTrue(gate.blocksAutocal(nowMs = 11_999L))
        assertFalse(gate.blocksAutocal(nowMs = 12_000L))
    }

    @Test
    fun withCommit_setsBusyFlag() = runBlocking {
        val gate = CalibrationCommitGate()
        var sawBusy = false
        gate.withCommit {
            sawBusy = gate.isBusy()
        }
        assertTrue(sawBusy)
        assertFalse(gate.isBusy())
    }
}
