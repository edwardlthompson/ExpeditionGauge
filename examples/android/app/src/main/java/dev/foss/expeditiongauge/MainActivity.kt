package dev.foss.expeditiongauge

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import dev.foss.expeditiongauge.about.AppUpdatePreferences
import dev.foss.expeditiongauge.accessibility.AccessibilityPreferences
import dev.foss.expeditiongauge.accessibility.AudibleTones
import dev.foss.expeditiongauge.onboarding.OnboardingPreferences
import dev.foss.expeditiongauge.permissions.PermissionsHelper
import dev.foss.expeditiongauge.stats.SessionStatsAggregator
import dev.foss.expeditiongauge.ui.ExpeditionGaugeApp
import dev.foss.expeditiongauge.ui.dashboard.DashboardViewModelFactory
import dev.foss.expeditiongauge.ui.theme.BrightnessMode
import dev.foss.expeditiongauge.ui.theme.ThemePreferences
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private lateinit var services: ExpeditionGaugeServices
    private var networkStatusMonitor: dev.foss.expeditiongauge.network.NetworkStatusMonitor? = null

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions(),
    ) { grants ->
        if (grants.values.any { it }) {
            services.startSensors()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        services = ExpeditionGaugeServices(applicationContext, lifecycleScope)
        services.initialize(lifecycleScope)

        val themePreferences = ThemePreferences(applicationContext)
        val accessibilityPreferences = AccessibilityPreferences(applicationContext)
        val onboardingPreferences = OnboardingPreferences(applicationContext)
        val appUpdatePreferences = AppUpdatePreferences(applicationContext)
        val sessionStatsAggregator = SessionStatsAggregator()
        val audibleTones = AudibleTones(applicationContext)
        networkStatusMonitor = dev.foss.expeditiongauge.network.NetworkStatusMonitor(applicationContext)
            .also { it.start() }

        lifecycleScope.launch {
            appUpdatePreferences.clearPendingRestart()
            appUpdatePreferences.ensureInstalledFormat()
        }

        val dashboardViewModelFactory = DashboardViewModelFactory(
            telemetryBus = services.telemetryBus,
            calibrationStore = services.calibrationStore,
            thermalMonitor = services.thermalMonitor,
            recordingWriter = services.recordingWriter,
            settingsProfileRepository = services.settingsProfileRepository,
            sessionEventRecorder = services.sessionEventRecorder,
            liveTelemetryModule = services.liveTelemetryModule,
        )

        setContent {
            ExpeditionGaugeApp(
                context = this,
                scope = lifecycleScope,
                services = services,
                themePreferences = themePreferences,
                accessibilityPreferences = accessibilityPreferences,
                onboardingPreferences = onboardingPreferences,
                appUpdatePreferences = appUpdatePreferences,
                networkStatusMonitor = networkStatusMonitor!!,
                dashboardViewModelFactory = dashboardViewModelFactory,
                sessionStatsAggregator = sessionStatsAggregator,
                audibleTones = audibleTones,
                brightnessMode = BrightnessMode.Auto,
            )
        }
    }

    override fun onStart() {
        super.onStart()
        if (PermissionsHelper.hasAll(this)) {
            services.startSensors()
        } else {
            PermissionsHelper.request(this, permissionLauncher)
        }
        services.thermalMonitor.refresh()
    }

    override fun onStop() {
        services.stopSensors()
        super.onStop()
    }

    override fun onDestroy() {
        networkStatusMonitor?.stop()
        services.obdManager.disconnect()
        services.externalGpsManager.disconnect()
        super.onDestroy()
    }
}
