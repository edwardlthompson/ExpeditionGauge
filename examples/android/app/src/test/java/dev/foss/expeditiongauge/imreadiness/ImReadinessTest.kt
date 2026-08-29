package dev.foss.expeditiongauge.imreadiness

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ImReadinessTest {
    @Test
    fun parseSparkIncompleteEvap() {
        val report = ImReadiness.parse("410181076504")!!
        assertEquals(listOf("EVAP"), report.incomplete)
        assertTrue(report.monitors.any { it.name == "MIS" && it.ready })
        assertEquals("I/M not ready: EVAP", ImReadiness.line(report))
    }

    @Test
    fun parseAllReadyContinuous() {
        val report = ImReadiness.parse("410100070000")!!
        assertEquals(emptyList<String>(), report.incomplete)
        assertEquals(3, report.supported)
        assertEquals("I/M ready (3/3)", ImReadiness.line(report))
    }

    @Test
    fun parseNoneOrNoise() {
        assertNull(ImReadiness.parse("410100000000"))
        assertNull(ImReadiness.parse("NO DATA"))
        assertNull(ImReadiness.parse(null))
    }
}
