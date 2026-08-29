package dev.foss.expeditiongauge.encryptedsessionzip

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class EncryptedSessionZipTest {
    @Test
    fun sealsAndOpensListing() {
        val sealed = EncryptedSessionZip.seal(listOf("run.csv", "run.gpx"), "k1")
        assertTrue(sealed.startsWith("zip|"))
        assertEquals(listOf("run.csv", "run.gpx"), EncryptedSessionZip.open(sealed, "k1"))
        assertTrue(EncryptedSessionZip.open(sealed, "k1") != EncryptedSessionZip.open(sealed, "k2"))
        assertNull(EncryptedSessionZip.open("not-a-zip", "k1"))
        assertEquals(listOf("a.csv"), EncryptedSessionZip.open(EncryptedSessionZip.seal(listOf("a.csv"), ""), ""))
    }
}
