package dev.foss.expeditiongauge.saffolderpicker

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SafFolderPickerTest {
    @Test
    fun recognizesTreeUrisAndPersistFlags() {
        assertTrue(SafFolderPicker.isTreeUri("content://com.android.externalstorage.documents/tree/primary%3AEG"))
        assertFalse(SafFolderPicker.isTreeUri("https://example.com/file"))
        val flags = SafFolderPicker.persistFlags()
        assertTrue(flags and android.content.Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION != 0)
        assertTrue(flags and android.content.Intent.FLAG_GRANT_WRITE_URI_PERMISSION != 0)
    }
}
