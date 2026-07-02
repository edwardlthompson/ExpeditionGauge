package dev.foss.expeditiongauge.car

import androidx.car.app.model.CarIcon

/** Three-tile HUD payload for Android Auto GridTemplate (attitude / telemetry / TPMS). */
data class CarHudTile(
    val title: String,
    val line1: String,
    val line2: String,
    val line3: String = "",
    val image: CarIcon? = null,
)

data class CarHudTiles(
    val gMeter: CarHudTile,
    val telemetry: CarHudTile,
    val tpms: CarHudTile,
)
