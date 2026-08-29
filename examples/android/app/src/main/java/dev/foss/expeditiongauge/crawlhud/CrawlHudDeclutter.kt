package dev.foss.expeditiongauge.crawlhud

object CrawlHudDeclutter {
    fun hideGpsExtras(crawling: Boolean): Boolean = crawling

    fun showHeading(crawling: Boolean, presetShowHeading: Boolean): Boolean =
        presetShowHeading && !crawling

    fun showAltitude(crawling: Boolean, presetShowGps: Boolean): Boolean =
        presetShowGps && !crawling
}
