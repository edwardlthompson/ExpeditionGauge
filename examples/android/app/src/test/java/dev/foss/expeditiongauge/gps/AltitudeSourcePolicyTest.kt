package dev.foss.expeditiongauge.gps

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class AltitudeSourcePolicyTest {
    @Test
    fun preferDem_whenFewSatellites() {
        assertTrue(AltitudeSourcePolicy.preferDemElevation(numSatellites = 4, verticalAccuracyM = 5f))
    }

    @Test
    fun preferGps_whenEnoughSatsAndTightVertical() {
        assertFalse(AltitudeSourcePolicy.preferDemElevation(numSatellites = 8, verticalAccuracyM = 8f))
    }

    @Test
    fun preferDem_whenVerticalAccuracyPoor() {
        assertTrue(AltitudeSourcePolicy.preferDemElevation(numSatellites = 10, verticalAccuracyM = 42f))
    }

    @Test
    fun preferDem_whenNoQualityMetadata() {
        assertTrue(AltitudeSourcePolicy.preferDemElevation(numSatellites = null, verticalAccuracyM = null))
    }

    @Test
    fun resolve_usesDemWhenPreferred() {
        assertEquals(
            2.4,
            AltitudeSourcePolicy.resolveMeters(gpsMslM = -29.5, demM = 2.4, preferDem = true),
            0.01,
        )
    }

    @Test
    fun resolve_keepsGpsWhenDemMissing() {
        assertEquals(
            -29.5,
            AltitudeSourcePolicy.resolveMeters(gpsMslM = -29.5, demM = null, preferDem = true),
            0.01,
        )
    }
}

class UsgsEpqsClientTest {
    @Test
    fun parseElevationMeters_numericValue() {
        val json =
            """{"location":{"x":-66.18,"y":18.45},"value":2.379143238,"rasterId":98199}"""
        assertEquals(2.379143238, UsgsEpqsClient.parseElevationMeters(json)!!, 1e-6)
    }

    @Test
    fun parseElevationMeters_stringValue() {
        val json = """{"value":"2.379143238"}"""
        assertEquals(2.379143238, UsgsEpqsClient.parseElevationMeters(json)!!, 1e-6)
    }

    @Test
    fun parseElevationMeters_rejectsSentinel() {
        assertNull(UsgsEpqsClient.parseElevationMeters("""{"value":-1000000}"""))
    }

    @Test
    fun buildUrl_ordersLonLat() {
        val url = UsgsEpqsClient.buildUrlMeters(18.457295, -66.18459)
        assertTrue(url.contains("x=-66.18459"))
        assertTrue(url.contains("y=18.457295"))
        assertTrue(url.contains("units=Meters"))
    }
}
