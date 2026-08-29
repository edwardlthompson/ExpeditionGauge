package dev.foss.expeditiongauge.settingsqrtransfer

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class SettingsQrTransferTest {
    @Test
    fun framesAndParsesBackupBlob() {
        val payload = SettingsQrTransfer.encodePairs(mapOf("speed_unit" to "imperial"))
        assertTrue(payload.startsWith("egset|v1|"))
        assertEquals("speed_unit=imperial", SettingsQrTransfer.parse(payload))
        assertNull(SettingsQrTransfer.parse("https://evil.example/qr"))
        assertNull(SettingsQrTransfer.parse("egset|v1|obd_device=AA:BB"))
    }
}
