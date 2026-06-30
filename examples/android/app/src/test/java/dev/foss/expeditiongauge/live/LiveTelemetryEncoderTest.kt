package dev.foss.expeditiongauge.live

import dev.foss.expeditiongauge.telemetry.TelemetrySnapshot
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.json.JSONObject
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [26])
class LiveTelemetryEncoderTest {
    private val encoder = LiveTelemetryEncoder(minIntervalMs = 0L)

    @Test
    fun encodeIfChanged_emitsCompactJson() {
        val snap = TelemetrySnapshot.empty().copy(timestampMs = 1000L, speedMps = 12f, latG = 0.5f)
        val json = encoder.encodeIfChanged(snap)
        assertNotNull(json)
        val obj = JSONObject(json!!)
        assertEquals(12.0, obj.getDouble("speed"), 0.01)
        assertEquals(0.5, obj.getDouble("latG"), 0.01)
    }

    @Test
    fun encodeIfChanged_skipsWhenUnchanged() {
        val snap = TelemetrySnapshot.empty().copy(timestampMs = 2000L, speedMps = 10f)
        encoder.encodeIfChanged(snap)
        assertNull(encoder.encodeIfChanged(snap.copy(timestampMs = 2001L)))
    }

    @Test
    fun encodeIfChanged_includesTpms() {
        val tpms = """{"fl":{"kpa":220.0}}"""
        val snap = TelemetrySnapshot.empty().copy(timestampMs = 3000L, speedMps = 20f)
        val json = encoder.encodeIfChanged(snap, tpms)
        assertNotNull(json)
        assertTrue(JSONObject(json!!).has("tpms"))
    }

    @Test
    fun reset_clearsState() {
        val snap = TelemetrySnapshot.empty().copy(timestampMs = 4000L, speedMps = 30f)
        encoder.encodeIfChanged(snap)
        encoder.reset()
        assertNotNull(encoder.encodeIfChanged(snap.copy(timestampMs = 4001L)))
    }
}
