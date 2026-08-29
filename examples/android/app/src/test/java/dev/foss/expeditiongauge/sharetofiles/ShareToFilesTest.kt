package dev.foss.expeditiongauge.sharetofiles

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ShareToFilesTest {
    @Test
    fun prefersDocumentsUiAndFiles() {
        assertTrue(ShareToFiles.prefersFiles("com.android.documentsui"))
        assertTrue(ShareToFiles.prefersFiles("com.google.android.documentsui"))
        assertTrue(ShareToFiles.prefersFiles("com.android.files"))
        assertFalse(ShareToFiles.prefersFiles("com.twitter.android"))
        assertFalse(ShareToFiles.prefersFiles("com.instagram.android"))
    }
}
