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
import dev.foss.expeditiongauge.car.aaparkeddtc.AaParkedDtc

/** Parked-only list of stored DTCs (code + catalog title). */
class AaParkedDtcScreen(carContext: CarContext) : Screen(carContext) {
    override fun onGetTemplate(): Template {
        val bridge = CarAppBridgeRegistry.bridge
        val rows = bridge?.parkedDtcRows().orEmpty()
        val parked = bridge?.isVehicleParked() == true
        if (!AaParkedDtc.canOpen(parked, rows.size)) {
            return MessageTemplate.Builder(AaParkedDtc.NEED_PARK)
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
            .setTitle(AaParkedDtc.TITLE)
            .setHeaderAction(Action.BACK)
            .setSingleList(list.build())
            .build()
    }
}
