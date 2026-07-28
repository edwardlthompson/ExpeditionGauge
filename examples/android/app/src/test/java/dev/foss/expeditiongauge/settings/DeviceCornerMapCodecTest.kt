package dev.foss.expeditiongauge.settings

import dev.foss.expeditiongauge.ble.ImuPlacement
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class DeviceCornerMapCodecTest {
    @Test
    fun roundTrip() {
        val map = mapOf(
            "AA:BB:CC:DD:EE:01" to ImuPlacement.FrontLeft,
            "AA:BB:CC:DD:EE:02" to ImuPlacement.FrontRight,
        )
        val encoded = DeviceCornerMapCodec.encode(map)
        assertEquals(map, DeviceCornerMapCodec.decode(encoded))
    }

    @Test
    fun decode_empty() {
        assertTrue(DeviceCornerMapCodec.decode(null).isEmpty())
        assertTrue(DeviceCornerMapCodec.decode("").isEmpty())
    }

    @Test
    fun encode_skipsUnassigned() {
        val encoded = DeviceCornerMapCodec.encode(
            mapOf("AA:BB" to ImuPlacement.Unassigned, "CC:DD" to ImuPlacement.RearLeft),
        )
        assertEquals(mapOf("CC:DD" to ImuPlacement.RearLeft), DeviceCornerMapCodec.decode(encoded))
    }
}
