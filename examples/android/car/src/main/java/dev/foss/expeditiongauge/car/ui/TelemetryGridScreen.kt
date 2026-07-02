package dev.foss.expeditiongauge.car.ui

import androidx.car.app.CarContext
import androidx.car.app.Screen
import androidx.car.app.model.Action
import androidx.car.app.model.ActionStrip
import androidx.car.app.model.CarText
import androidx.car.app.model.GridItem
import androidx.car.app.model.GridTemplate
import androidx.car.app.model.ItemList
import androidx.car.app.model.Template
import dev.foss.expeditiongauge.car.CarAppBridgeRegistry
import dev.foss.expeditiongauge.car.CarHudTile

class TelemetryGridScreen(carContext: CarContext) : Screen(carContext) {

    init {
        CarAppBridgeRegistry.bridge?.setInvalidationListener { invalidate() }
    }

    override fun onGetTemplate(): Template {
        val bridge = CarAppBridgeRegistry.bridge
        if (bridge == null) {
            return waitingTemplate("Start ExpeditionGauge on phone")
        }

        val tiles = bridge.hudTiles()
        val gridItems = ItemList.Builder().apply {
            addItem(gridItem(tiles.gMeter))
            addItem(gridItem(tiles.telemetry))
            addItem(gridItem(tiles.tpms))
        }.build()

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

        val markAction = Action.Builder()
            .setTitle("Mark")
            .setOnClickListener {
                bridge.markEvent()
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
                    .addAction(markAction)
                    .build(),
            )
            .build()
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
        return GridItem.Builder()
            .setTitle(tile.title)
            .setText(CarText.create(text))
            .build()
    }

    private fun waitingTemplate(message: String): Template {
        val item = GridItem.Builder()
            .setTitle("ExpeditionGauge")
            .setText(CarText.create(message))
            .build()
        return GridTemplate.Builder()
            .setTitle("ExpeditionGauge")
            .setSingleList(ItemList.Builder().addItem(item).build())
            .build()
    }
}
