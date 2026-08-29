package dev.foss.expeditiongauge.aaosstandalone

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AaosStandaloneTest {
    @Test
    fun suffixesIdAndRequiresAutomotiveOnlyWhenStandalone() {
        assertEquals(
            "dev.foss.expeditiongauge",
            AaosStandalone.applicationId("dev.foss.expeditiongauge", false),
        )
        assertEquals(
            "dev.foss.expeditiongauge.aaos",
            AaosStandalone.applicationId("dev.foss.expeditiongauge", true),
        )
        assertEquals("2.18.12-aaos", AaosStandalone.versionName("2.18.12", true))
        assertFalse(AaosStandalone.automotiveRequired(false))
        assertTrue(AaosStandalone.automotiveRequired(true))
    }
}
