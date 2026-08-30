package dev.foss.expeditiongauge.crawlhud

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class CrawlHudDeclutterTest {
    @Test
    fun hidesGpsAndHeadingOnlyWhileRecordingCrawl() {
        assertFalse(CrawlHudDeclutter.hideGpsExtras(recording = false, crawlMode = true))
        assertFalse(CrawlHudDeclutter.hideGpsExtras(recording = true, crawlMode = false))
        assertTrue(CrawlHudDeclutter.hideGpsExtras(recording = true, crawlMode = true))
        assertFalse(CrawlHudDeclutter.showHeading(hideGpsExtras = true, presetShowHeading = true))
        assertTrue(CrawlHudDeclutter.showHeading(hideGpsExtras = false, presetShowHeading = true))
        assertFalse(CrawlHudDeclutter.showAltitude(hideGpsExtras = true, presetShowGps = true))
        assertTrue(CrawlHudDeclutter.showAltitude(hideGpsExtras = false, presetShowGps = true))
    }
}
