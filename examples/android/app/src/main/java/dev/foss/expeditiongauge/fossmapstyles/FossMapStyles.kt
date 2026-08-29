package dev.foss.expeditiongauge.fossmapstyles

import dev.foss.expeditiongauge.terraintoggle.TerrainToggle

data class FossMapStyle(val id: String, val label: String, val url: String)

/** Extra FOSS basemap styles (OpenFreeMap + MapLibre demo). */
object FossMapStyles {
    val ALL = listOf(
        FossMapStyle("demo", "MapLibre demo", "https://demotiles.maplibre.org/style.json"),
        FossMapStyle("liberty", "OpenFreeMap Liberty", "https://tiles.openfreemap.org/styles/liberty"),
        FossMapStyle("bright", "OpenFreeMap Bright", "https://tiles.openfreemap.org/styles/bright"),
    )

    @Volatile
    var selectedId: String = ALL.first().id

    fun byId(id: String): FossMapStyle = ALL.firstOrNull { it.id == id } ?: ALL.first()

    fun cycle(id: String): FossMapStyle {
        val index = ALL.indexOfFirst { it.id == id }.let { if (it < 0) 0 else it }
        return ALL[(index + 1) % ALL.size]
    }

    fun url(id: String = selectedId, terrainOn: Boolean = false): String =
        TerrainToggle.styleUrl(byId(id).url, terrainOn)
}
