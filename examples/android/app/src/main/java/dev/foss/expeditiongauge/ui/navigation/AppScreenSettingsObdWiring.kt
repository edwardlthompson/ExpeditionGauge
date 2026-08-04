package dev.foss.expeditiongauge.ui.navigation

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.foss.expeditiongauge.ExpeditionGaugeServices
import dev.foss.expeditiongauge.R
import dev.foss.expeditiongauge.obd.ClassicBluetoothPairing
import dev.foss.expeditiongauge.obd.ObdConnectionPhase
import dev.foss.expeditiongauge.settings.ObdPidConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

internal data class AppScreenSettingsObdWiring(
    val connectionStatus: String,
    val onRetry: () -> Unit,
    val onForget: () -> Unit,
    val onPairNew: () -> Unit,
    val onDeviceSelect: (String) -> Unit,
    val onPidConfigChange: (ObdPidConfig) -> Unit,
)

@Composable
internal fun rememberAppScreenSettingsObdWiring(
    context: Context,
    scope: CoroutineScope,
    services: ExpeditionGaugeServices,
    obdAddress: String?,
): AppScreenSettingsObdWiring {
    val obdPhase by services.obdManager.phase.collectAsStateWithLifecycle()
    val connectionStatus = when (obdPhase) {
        ObdConnectionPhase.Idle -> stringResource(R.string.settings_obd_status_idle)
        ObdConnectionPhase.Connecting -> stringResource(R.string.settings_obd_status_connecting)
        ObdConnectionPhase.Connected -> stringResource(R.string.settings_obd_status_connected)
        ObdConnectionPhase.Failed -> stringResource(R.string.settings_obd_status_failed)
    }
    return AppScreenSettingsObdWiring(
        connectionStatus = connectionStatus,
        onRetry = {
            obdAddress?.let { address ->
                services.obdManager.selectDevice(address)
                services.obdManager.connect()
            }
        },
        onForget = {
            scope.launch {
                services.settingsPreferences.forgetObdDevice()
                services.obdManager.disconnect()
            }
        },
        onPairNew = {
            runCatching {
                context.startActivity(ClassicBluetoothPairing(context).openSystemBluetoothSettings())
            }
        },
        onDeviceSelect = { address ->
            // Preference Flow (ExpeditionGaugeSensorPrefBindings) owns connect — avoid
            // a second connect() that tears down the first RFCOMM mid-handshake.
            scope.launch {
                services.settingsPreferences.setObdDeviceAddress(address)
            }
        },
        onPidConfigChange = { config ->
            scope.launch {
                services.settingsPreferences.setObdPidConfig(config)
                services.obdManager.pidConfig = config
            }
        },
    )
}
