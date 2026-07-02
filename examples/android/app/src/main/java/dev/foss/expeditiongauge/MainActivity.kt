package dev.foss.expeditiongauge

import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.lifecycleScope
import dev.foss.expeditiongauge.about.AppUpdatePreferences
import dev.foss.expeditiongauge.accessibility.AccessibilityPreferences
import dev.foss.expeditiongauge.accessibility.AudibleTones
import dev.foss.expeditiongauge.onboarding.OnboardingPreferences
import dev.foss.expeditiongauge.permissions.PermissionsHelper
import dev.foss.expeditiongauge.settings.DrivingModePreferences
import dev.foss.expeditiongauge.stats.SessionStatsAggregator
import dev.foss.expeditiongauge.ui.navigation.ExpeditionGaugeApp
import dev.foss.expeditiongauge.ui.dashboard.DashboardViewModelFactory
import dev.foss.expeditiongauge.ui.theme.BrightnessPreferences
import dev.foss.expeditiongauge.ui.theme.ThemePreferences
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private lateinit var services: ExpeditionGaugeServices
    private var networkStatusMonitor: dev.foss.expeditiongauge.network.NetworkStatusMonitor? = null
    private var permissionsGranted by mutableStateOf(false)

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions(),
    ) { grants ->
        permissionsGranted = PermissionsHelper.hasAll(this)
        if (grants.values.any { it }) {
            services.startSensors()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        permissionsGranted = PermissionsHelper.hasAll(this)
        val app = application as ExpeditionGaugeApplication
        services = app.services
        val accessibilityPreferences = AccessibilityPreferences(applicationContext)

        val themePreferences = ThemePreferences(applicationContext)
        val drivingModePreferences = DrivingModePreferences(applicationContext)
        val onboardingPreferences = OnboardingPreferences(applicationContext)
        val appUpdatePreferences = AppUpdatePreferences(applicationContext)
        val brightnessPreferences = BrightnessPreferences(applicationContext)
        val sessionStatsAggregator = SessionStatsAggregator()
        val audibleTones = AudibleTones(applicationContext)
        networkStatusMonitor = dev.foss.expeditiongauge.network.NetworkStatusMonitor(applicationContext)
            .also { it.start() }

        lifecycleScope.launch {
            appUpdatePreferences.clearPendingRestart()
            appUpdatePreferences.ensureInstalledFormat()
        }

        lifecycleScope.launch {
            combine(
                drivingModePreferences.drivingModeEnabled,
                drivingModePreferences.lockLandscapeWhileRecording,
                services.recordingWriter.recording,
            ) { drivingMode, lockLandscape, recording ->
                Triple(drivingMode, lockLandscape, recording)
            }.collect { (drivingMode, lockLandscape, recording) ->
                requestedOrientation = when {
                    drivingMode && lockLandscape && recording ->
                        ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
                    else -> ActivityInfo.SCREEN_ORIENTATION_FULL_USER
                }
            }
        }

        val dashboardViewModelFactory = DashboardViewModelFactory(
            appContext = applicationContext,
            database = services.database,
            telemetryBus = services.telemetryBus,
            calibrationStore = services.calibrationStore,
            thermalMonitor = services.thermalMonitor,
            recordingWriter = services.recordingWriter,
            settingsProfileRepository = services.settingsProfileRepository,
            sessionEventRecorder = services.sessionEventRecorder,
            liveTelemetryModule = services.liveTelemetryModule,
            lapTimingService = services.lapTimingService,
            settingsPreferences = services.settingsPreferences,
            alertService = services.alertService,
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
                brightnessPreferences = brightnessPreferences,
                permissionsGranted = permissionsGranted,
                onRequestPermissions = {
                    PermissionsHelper.requestRequired(this, permissionLauncher)
                },
            )
        }
    }

    override fun onStart() {
        super.onStart()
        permissionsGranted = PermissionsHelper.hasAll(this)
        if (permissionsGranted) {
            services.startSensors()
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
