package dev.foss.expeditiongauge.ble

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class WitMotionParserTest {
    @Test
    fun parses0x61PacketPitch() {
        val packet = byteArrayOf(
            0x55, 0x61,
            0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
            0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
            0x00, 0x00, 0x00, 0x20, 0x00, 0x00,
        )
        val sample = WitMotionPacketParser.parsePacket(packet)
        assertNotNull(sample)
        assertEquals(45f, sample!!.pitchDeg, 1f)
    }

    @Test
    fun parses0x61PacketYawAndGyro() {
        val packet = byteArrayOf(
            0x55, 0x61,
            0x00, 0x00, 0x00, 0x00, 0x40, 0x00,
            0x00, 0x10, 0x00, 0x00, 0x00, 0x04,
            0x00, 0x00, 0x00, 0x00, 0x00, 0x40,
        )
        val sample = WitMotionParser.parsePacket(packet)
        assertNotNull(sample)
        assertEquals(90f, sample!!.yawDeg, 1f)
        assertEquals(62.5f, sample.gzDegPerSec, 2f)
    }

    @Test
    fun rejectsInvalidHeader() {
        val packet = byteArrayOf(0x56, 0x61) + ByteArray(18)
        assertNull(WitMotionParser.parsePacket(packet))
    }

    @Test
    fun rejectsShortPacket() {
        assertNull(WitMotionParser.parsePacket(byteArrayOf(0x55, 0x61)))
    }

    @Test
    fun buildRateCommandTargets50Hz() {
        val cmd = WitMotionParser.buildRateCommand(50)
        assertEquals(0xFF.toByte(), cmd[0])
        assertEquals(0xAA.toByte(), cmd[1])
        assertEquals(0x03.toByte(), cmd[2])
        assertEquals(0x08.toByte(), cmd[3])
    }
}
