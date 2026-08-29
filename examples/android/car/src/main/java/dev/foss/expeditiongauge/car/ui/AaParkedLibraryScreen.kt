package dev.foss.expeditiongauge.car.ui

import androidx.car.app.CarContext
import androidx.car.app.Screen
import androidx.car.app.model.Action
import androidx.car.app.model.ItemList
import androidx.car.app.model.ListTemplate
import androidx.car.app.model.MessageTemplate
import androidx.car.app.model.Row
import androidx.car.app.model.Template
import dev.foss.expeditiongauge.car.CarAppBridgeRegistry
import dev.foss.expeditiongauge.car.aaa11y.AaA11yType
import dev.foss.expeditiongauge.car.aaparkedlibrary.AaParkedLibrary

/** Parked-only list of recorded sessions (name + duration). */
class AaParkedLibraryScreen(carContext: CarContext) : Screen(carContext) {
    override fun onGetTemplate(): Template {
        val bridge = CarAppBridgeRegistry.bridge
        val parked = bridge?.isVehicleParked() == true
        if (!AaParkedLibrary.canOpen(parked)) {
            return MessageTemplate.Builder(AaParkedLibrary.NEED_PARK)
                .setHeaderAction(Action.BACK)
                .build()
        }
        val rows = bridge?.parkedLibraryRows().orEmpty()
        if (rows.isEmpty()) {
            return MessageTemplate.Builder(AaParkedLibrary.EMPTY)
                .setHeaderAction(Action.BACK)
                .build()
        }
        val list = ItemList.Builder()
        rows.forEach { row ->
            list.addItem(
                Row.Builder()
                    .setTitle(AaA11yType.spoken(row.title, row.text))
                    .addText(row.text)
                    .build(),
            )
        }
        return ListTemplate.Builder()
            .setTitle(AaParkedLibrary.TITLE)
            .setHeaderAction(Action.BACK)
            .setSingleList(list.build())
            .build()
    }
}
