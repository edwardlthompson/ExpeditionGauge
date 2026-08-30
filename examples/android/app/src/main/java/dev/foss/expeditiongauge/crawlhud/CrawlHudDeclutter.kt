package dev.foss.expeditiongauge.crawlhud

object CrawlHudDeclutter {
    fun hideGpsExtras(recording: Boolean, crawlMode: Boolean): Boolean =
        recording && crawlMode

    fun showHeading(hideGpsExtras: Boolean, presetShowHeading: Boolean): Boolean =
        presetShowHeading && !hideGpsExtras

    fun showAltitude(hideGpsExtras: Boolean, presetShowGps: Boolean): Boolean =
        presetShowGps && !hideGpsExtras
}
