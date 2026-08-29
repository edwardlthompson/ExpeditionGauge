package dev.foss.expeditiongauge.car.ui

import androidx.car.app.CarContext
import androidx.car.app.ScreenManager
import dev.foss.expeditiongauge.car.CarAppBridgeRegistry
import dev.foss.expeditiongauge.car.aaparkeddtc.AaParkedDtc
import dev.foss.expeditiongauge.car.aaparkedlibrary.AaParkedLibrary

/** Pushes parked-only AA list screens from Drive HUD taps. */
internal object DriveMapHudParked {
    fun openDtc(carContext: CarContext, screens: ScreenManager) {
        val bridge = CarAppBridgeRegistry.bridge ?: return
        val rows = bridge.parkedDtcRows()
        if (!AaParkedDtc.canOpen(bridge.isVehicleParked(), rows.size)) return
        screens.push(AaParkedDtcScreen(carContext))
    }

    fun openLibrary(carContext: CarContext, screens: ScreenManager) {
        val bridge = CarAppBridgeRegistry.bridge ?: return
        if (!AaParkedLibrary.canOpen(bridge.isVehicleParked())) return
        screens.push(AaParkedLibraryScreen(carContext))
    }
}
