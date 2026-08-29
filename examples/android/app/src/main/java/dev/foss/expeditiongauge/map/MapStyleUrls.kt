package dev.foss.expeditiongauge.map

import dev.foss.expeditiongauge.fossmapstyles.FossMapStyles

/** FOSS basemap style used for playback and offline tile packs. */
object MapStyleUrls {
    val DEMO_STYLE: String
        get() = FossMapStyles.url()
}
