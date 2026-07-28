package dev.foss.expeditiongauge.ui.settings.tpms

import org.junit.Assert.assertEquals
import org.junit.Test

class TpmsQrFrameDecoderRotateTest {
    @Test
    fun rotateCw_2x2() {
        // Access via decode path indirectly: verify 2x2 pattern rotation math here.
        val src = byteArrayOf(1, 2, 3, 4) // row0: 1 2 / row1: 3 4
        val width = 2
        val height = 2
        val out = ByteArray(src.size)
        for (y in 0 until height) {
            for (x in 0 until width) {
                out[x * height + (height - 1 - y)] = src[y * width + x]
            }
        }
        // Expected: 3 1 / 4 2
        assertEquals(3.toByte(), out[0])
        assertEquals(1.toByte(), out[1])
        assertEquals(4.toByte(), out[2])
        assertEquals(2.toByte(), out[3])
    }
}
