package dev.foss.expeditiongauge.alerts

import dev.foss.expeditiongauge.telemetry.TelemetrySnapshot
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class AlertEngineTest {
    @Test
    fun disabledWhenMasterOff() {
        val engine = AlertEngine(AlertThresholds(masterEnabled = false, maxLatG = 1f))
        assertTrue(engine.evaluate(TelemetrySnapshot(latG = 2f)).isEmpty())
    }

    @Test
    fun firesLatGAlert() {
        val engine = AlertEngine(AlertThresholds(masterEnabled = true, maxLatG = 1f))
        val events = engine.evaluate(TelemetrySnapshot(timestampMs = 1000L, latG = 1.5f))
        assertEquals(1, events.size)
        assertEquals(AlertType.LAT_G, events.first().type)
    }

    @Test
    fun cooldownPreventsSpam() {
        val engine = AlertEngine(AlertThresholds(masterEnabled = true, maxLatG = 1f, cooldownMs = 5000L))
        engine.evaluate(TelemetrySnapshot(timestampMs = 1000L, latG = 1.5f))
        val second = engine.evaluate(TelemetrySnapshot(timestampMs = 2000L, latG = 1.5f))
        assertTrue(second.isEmpty())
    }

    @Test
    fun fuelEconomyComputedFromObd() {
        val engine = AlertEngine()
        val kmpl = engine.computeFuelEconomyKmpl(speedMps = 27.78f, fuelRateLph = 10f)
        assertEquals(10f, kmpl!!, 0.1f)
    }
}
