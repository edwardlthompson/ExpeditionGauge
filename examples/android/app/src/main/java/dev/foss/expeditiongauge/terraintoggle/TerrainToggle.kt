package dev.foss.expeditiongauge.terraintoggle

/** Pick a hillshade style URL when terrain is on. */
object TerrainToggle {
    const val HILLSHADE_STYLE = "https://tiles.openfreemap.org/styles/fiord"

    fun styleUrl(baseUrl: String, terrainOn: Boolean, hillshadeUrl: String = HILLSHADE_STYLE): String =
        if (terrainOn) hillshadeUrl else baseUrl
}
