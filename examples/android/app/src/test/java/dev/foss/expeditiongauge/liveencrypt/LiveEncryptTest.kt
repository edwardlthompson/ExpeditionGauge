package dev.foss.expeditiongauge.liveencrypt

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class LiveEncryptTest {
    @Test
    fun sealsAndOpensWithKey() {
        val sealed = LiveEncrypt.apply("hello", "ab")
        assertEquals("hello", LiveEncrypt.open(sealed, "ab"))
        assertNull(LiveEncrypt.open(sealed, null))
        assertEquals("plain", LiveEncrypt.apply("plain", null))
    }
}
