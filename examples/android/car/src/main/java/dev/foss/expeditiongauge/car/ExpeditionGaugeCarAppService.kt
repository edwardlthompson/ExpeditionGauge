package dev.foss.expeditiongauge.car

import android.content.Intent
import android.content.res.Configuration
import androidx.car.app.CarAppService
import androidx.car.app.ScreenManager
import androidx.car.app.Session
import androidx.car.app.validation.HostValidator
import dev.foss.expeditiongauge.car.ui.TelemetryGridScreen

class ExpeditionGaugeCarAppService : CarAppService() {
    override fun createHostValidator(): HostValidator = HostValidator.ALLOW_ALL_HOSTS_VALIDATOR

    override fun onCreateSession(): Session = ExpeditionGaugeCarSession()
}

private class ExpeditionGaugeCarSession : Session() {
    override fun onCreateScreen(intent: Intent) = TelemetryGridScreen(carContext)

    override fun onCarConfigurationChanged(newConfiguration: Configuration) {
        // HU portrait/landscape/density — independent of phone Display.rotation.
        runCatching {
            carContext.getCarService(ScreenManager::class.java).top.invalidate()
        }
    }
}
