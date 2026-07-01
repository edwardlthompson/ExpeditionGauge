package dev.foss.expeditiongauge.calibration

import org.junit.Assert.assertEquals
import org.junit.Test

class CalibrationStoreTest {
    @Test
    fun computeZeroOffsets_firstCalibrate_storesRawBaseline() {
        val next = CalibrationStore.computeZeroOffsets(
            displayPitchDeg = 18f,
            displayRollDeg = -6f,
            current = CalibrationOffsets(),
        )
        assertEquals(18f, next.pitchOffsetDeg, 0.001f)
        assertEquals(-6f, next.rollOffsetDeg, 0.001f)
    }

    @Test
    fun computeZeroOffsets_repeatedTapWhileCentered_keepsSameOffsets() {
        val first = CalibrationStore.computeZeroOffsets(20f, 5f, CalibrationOffsets())
        val second = CalibrationStore.computeZeroOffsets(0f, 0f, first)
        assertEquals(first, second)
    }

    @Test
    fun computeZeroOffsets_afterCalibrate_rawMinusOffsetIsZero() {
        val offsets = CalibrationStore.computeZeroOffsets(15f, -4f, CalibrationOffsets())
        val rawPitch = 15f
        val rawRoll = -4f
        assertEquals(0f, rawPitch - offsets.pitchOffsetDeg, 0.001f)
        assertEquals(0f, rawRoll - offsets.rollOffsetDeg, 0.001f)
    }
}
