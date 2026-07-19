package dev.foss.expeditiongauge.car

import android.content.Intent
import android.content.res.Configuration
import androidx.car.app.CarAppService
import androidx.car.app.ScreenManager
import androidx.car.app.Session
import androidx.car.app.validation.HostValidator
import dev.foss.expeditiongauge.car.ui.DriveMapHudScreen
import dev.foss.expeditiongauge.car.ui.DrivePaneScreen

class ExpeditionGaugeCarAppService : CarAppService() {
    override fun createHostValidator(): HostValidator = HostValidator.ALLOW_ALL_HOSTS_VALIDATOR

    override fun onCreateSession(): Session = ExpeditionGaugeCarSession()
}

private class ExpeditionGaugeCarSession : Session() {
    override fun onCreateScreen(intent: Intent) = DriveMapHudScreen(carContext)

    override fun onCarConfigurationChanged(newConfiguration: Configuration) {
        // HU portrait/landscape/density — independent of phone Display.rotation.
        runCatching {
            val top = carContext.getCarService(ScreenManager::class.java).top
            when (top) {
                is DriveMapHudScreen -> top.refreshDisplaySpec()
                is DrivePaneScreen -> top.refreshDisplaySpec()
            }
            top.invalidate()
        }
    }
}
