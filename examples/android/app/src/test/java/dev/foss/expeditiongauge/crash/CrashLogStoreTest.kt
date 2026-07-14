package dev.foss.expeditiongauge.crash

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

class CrashLogStoreTest {
    @get:Rule
    val tmp = TemporaryFolder()

    @Test
    fun writeReadClearRoundTrip() {
        val store = CrashLogStore(tmp.newFolder("crash"), maxBytes = 4096)
        store.write(IllegalStateException("grid boom"), "2.16.0 (34)", "main")
        val text = store.readText()!!
        assertTrue(text.contains("grid boom"))
        assertTrue(text.contains("2.16.0"))
        assertTrue(text.contains("IllegalStateException"))
        assertTrue(store.previewLines(5)!!.lines().size <= 5)
        store.clear()
        assertNull(store.readText())
    }

    @Test
    fun enforcesSizeCap() {
        val store = CrashLogStore(tmp.newFolder("crash"), maxBytes = 200)
        val huge = RuntimeException("x".repeat(5000))
        store.write(huge, "v", "t")
        val text = store.readText()!!
        assertTrue(text.length <= 220)
        assertTrue(text.contains("truncated"))
    }

    @Test
    fun previewNullWhenEmpty() {
        val store = CrashLogStore(tmp.newFolder("crash"))
        assertNull(store.previewLines())
        assertEquals(null, store.readText())
    }
}
