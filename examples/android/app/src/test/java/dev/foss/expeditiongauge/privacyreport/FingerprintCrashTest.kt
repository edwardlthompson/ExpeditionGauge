package dev.foss.expeditiongauge.privacyreport

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test

class FingerprintCrashTest {
    @Test
    fun stableWhenOnlyUsernameChanges() {
        val a = FingerprintCrash.of("at C:\\Users\\Ada\\x.kt:1", "TypeError")
        val b = FingerprintCrash.of("at C:\\Users\\Bob\\x.kt:1", "TypeError")
        assertEquals(a, b)
        assertEquals(12, a.length)
    }

    @Test
    fun changesWhenFramesChange() {
        val a = FingerprintCrash.of("at Foo.kt:1\nat Bar.kt:2", "Error")
        val b = FingerprintCrash.of("at Foo.kt:1\nat Baz.kt:9", "Error")
        assertNotEquals(a, b)
    }
}
