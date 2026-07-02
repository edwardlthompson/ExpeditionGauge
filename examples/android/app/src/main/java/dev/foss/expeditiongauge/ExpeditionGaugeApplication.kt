package dev.foss.expeditiongauge

import android.app.Application
import dev.foss.expeditiongauge.accessibility.AccessibilityPreferences
import dev.foss.expeditiongauge.car.AndroidAutoBridge
import dev.foss.expeditiongauge.car.CarAppBridgeRegistry
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

class ExpeditionGaugeApplication : Application() {
    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    lateinit var services: ExpeditionGaugeServices
        private set

    override fun onCreate() {
        super.onCreate()
        services = ExpeditionGaugeServices(applicationContext, applicationScope)
        val accessibilityPreferences = AccessibilityPreferences(applicationContext)
        services.initialize(applicationScope, accessibilityPreferences)
        CarAppBridgeRegistry.bridge = AndroidAutoBridge(
            services,
            services.settingsPreferences,
            services.calibrationStore,
            applicationScope,
        )
    }
}
