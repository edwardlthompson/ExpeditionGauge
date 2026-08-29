package dev.foss.expeditiongauge.crawlhud

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class CrawlHudDeclutterTest {
    @Test
    fun hidesGpsAndHeadingOnlyWhileCrawling() {
        assertTrue(CrawlHudDeclutter.hideGpsExtras(crawling = true))
        assertFalse(CrawlHudDeclutter.hideGpsExtras(crawling = false))
        assertFalse(CrawlHudDeclutter.showHeading(crawling = true, presetShowHeading = true))
        assertTrue(CrawlHudDeclutter.showHeading(crawling = false, presetShowHeading = true))
        assertFalse(CrawlHudDeclutter.showAltitude(crawling = true, presetShowGps = true))
        assertTrue(CrawlHudDeclutter.showAltitude(crawling = false, presetShowGps = true))
    }
}
