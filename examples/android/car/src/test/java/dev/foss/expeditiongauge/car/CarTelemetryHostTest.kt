package dev.foss.expeditiongauge.car

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CarTelemetryHostTest {
    @Test
    fun buildRows_respectsAllowlistAndPriority() {
        val metrics = mapOf(
            "speed" to "42 km/h",
            "latG" to "0.85 G",
            "pitch" to "+2°",
            "roll" to "-1°",
            "beta" to "+5°",
            "rpm" to "3200 rpm",
            "throttle" to "45%",
        )
        val allowlist = setOf("speed", "latG", "beta")

        val rows = CarTelemetryHost.buildRows(metrics, allowlist)

        assertEquals(listOf("speed", "latG", "beta"), rows.map { it.key })
        assertEquals("Speed", rows[0].title)
        assertEquals("42 km/h", rows[0].value)
    }

    @Test
    fun buildRows_skipsBlankValues() {
        val metrics = mapOf(
            "speed" to "10 km/h",
            "latG" to "",
            "pitch" to "  ",
        )
        val rows = CarTelemetryHost.buildRows(metrics, setOf("speed", "latG", "pitch"))

        assertEquals(1, rows.size)
        assertEquals("speed", rows.single().key)
    }

    @Test
    fun defaultPriority_includesCoreDrivingMetrics() {
        assertTrue(CarTelemetryHost.defaultPriority.contains("speed"))
        assertTrue(CarTelemetryHost.defaultPriority.contains("latG"))
        assertTrue(CarTelemetryHost.defaultPriority.first() == "speed")
    }
}
