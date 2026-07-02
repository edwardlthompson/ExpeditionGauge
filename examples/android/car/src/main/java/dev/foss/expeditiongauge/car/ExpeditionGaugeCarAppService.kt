package dev.foss.expeditiongauge.car

import android.content.Intent
import androidx.car.app.CarAppService
import androidx.car.app.Session
import androidx.car.app.validation.HostValidator
import dev.foss.expeditiongauge.car.ui.TelemetryGridScreen

class ExpeditionGaugeCarAppService : CarAppService() {
    override fun createHostValidator(): HostValidator = HostValidator.ALLOW_ALL_HOSTS_VALIDATOR

    override fun onCreateSession(): Session = ExpeditionGaugeCarSession()
}

private class ExpeditionGaugeCarSession : Session() {
    override fun onCreateScreen(intent: Intent) = TelemetryGridScreen(carContext)
}
