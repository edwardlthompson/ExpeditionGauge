package dev.foss.expeditiongauge.recording

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class BluetoothConnectionHelperTest {
    @Test
    fun firstAllowlistedAddress_returnsFirstMatchInConnectionOrder() {
        val allowlist = setOf("AA:BB:CC:DD:EE:01", "AA:BB:CC:DD:EE:02")
        val connected = listOf("AA:BB:CC:DD:EE:99", "AA:BB:CC:DD:EE:02", "AA:BB:CC:DD:EE:01")

        assertEquals("AA:BB:CC:DD:EE:02", BluetoothConnectionHelper.firstAllowlistedAddress(allowlist, connected))
    }

    @Test
    fun firstAllowlistedAddress_ignoresBondedOnlyDevicesNotInConnectedList() {
        val allowlist = setOf("AA:BB:CC:DD:EE:01")
        val connected = emptyList<String>()

        assertNull(BluetoothConnectionHelper.firstAllowlistedAddress(allowlist, connected))
    }

    @Test
    fun firstAllowlistedAddress_emptyAllowlistReturnsNull() {
        assertNull(BluetoothConnectionHelper.firstAllowlistedAddress(emptySet(), listOf("AA:BB:CC:DD:EE:01")))
    }
}
