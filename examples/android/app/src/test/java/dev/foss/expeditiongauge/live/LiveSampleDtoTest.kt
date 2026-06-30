package dev.foss.expeditiongauge.live

import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [26])
class LiveSampleDtoTest {
    @Test
    fun toJson_roundTripFields() {
        val dto = LiveSampleDto(
            timestampMs = 100L,
            speedMps = 5.5f,
            latG = 0.8f,
            betaDeg = 12f,
            pitchDeg = 1f,
            rollDeg = 2f,
            headingDeg = 90f,
            tpmsJson = """{"fl":{"kpa":200}}""",
        )
        val obj = JSONObject(dto.toJson())
        assertEquals(100L, obj.getLong("t"))
        assertEquals(5.5, obj.getDouble("speed"), 0.01)
        assertEquals(12.0, obj.getDouble("beta"), 0.01)
        assertEquals(200.0, obj.getJSONObject("tpms").getJSONObject("fl").getDouble("kpa"), 0.01)
    }

    @Test
    fun toJson_omitsBetaWhenNull() {
        val dto = LiveSampleDto(
            timestampMs = 1L,
            speedMps = 0f,
            latG = 0f,
            betaDeg = null,
            pitchDeg = 0f,
            rollDeg = 0f,
            headingDeg = 0f,
        )
        assertNull(JSONObject(dto.toJson()).opt("beta"))
    }
}
