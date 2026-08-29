package dev.foss.expeditiongauge.thermalloginterval

import dev.foss.expeditiongauge.batterysaverrecord.BatterySaverRecord
import dev.foss.expeditiongauge.thermal.ThermalStatus
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ThermalLogIntervalTest {
    @Test
    fun idleDoesNotChangeInterval() {
        assertNull(ThermalLogInterval.autoIntervalMs(ThermalStatus.Critical, recording = false))
    }

    @Test
    fun warningWhileRecordingUses20Hz() {
        assertEquals(50L, ThermalLogInterval.autoIntervalMs(ThermalStatus.Warning, recording = true))
    }

    @Test
    fun batterySaverNeverGoesFasterThanFiveHz() {
        assertEquals(
            BatterySaverRecord.INTERVAL_MS,
            ThermalLogInterval.autoIntervalMs(
                ThermalStatus.Warning,
                recording = true,
                batterySaver = true,
            ),
        )
    }
}
