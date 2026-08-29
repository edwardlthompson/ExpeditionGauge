package dev.foss.expeditiongauge.inclinometerzeroprofile

import org.junit.Assert.assertEquals
import org.junit.Test

class InclinometerZeroProfileTest {
    @Test
    fun encodesPerVehicle() {
        val offset = ZeroOffset(1.5f, -0.5f)
        assertEquals("zero:truck", InclinometerZeroProfile.key("Truck"))
        assertEquals(offset, InclinometerZeroProfile.decode(InclinometerZeroProfile.encode(offset)))
        val map = InclinometerZeroProfile.parseAll("zero:truck=1.5|-0.5")
        assertEquals(-0.5f, map.getValue("zero:truck").rollDeg)
    }
}
