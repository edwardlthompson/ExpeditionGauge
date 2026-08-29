package dev.foss.expeditiongauge.feedback

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [26])
class FeedbackPrefsTest {
    private val context: Context = ApplicationProvider.getApplicationContext()

    @Test
    fun defaultsOffAndClearsWhenDisabled() {
        val prefs = FeedbackPrefs(context)
        assertFalse(prefs.saveCrashes())
        prefs.setSaveCrashes(true)
        assertTrue(prefs.saveCrashes())
        prefs.setSaveCrashes(false)
        assertFalse(prefs.saveCrashes())
    }
}
