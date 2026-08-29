package dev.foss.expeditiongauge.vehiclepidmaps

import org.junit.Assert.assertEquals
import org.junit.Test

class VehiclePidMapsTest {
    @Test
    fun parsesAndFallsBack() {
        assertEquals(listOf("11", "0C"), VehiclePidMaps.parse("11, 0c, zz"))
        assertEquals(VehiclePidMaps.DEFAULT, VehiclePidMaps.pidsFor("unknown"))
        assertEquals(listOf("22"), VehiclePidMaps.pidsFor("truck", mapOf("truck" to listOf("22"))))
    }
}
