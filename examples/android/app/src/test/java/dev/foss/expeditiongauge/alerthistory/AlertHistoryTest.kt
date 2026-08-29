package dev.foss.expeditiongauge.alerthistory

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class AlertHistoryTest {
    @Test
    fun prependsCapsAndRoundTrips() {
        val first = AlertHistoryEntry("SPEED", 40f, 30f, 1L)
        val second = AlertHistoryEntry("RPM", 6000f, 5500f, 2L)
        val two = AlertHistory.append(listOf(first), second)
        assertEquals(listOf(second, first), two)
        val filled = (1..AlertHistory.MAX + 3).fold(emptyList<AlertHistoryEntry>()) { acc, i ->
            AlertHistory.append(acc, AlertHistoryEntry("SPEED", i.toFloat(), 1f, i.toLong()))
        }
        assertEquals(AlertHistory.MAX, filled.size)
        assertEquals(AlertHistory.MAX + 3f, filled.first().value)
        assertEquals(two, AlertHistory.decode(AlertHistory.encode(two)))
        assertTrue(AlertHistory.decode(null).isEmpty())
    }
}
