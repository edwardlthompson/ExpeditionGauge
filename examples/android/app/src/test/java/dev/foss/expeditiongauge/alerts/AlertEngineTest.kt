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
    fun cooldownPreventsSpamWithinWindow() {
        val engine = AlertEngine(AlertThresholds(masterEnabled = true, maxLatG = 1f, cooldownMs = 5000L))
        engine.evaluate(TelemetrySnapshot(timestampMs = 1000L, latG = 1.5f))
        val second = engine.evaluate(TelemetrySnapshot(timestampMs = 2000L, latG = 1.5f))
        assertTrue(second.isEmpty())
    }

    @Test
    fun levelTriggeredActiveStaysWhileOverLimit() {
        val engine = AlertEngine(AlertThresholds(masterEnabled = true, maxSpeedMps = 20f, cooldownMs = 1000L))
        val active1 = engine.evaluateActive(TelemetrySnapshot(timestampMs = 1000L, speedMps = 25f))
        val active2 = engine.evaluateActive(TelemetrySnapshot(timestampMs = 1500L, speedMps = 25f))
        assertEquals(1, active1.size)
        assertEquals(1, active2.size)
        assertEquals(AlertType.SPEED, active2.first().type)
    }

    @Test
    fun feedbackRepeatsAfterOneSecond() {
        val engine = AlertEngine(AlertThresholds(masterEnabled = true, maxSpeedMps = 20f, cooldownMs = 1000L))
        val first = engine.evaluate(TelemetrySnapshot(timestampMs = 1000L, speedMps = 25f))
        val mid = engine.evaluate(TelemetrySnapshot(timestampMs = 1500L, speedMps = 25f))
        val again = engine.evaluate(TelemetrySnapshot(timestampMs = 2100L, speedMps = 25f))
        assertEquals(1, first.size)
        assertTrue(mid.isEmpty())
        assertEquals(1, again.size)
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
    fun tirePressureAlertIncludesCorner() {
        val engine = AlertEngine(AlertThresholds(masterEnabled = true, minTirePressureKpa = 200f))
        val tracker = TpmsPressureTracker()
        val tpms = dev.foss.expeditiongauge.telemetry.TpmsSnapshot(
            frontRight = dev.foss.expeditiongauge.telemetry.TpmsCornerReading(pressureKpa = 180f),
        )
        val events = engine.evaluateTpms(tpms, 1000L, tracker)
        assertEquals(1, events.size)
        assertEquals(AlertType.TIRE_PRESSURE, events.first().type)
        assertEquals(TireCornerId.FR, events.first().tireCorner)
    }

    @Test
    fun startupGraceSuppressesPitchAndRollOnly() {
        assertTrue(AlertStartupGrace.suppressFeedback(AlertType.PITCH, 0L, attitudeSettled = true))
        assertTrue(AlertStartupGrace.suppressFeedback(AlertType.ROLL, 500L, attitudeSettled = true))
        assertTrue(!AlertStartupGrace.suppressFeedback(AlertType.LAT_G, 0L, attitudeSettled = false))
        assertTrue(
            !AlertStartupGrace.suppressFeedback(
                AlertType.PITCH,
                AlertStartupGrace.ATTITUDE_GRACE_MS,
                attitudeSettled = true,
            ),
        )
        assertTrue(
            AlertStartupGrace.suppressFeedback(
                AlertType.PITCH,
                AlertStartupGrace.ATTITUDE_GRACE_MS + 1,
                attitudeSettled = false,
            ),
        )
    }

    @Test
    fun attitudeSettleGateWaitsForStableWindow() {
        val gate = AttitudeSettleGate(stableWindowMs = 1_000L, maxDeltaDeg = 2.5f, minSamples = 3)
        assertTrue(!gate.onSample(-60f, 0f, 0L))
        assertTrue(!gate.onSample(-59f, 0.5f, 400L))
        assertTrue(!gate.onSample(-58.5f, 0.2f, 800L))
        assertTrue(gate.onSample(-58.8f, 0.3f, 1_100L))
        assertTrue(gate.isSettled())
    }

    @Test
    fun attitudeSettleGateResetsWhenSwingContinues() {
        val gate = AttitudeSettleGate(stableWindowMs = 1_000L, maxDeltaDeg = 2.5f, minSamples = 3)
        gate.onSample(-50f, 0f, 0L)
        gate.onSample(-49f, 0f, 500L)
        assertTrue(!gate.onSample(-40f, 0f, 600L)) // swing resets window
        assertTrue(!gate.isSettled())
        gate.onSample(-40.5f, 0.2f, 1_200L)
        gate.onSample(-40.2f, 0.1f, 1_600L)
        assertTrue(gate.onSample(-40.0f, 0.0f, 2_200L))
    }

    @Test
    fun attitudeSettleGateResettlesAfterLargeJump() {
        val gate = AttitudeSettleGate(stableWindowMs = 500L, maxDeltaDeg = 2.5f, minSamples = 2)
        gate.onSample(0f, 0f, 0L)
        gate.onSample(0.5f, 0f, 300L)
        assertTrue(gate.onSample(0.2f, 0f, 600L))
        assertTrue(!gate.onSample(40f, 0f, 700L)) // Madgwick-style jump
        assertTrue(!gate.isSettled())
    }
}
