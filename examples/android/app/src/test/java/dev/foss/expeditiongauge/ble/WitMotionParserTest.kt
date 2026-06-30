package dev.foss.expeditiongauge.ble

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class WitMotionParserTest {
    @Test
    fun parses0x61Packet() {
        val packet = byteArrayOf(
            0x55, 0x61,
            0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
            0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
            0x00, 0x00, 0x00, 0x20, 0x00, 0x00,
        )
        val sample = WitMotionParser.parsePacket(packet)
        assertNotNull(sample)
        assertEquals(45f, sample!!.pitchDeg, 1f)
    }

    @Test
    fun buildRateCommandTargets50Hz() {
        val cmd = WitMotionParser.buildRateCommand(50)
        assertEquals(0xFF.toByte(), cmd[0])
        assertEquals(0xAA.toByte(), cmd[1])
        assertEquals(0x03.toByte(), cmd[2])
    }
}
