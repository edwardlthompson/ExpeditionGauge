package dev.foss.expeditiongauge.ble.tpms

import dev.foss.expeditiongauge.ble.ImuPlacement
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class TpmsCornerAssignLogicTest {
    @Test
    fun exclusive_setsCorner() {
        val next = TpmsCornerAssignLogic.exclusiveAssign(
            emptyMap(),
            "aa:bb:cc:dd:ee:ff",
            ImuPlacement.FrontLeft,
        )
        assertEquals(ImuPlacement.FrontLeft, next["AA:BB:CC:DD:EE:FF"])
    }

    @Test
    fun exclusive_relocatesMac() {
        val start = mapOf("AA:BB:CC:DD:EE:FF" to ImuPlacement.FrontLeft)
        val next = TpmsCornerAssignLogic.exclusiveAssign(
            start,
            "AA:BB:CC:DD:EE:FF",
            ImuPlacement.FrontRight,
        )
        assertEquals(ImuPlacement.FrontRight, next["AA:BB:CC:DD:EE:FF"])
        assertEquals(1, next.size)
    }

    @Test
    fun exclusive_clearsDisplacedCorner() {
        val start = mapOf(
            "AA:BB:CC:DD:EE:01" to ImuPlacement.FrontLeft,
            "AA:BB:CC:DD:EE:02" to ImuPlacement.FrontRight,
        )
        val next = TpmsCornerAssignLogic.exclusiveAssign(
            start,
            "AA:BB:CC:DD:EE:02",
            ImuPlacement.FrontLeft,
        )
        assertEquals(ImuPlacement.FrontLeft, next["AA:BB:CC:DD:EE:02"])
        assertNull(next["AA:BB:CC:DD:EE:01"])
    }

    @Test
    fun unassign_removesMac() {
        val start = mapOf("AA:BB:CC:DD:EE:FF" to ImuPlacement.RearLeft)
        val next = TpmsCornerAssignLogic.exclusiveAssign(
            start,
            "AA:BB:CC:DD:EE:FF",
            ImuPlacement.Unassigned,
        )
        assertEquals(emptyMap<String, ImuPlacement>(), next)
    }
}
