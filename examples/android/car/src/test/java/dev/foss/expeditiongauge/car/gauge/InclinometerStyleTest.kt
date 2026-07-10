package dev.foss.expeditiongauge.car.gauge

import org.junit.Assert.assertEquals
import org.junit.Test

class InclinometerStyleTest {
    @Test
    fun next_cyclesAllStyles() {
        assertEquals(InclinometerStyle.HORIZON, InclinometerStyle.LADDER.next())
        assertEquals(InclinometerStyle.DUAL_DIAL, InclinometerStyle.HORIZON.next())
        assertEquals(InclinometerStyle.BUBBLE, InclinometerStyle.DUAL_DIAL.next())
        assertEquals(InclinometerStyle.LADDER, InclinometerStyle.BUBBLE.next())
    }

    @Test
    fun storageRoundTrip() {
        InclinometerStyle.entries.forEach { style ->
            assertEquals(style, inclinometerStyleFromStorage(style.storageKey()))
        }
        assertEquals(InclinometerStyle.LADDER, inclinometerStyleFromStorage(null))
        assertEquals(InclinometerStyle.LADDER, inclinometerStyleFromStorage("unknown"))
    }
}
