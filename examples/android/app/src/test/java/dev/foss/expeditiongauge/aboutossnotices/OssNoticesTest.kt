package dev.foss.expeditiongauge.aboutossnotices

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class OssNoticesTest {
    @Test
    fun requiresMitMaplibreAndAndroidx() {
        assertTrue(OssNotices.complete(listOf("MIT License", "MapLibre GL", "AndroidX Core")))
        assertFalse(OssNotices.complete(listOf("MIT")))
        assertEquals("MIT · MapLibre · AndroidX", OssNotices.summary())
    }
}
