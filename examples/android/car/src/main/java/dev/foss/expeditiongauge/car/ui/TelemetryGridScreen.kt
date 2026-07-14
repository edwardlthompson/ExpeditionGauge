package dev.foss.expeditiongauge.car.ui

import android.content.res.Configuration
import androidx.car.app.CarContext
import androidx.car.app.Screen
import androidx.car.app.constraints.ConstraintManager
import androidx.car.app.model.Action
import androidx.car.app.model.ActionStrip
import androidx.car.app.model.CarIcon
import androidx.car.app.model.CarText
import androidx.car.app.model.GridItem
import androidx.car.app.model.GridTemplate
import androidx.car.app.model.ItemList
import androidx.car.app.model.Template
import dev.foss.expeditiongauge.car.AaDisplaySpec
import dev.foss.expeditiongauge.car.CarAppBridgeRegistry
import dev.foss.expeditiongauge.car.CarHudTile
import dev.foss.expeditiongauge.car.GridItemImagePolicy

class TelemetryGridScreen(carContext: CarContext) : Screen(carContext) {

    init {
        CarAppBridgeRegistry.bridge?.setInvalidationListener { invalidate() }
    }

    override fun onGetTemplate(): Template {
        val bridge = CarAppBridgeRegistry.bridge
        if (bridge == null) {
            return waitingTemplate("Start ExpeditionGauge on phone")
        }

        val spec = resolveDisplaySpec()
        val tiles = bridge.hudTiles(spec)
        val listBuilder = ItemList.Builder()
            .addItem(gridItem(tiles.gMeter))
            .addItem(gridItem(tiles.telemetry))
        if (spec.maxGridItems >= 3) {
            listBuilder.addItem(gridItem(tiles.tpms))
        }
        val gridItems = listBuilder.build()

        val recordAction = if (bridge.isRecording()) {
            Action.Builder()
                .setTitle("Stop")
                .setOnClickListener {
                    bridge.stopRecording()
                    invalidate()
                }
                .build()
        } else {
            Action.Builder()
                .setTitle("Record")
                .setOnClickListener {
                    bridge.startRecording()
                    invalidate()
                }
                .build()
        }

        val zeroAction = Action.Builder()
            .setTitle("Zero")
            .setOnClickListener {
                bridge.zeroAttitude()
                invalidate()
            }
            .build()

        return GridTemplate.Builder()
            .setTitle("ExpeditionGauge")
            .setHeaderAction(Action.APP_ICON)
            .setSingleList(gridItems)
            .setActionStrip(
                ActionStrip.Builder()
                    .addAction(recordAction)
                    .addAction(zeroAction)
                    .build(),
            )
            .build()
    }

    /** Re-read car config every template build (hosts may skip configuration callbacks). */
    fun resolveDisplaySpec(): AaDisplaySpec {
        val cfg = carContext.resources.configuration
        val density = carContext.resources.displayMetrics.density
        val limit = runCatching {
            carContext.getCarService(ConstraintManager::class.java)
                .getContentLimit(ConstraintManager.CONTENT_LIMIT_TYPE_GRID)
        }.getOrDefault(AaDisplaySpec.DEFAULT_GRID_LIMIT)
        val night = (cfg.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES
        return AaDisplaySpec.from(
            widthDp = cfg.screenWidthDp,
            heightDp = cfg.screenHeightDp,
            density = density,
            maxGridItems = limit,
            isDarkMode = night || carContext.isDarkMode,
        )
    }

    private fun gridItem(tile: CarHudTile): GridItem {
        val text = buildString {
            append(tile.line1)
            if (tile.line2.isNotBlank()) {
                append('\n')
                append(tile.line2)
            }
            if (tile.line3.isNotBlank()) {
                append('\n')
                append(tile.line3)
            }
        }
        val image = GridItemImagePolicy.resolve(tile.image)
        return GridItem.Builder()
            .setTitle(tile.title)
            .setText(CarText.create(text))
            .setImage(image, GridItem.IMAGE_TYPE_LARGE)
            .build()
    }

    private fun waitingTemplate(message: String): Template {
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
