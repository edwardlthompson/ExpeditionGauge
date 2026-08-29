package dev.foss.expeditiongauge.wifielm

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class WifiElm327Test {
    @Test
    fun parseDefaultAndPrivateLan() {
        val def = WifiElm327.parse("tcp:192.168.0.10:35000")!!
        assertEquals("192.168.0.10", def.host)
        assertEquals(35_000, def.port)
        assertEquals("tcp:10.0.0.5:23", WifiElm327.encode("10.0.0.5", 23))
        assertEquals(23, WifiElm327.parseDisplay("10.0.0.5:23")!!.port)
        assertTrue(WifiElm327.isWifi("tcp:192.168.1.1:35000"))
        assertFalse(WifiElm327.isWifi("AA:BB:CC:DD:EE:FF"))
    }

    @Test
    fun rejectsPublicAndBluetoothMac() {
        assertNull(WifiElm327.parse("AA:BB:CC:DD:EE:FF"))
        assertNull(WifiElm327.parse("tcp:8.8.8.8:35000"))
        assertNull(WifiElm327.parse("tcp:example.com:35000"))
        assertNull(WifiElm327.parseDisplay("1.2.3.4:35000"))
        assertTrue(WifiElm327.privateHost("172.16.1.9"))
        assertFalse(WifiElm327.privateHost("172.32.0.1"))
    }
}
