package dev.foss.expeditiongauge.blepermissionrationale

import org.junit.Assert.assertTrue
import org.junit.Test

class BlePermissionRationaleTest {
    @Test
    fun mentionsPairingOnly() {
        val text = BlePermissionRationale.forScan()
        assertTrue(text.contains("pair"))
        assertTrue(text.contains("uploaded").not() || text.contains("Nothing is uploaded"))
    }
}
