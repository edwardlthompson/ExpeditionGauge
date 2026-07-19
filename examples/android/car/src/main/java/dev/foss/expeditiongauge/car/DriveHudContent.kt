package dev.foss.expeditiongauge.car

import androidx.car.app.model.CarIcon

/** PaneTemplate Drive HUD payload — one large image + up to 4 text rows. */
data class DriveHudRow(
    val title: String,
    val text: String,
)

data class DriveHudContent(
    val image: CarIcon,
    val rows: List<DriveHudRow>,
)
