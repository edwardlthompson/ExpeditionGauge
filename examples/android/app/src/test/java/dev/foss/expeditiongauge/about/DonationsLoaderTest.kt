package dev.foss.expeditiongauge.about

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [26])
class DonationsLoaderTest {
    private val context: Context = ApplicationProvider.getApplicationContext()

    @Test
    fun loadsVenmoDonateLink() {
        val cfg = DonationsLoader.load(context)
        assertTrue(cfg.enabled)
        assertTrue(cfg.links.any { it.url == DonateLinks.VENMO_URL })
        assertEquals(DonateLinks.LABEL, cfg.links.first { it.url == DonateLinks.VENMO_URL }.label)
    }
}
