package dev.foss.expeditiongauge.car

import androidx.car.app.model.CarIcon

/** Three-tile HUD payload for Android Auto GridTemplate (attitude / telemetry / TPMS). */
data class CarHudTile(
    val title: String,
    val line1: String,
    val line2: String,
    val line3: String = "",
    val image: CarIcon? = null,
) {
    /** GridItem secondary text is a single truncated line — join without newlines. */
    fun secondaryText(): String =
        listOf(line1, line2, line3)
            .filter { it.isNotBlank() }
            .joinToString(" · ")
}

data class CarHudTiles(
    val gMeter: CarHudTile,
    val telemetry: CarHudTile,
    val tpms: CarHudTile,
)
