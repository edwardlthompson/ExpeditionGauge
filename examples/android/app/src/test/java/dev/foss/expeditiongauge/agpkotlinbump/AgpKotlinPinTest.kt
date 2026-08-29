package dev.foss.expeditiongauge.agpkotlinbump

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AgpKotlinPinTest {
    @Test
    fun holdsCurrentToolchainAndSkipsAutomerge() {
        assertTrue(AgpKotlinPin.holds("9.2.1", "2.4.0"))
        assertFalse(AgpKotlinPin.holds("9.3.1", "2.4.10"))
        assertTrue(AgpKotlinPin.skipAutomerge("com.android.application"))
        assertTrue(AgpKotlinPin.skipAutomerge("org.jetbrains.kotlin.android"))
        assertFalse(AgpKotlinPin.skipAutomerge("androidx.core:core-ktx"))
        assertFalse(AgpKotlinPin.allowBump("9.3.1", "2.4.10"))
    }
}
