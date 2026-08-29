package dev.foss.expeditiongauge.obd

import android.bluetooth.BluetoothAdapter
import android.util.Log
import dev.foss.expeditiongauge.obd.dtc.DtcCatalog
import dev.foss.expeditiongauge.settings.ObdPidConfig
import dev.foss.expeditiongauge.wifielm.WifiElm327
import dev.foss.expeditiongauge.wifielm.WifiElmEndpoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withTimeout

internal object ObdConnectSession {
    private const val TAG = "ExpeditionGauge/Obd"
    const val OPEN_TIMEOUT_MS = 20_000L
    const val INIT_TIMEOUT_MS = 40_000L

    fun launch(
        scope: CoroutineScope,
        mutex: Mutex,
        address: String,
        adapter: BluetoothAdapter?,
        classicBudget: ClassicBluetoothBudget,
        pidConfig: ObdPidConfig,
        catalog: DtcCatalog,
        hud: ObdHudState,
        setPhase: (ObdConnectionPhase) -> Unit,
        setSnapshot: (ObdSnapshot) -> Unit,
        setLink: (ObdLink?) -> Unit,
        currentLink: () -> ObdLink?,
        setPollJob: (Job) -> Unit,
        disconnectSoft: () -> Unit,
    ): Job = scope.launch(Dispatchers.IO) {
        mutex.withLock {
            try {
                val wifi = WifiElm327.parse(address)
                if (wifi == null && !classicBudget.canConnect(ClassicBluetoothBudget.Slot.OBD)) {
                    Log.w(TAG, "OBD connect denied: classic SPP budget full")
                    setPhase(ObdConnectionPhase.Failed)
                    return@withLock
                }
                val link = withTimeout(OPEN_TIMEOUT_MS) { open(address, wifi, adapter) }
                withTimeout(INIT_TIMEOUT_MS) { Elm327Protocol.init(link.input, link.output) }
                setLink(link)
                if (wifi == null) classicBudget.onConnected(ClassicBluetoothBudget.Slot.OBD)
                setPhase(ObdConnectionPhase.Connected)
                setSnapshot(ObdSnapshot(connected = true))
                Log.i(TAG, "OBD connected — scheduling immediate DTC scan")
                setPollJob(
                    ObdPollStarter.launch(scope, link, pidConfig, catalog, hud, setSnapshot) {
                        if (currentLink() === link) {
                            disconnectSoft()
                            setPhase(ObdConnectionPhase.Failed)
                        }
                    },
                )
            } catch (e: Exception) {
                Log.w(TAG, "OBD connect/validate failed: ${e.message}")
                disconnectSoft()
                setPhase(ObdConnectionPhase.Failed)
            }
        }
    }

    private fun open(
        address: String,
        wifi: WifiElmEndpoint?,
        adapter: BluetoothAdapter?,
    ): ObdLink {
        if (wifi != null) return ObdTcp.open(wifi)
        checkNotNull(adapter) { "Bluetooth adapter required" }
        return ObdLink.bluetooth(ObdRfcomm.open(adapter, address))
    }
}
