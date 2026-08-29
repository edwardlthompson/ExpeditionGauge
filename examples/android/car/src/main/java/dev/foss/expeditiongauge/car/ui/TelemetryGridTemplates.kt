package dev.foss.expeditiongauge.car.ui

import androidx.car.app.CarContext
import androidx.car.app.constraints.ConstraintManager
import androidx.car.app.model.CarIcon
import androidx.car.app.model.CarText
import androidx.car.app.model.GridItem
import androidx.car.app.model.GridTemplate
import androidx.car.app.model.ItemList
import androidx.car.app.model.Template
import dev.foss.expeditiongauge.car.AaDisplaySpec
import dev.foss.expeditiongauge.car.CarHudTile
import dev.foss.expeditiongauge.car.GridItemImagePolicy
import dev.foss.expeditiongauge.car.aanight.AaNightMode

internal object TelemetryGridTemplates {
    fun readDisplaySpec(carContext: CarContext): AaDisplaySpec {
        val cfg = carContext.resources.configuration
        val density = carContext.resources.displayMetrics.density
        val limit = runCatching {
            carContext.getCarService(ConstraintManager::class.java)
                .getContentLimit(ConstraintManager.CONTENT_LIMIT_TYPE_GRID)
        }.getOrDefault(AaDisplaySpec.DEFAULT_GRID_LIMIT)
        return AaDisplaySpec.from(
            widthDp = cfg.screenWidthDp,
            heightDp = cfg.screenHeightDp,
            density = density,
            maxGridItems = limit,
            isDarkMode = AaNightMode.fromCarUi(cfg.uiMode, carContext.isDarkMode),
        )
    }

    fun gridItem(tile: CarHudTile): GridItem {
        val image = GridItemImagePolicy.resolve(tile.image)
        return GridItem.Builder()
            .setTitle(tile.title)
            .setText(CarText.create(tile.secondaryText()))
            .setImage(image, GridItem.IMAGE_TYPE_LARGE)
            .build()
    }

    fun waitingTemplate(message: String): Template {
        val item = GridItem.Builder()
            .setTitle("ExpeditionGauge")
            .setText(CarText.create(message))
            .setImage(CarIcon.APP_ICON, GridItem.IMAGE_TYPE_LARGE)
            .build()
        return GridTemplate.Builder()
            .setTitle("ExpeditionGauge")
            .setSingleList(ItemList.Builder().addItem(item).build())
            .build()
    }
}
