package dev.foss.expeditiongauge.car

/** Three-tile HUD payload for Android Auto GridTemplate (G / telemetry / TPMS). */
data class CarHudTile(
    val title: String,
    val line1: String,
    val line2: String,
    val line3: String = "",
)

data class CarHudTiles(
    val gMeter: CarHudTile,
    val telemetry: CarHudTile,
    val tpms: CarHudTile,
)
