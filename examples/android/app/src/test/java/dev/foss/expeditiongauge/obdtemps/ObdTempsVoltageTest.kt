package dev.foss.expeditiongauge.obdtemps

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ObdTempsVoltageTest {
    @Test
    fun parseCoolantOilAndVoltage() {
        assertEquals(50f, ObdTempsVoltage.parseCoolantC("41 05 5A")!!, 0.01f)
        assertEquals(90f, ObdTempsVoltage.parseOilC("41 5C 82")!!, 0.01f)
        assertEquals(13.676f, ObdTempsVoltage.parseVoltage("41 42 35 6C")!!, 0.001f)
        assertNull(ObdTempsVoltage.parseCoolantC("NO DATA"))
        assertNull(ObdTempsVoltage.parseOilC("UNABLE TO CONNECT"))
    }

    @Test
    fun lineJoinsPresentFieldsWhenParked() {
        val snap = ObdTempsVoltageSnapshot(coolantC = 90f, oilC = 95f, voltage = 13.8f)
        val line = ObdTempsVoltage.line(snap)
        assertTrue(line!!.contains("ECT 90°C"))
        assertTrue(line.contains("Oil 95°C"))
        assertTrue(line.contains("Batt 13.8V"))
        assertTrue(ObdTempsVoltage.matches(line))
        assertNull(ObdTempsVoltage.line(snap, parked = false))
        assertNull(ObdTempsVoltage.line(ObdTempsVoltageSnapshot()))
        assertTrue(ObdTempsVoltage.parked(null))
        assertTrue(ObdTempsVoltage.parked(0.2f))
        assertFalse(ObdTempsVoltage.parked(5f))
    }
}
