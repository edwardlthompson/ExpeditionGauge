package dev.foss.expeditiongauge.calibration

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
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
        assertEquals(0f, next.yawOffsetDeg, 0.001f)
    }

    @Test
    fun computeZeroOffsets_includesYaw_circular() {
        val first = CalibrationStore.computeZeroOffsets(
            displayPitchDeg = 1f,
            displayRollDeg = 2f,
            displayYawDeg = 170f,
            current = CalibrationOffsets(yawOffsetDeg = 20f),
            includeYaw = true,
        )
        // 20+170=190 → wrapSigned180 → -170
        assertEquals(-170f, first.yawOffsetDeg, 0.001f)
    }

    @Test
    fun computeZeroOffsets_excludeYaw_keepsPriorYaw() {
        val current = CalibrationOffsets(yawOffsetDeg = 45f)
        val next = CalibrationStore.computeZeroOffsets(
            displayPitchDeg = 3f,
            displayRollDeg = 4f,
            displayYawDeg = 90f,
            current = current,
            includeYaw = false,
        )
        assertEquals(3f, next.pitchOffsetDeg, 0.001f)
        assertEquals(4f, next.rollOffsetDeg, 0.001f)
        assertEquals(45f, next.yawOffsetDeg, 0.001f)
    }

    @Test
    fun wrapSigned180_andAlreadyLevel() {
        assertEquals(-170f, CalibrationStore.wrapSigned180(190f), 0.001f)
        assertTrue(CalibrationStore.alreadyLevel(1.5f, -1.5f))
        assertFalse(CalibrationStore.alreadyLevel(2.5f, 0f))
    }

    @Test
    fun computeZeroOffsets_repeatedTapWhileCentered_keepsSameOffsets() {
        val first = CalibrationStore.computeZeroOffsets(20f, 5f, current = CalibrationOffsets())
        val second = CalibrationStore.computeZeroOffsets(0f, 0f, current = first)
        assertEquals(first, second)
    }

    @Test
    fun computeZeroOffsets_afterCalibrate_rawMinusOffsetIsZero() {
        val offsets = CalibrationStore.computeZeroOffsets(15f, -4f, current = CalibrationOffsets())
        assertEquals(0f, 15f - offsets.pitchOffsetDeg, 0.001f)
        assertEquals(0f, -4f - offsets.rollOffsetDeg, 0.001f)
    }
}
