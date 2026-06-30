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

    @Test
    fun firesPitchAndRollAlerts() {
        val engine = AlertEngine(AlertThresholds(masterEnabled = true, maxPitchDeg = 10f, maxRollDeg = 12f))
        val events = engine.evaluate(
            TelemetrySnapshot(timestampMs = 1000L, pitchDeg = 15f, rollDeg = -13f),
        )
        assertEquals(2, events.size)
        assertTrue(events.any { it.type == AlertType.PITCH })
        assertTrue(events.any { it.type == AlertType.ROLL })
    }

    @Test
    fun rpmOnlyWhenObdEvaluated() {
        val engine = AlertEngine(AlertThresholds(masterEnabled = true, maxRpm = 3000f))
        val withoutObd = engine.evaluate(TelemetrySnapshot(timestampMs = 1000L, rpm = 4000f))
        assertTrue(withoutObd.none { it.type == AlertType.RPM })
        val withObd = engine.evaluateObd(rpm = 4000f, slipRatio = null, fuelRateLph = null, speedMps = 10f, nowMs = 1000L)
        assertEquals(1, withObd.size)
        assertEquals(AlertType.RPM, withObd.first().type)
    }

    @Test
    fun tirePressureAlert() {
        val engine = AlertEngine(AlertThresholds(masterEnabled = true, minTirePressureKpa = 200f))
        val tracker = TpmsPressureTracker()
        val tpms = dev.foss.expeditiongauge.telemetry.TpmsSnapshot(
            frontLeft = dev.foss.expeditiongauge.telemetry.TpmsCornerReading(pressureKpa = 180f),
        )
        val events = engine.evaluateTpms(tpms, 1000L, tracker)
        assertEquals(1, events.size)
        assertEquals(AlertType.TIRE_PRESSURE, events.first().type)
    }
}
