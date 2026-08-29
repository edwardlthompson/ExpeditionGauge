package dev.foss.expeditiongauge.tpmstempcomp

import org.junit.Assert.assertEquals
import org.junit.Test

class TpmsTempCompTest {
    @Test
    fun coolsPressureTowardReference() {
        val hot = TpmsTempComp.compensateKpa(240f, 50f)
        assertEquals(true, hot < 240f)
        assertEquals(240f, TpmsTempComp.compensateKpa(240f, 20f), 0.01f)
    }
}
