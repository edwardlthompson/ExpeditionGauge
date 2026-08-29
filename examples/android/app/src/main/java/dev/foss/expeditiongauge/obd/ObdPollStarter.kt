package dev.foss.expeditiongauge.obd

import android.bluetooth.BluetoothSocket
import android.util.Log
import dev.foss.expeditiongauge.obd.dtc.DtcCatalog
import dev.foss.expeditiongauge.settings.ObdPidConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

internal object ObdPollStarter {
    private const val TAG = "ExpeditionGauge/Obd"

    fun launch(
        scope: CoroutineScope,
        sock: BluetoothSocket,
        pidConfig: ObdPidConfig,
        catalog: DtcCatalog,
        hud: ObdHudState,
        onSnapshot: (ObdSnapshot) -> Unit,
        onDead: () -> Unit,
    ): Job = scope.launch(Dispatchers.IO) {
        try {
            ObdPollLoop.run(
                sock = sock,
                pidConfig = pidConfig,
                catalog = catalog,
                isActive = { isActive },
                onSnapshot = onSnapshot,
                currentDtcs = { hud.dtcs.value },
                onDtcs = { hud.setDtcs(it) },
                consumeClear = { hud.clear.consume() },
                onIm = { hud.im.set(it) },
                onTrip = { hud.trip.set(it) },
                onVin = { hud.vin.set(it) },
                currentVin = { hud.vin.last6.value },
                consumeDiscover = { hud.discovery.consume() },
                onDiscover = { hud.discovery.set(it) },
                onFordTemps = { hud.fordTemps.set(it) },
                onBoost = { hud.boost.set(it) },
            )
        } catch (e: Exception) {
            Log.w(TAG, "OBD poll ended: ${e.message}")
        } finally {
            onDead()
        }
    }
}
