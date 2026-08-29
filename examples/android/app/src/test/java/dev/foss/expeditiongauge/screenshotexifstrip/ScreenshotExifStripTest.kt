package dev.foss.expeditiongauge.screenshotexifstrip

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ScreenshotExifStripTest {
    @Test
    fun dropsGpsKeysAndKeepsDisplayName() {
        val kept = ScreenshotExifStrip.dropGpsKeys(
            listOf("display_name", "latitude", "longitude", "mime_type", "LATITUDE"),
        )
        assertEquals(listOf("display_name", "mime_type"), kept)
        assertTrue(ScreenshotExifStrip.isGpsKey("longitude_ref"))
        assertFalse(ScreenshotExifStrip.isGpsKey("relative_path"))
    }

    @Test
    fun detectsGpsIfdAndAscii() {
        val clean = byteArrayOf(0xFF.toByte(), 0xD8.toByte(), 0x00, 0x10)
        assertFalse(ScreenshotExifStrip.containsGpsExif(clean))
        val ifd = clean + byteArrayOf(0x88.toByte(), 0x25)
        assertTrue(ScreenshotExifStrip.containsGpsExif(ifd))
        val ascii = "JFIF GPS lat".toByteArray(Charsets.US_ASCII)
        assertTrue(ScreenshotExifStrip.containsGpsExif(ascii))
    }
}
