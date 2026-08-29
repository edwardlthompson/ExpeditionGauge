package dev.foss.expeditiongauge.screenshotexifstrip

import android.content.ContentValues

/** Drop MediaStore GPS keys and detect JPEG EXIF GPS IFD (0x8825). */
object ScreenshotExifStrip {
    private val gpsKeys = setOf(
        "latitude",
        "longitude",
        "latitude_ref",
        "longitude_ref",
    )

    fun isGpsKey(key: String): Boolean = key.lowercase() in gpsKeys

    fun dropGpsKeys(keys: Iterable<String>): List<String> =
        keys.filterNot { isGpsKey(it) }

    fun stripLocation(values: ContentValues) {
        gpsKeys.forEach { values.remove(it) }
    }

    fun containsGpsExif(jpeg: ByteArray): Boolean {
        if (jpeg.size < 4) return false
        return hasMarker(jpeg, byteArrayOf(0x88.toByte(), 0x25)) ||
            hasMarker(jpeg, byteArrayOf(0x25, 0x88.toByte())) ||
            hasMarker(jpeg, "GPS".toByteArray(Charsets.US_ASCII))
    }

    private fun hasMarker(haystack: ByteArray, needle: ByteArray): Boolean {
        if (needle.isEmpty() || haystack.size < needle.size) return false
        outer@ for (i in 0..haystack.size - needle.size) {
            for (j in needle.indices) {
                if (haystack[i + j] != needle[j]) continue@outer
            }
            return true
        }
        return false
    }
}
