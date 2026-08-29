package dev.foss.expeditiongauge.settingsjsonbackup

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SettingsJsonBackupTest {
    @Test
    fun roundTripsAllowedKeysAndDropsSecrets() {
        val blob = SettingsJsonBackup.encode(
            mapOf(
                "speed_unit" to "imperial",
                "log_interval_ms" to "50",
                "live_telemetry" to "true",
                "obd_device" to "AA:BB",
                "temp_unit" to "c|el=sius",
            ),
        )
        assertTrue(!blob.contains("AA:BB"))
        val decoded = SettingsJsonBackup.decode(blob)
        assertEquals("imperial", decoded["speed_unit"])
        assertEquals("50", decoded["log_interval_ms"])
        assertEquals("true", decoded["live_telemetry"])
        assertEquals("c|el=sius", decoded["temp_unit"])
        assertTrue("obd_device" !in decoded)
        assertTrue("obd_device" !in SettingsJsonBackup.decode("obd_device=AA:BB|speed_unit=metric"))
    }
}
