package dev.foss.expeditiongauge.obd

import android.annotation.SuppressLint
import android.bluetooth.BluetoothManager
import android.content.Context
import android.util.Log
import dev.foss.expeditiongauge.obd.dtc.DtcCatalog
import dev.foss.expeditiongauge.settings.ObdPidConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.sync.Mutex

@SuppressLint("MissingPermission")
class ObdClassicManager(
    private val context: Context,
    private val classicBudget: ClassicBluetoothBudget,
    private val scope: CoroutineScope,
) {
    private val adapter = (context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager).adapter
    private var link: ObdLink? = null
    private var pollJob: Job? = null
    private var connectJob: Job? = null
    private val connectMutex = Mutex()
    private var selectedAddress: String? = null
    private val catalog: DtcCatalog by lazy {
        runCatching { DtcCatalog.load(context) }
            .onFailure { Log.w(TAG, "DTC catalog load failed: ${it.message}") }
            .getOrElse { DtcCatalog.of(emptyMap()) }
    }
    private val hud = ObdHudState()

    var pidConfig: ObdPidConfig = ObdPidConfig()

    private val _snapshot = MutableStateFlow(ObdSnapshot())
    val snapshot: StateFlow<ObdSnapshot> = _snapshot.asStateFlow()

    private val _phase = MutableStateFlow(ObdConnectionPhase.Idle)
    val phase: StateFlow<ObdConnectionPhase> = _phase.asStateFlow()

    val storedDtcs = hud.dtcs
    val imReadiness = hud.im.report
    val tripSinceClear = hud.trip.trip
    val vinLast6 = hud.vin.last6
    val pidDiscovery = hud.discovery.pids
    val fordTemps = hud.fordTemps.temps
    val boostPids = hud.boost.snap
    val tempsVoltage = hud.temps.snap

    fun selectDevice(address: String) {
        selectedAddress = address
    }

    fun selectedAddressOrNull(): String? = selectedAddress

    fun connect() {
        val address = selectedAddress ?: run {
            Log.w(TAG, "OBD connect denied: no device address")
            _phase.value = ObdConnectionPhase.Failed
            return
        }
        connectJob?.cancel()
        disconnect(clearPhase = false)
        _phase.value = ObdConnectionPhase.Connecting
        connectJob = ObdConnectSession.launch(
            scope = scope,
            mutex = connectMutex,
            address = address,
            adapter = adapter,
            classicBudget = classicBudget,
            pidConfig = pidConfig,
            catalog = catalog,
            hud = hud,
            setPhase = { _phase.value = it },
            setSnapshot = { _snapshot.value = it },
            setLink = { link = it },
            currentLink = { link },
            setPollJob = { pollJob = it },
            disconnectSoft = { disconnect(clearPhase = false) },
        )
    }

    fun disconnect(clearPhase: Boolean = true) {
        connectJob?.cancel()
        connectJob = null
        pollJob?.cancel()
        pollJob = null
        try {
            link?.close()
        } catch (_: Exception) {
        }
        link = null
        classicBudget.onDisconnected(ClassicBluetoothBudget.Slot.OBD)
        _snapshot.value = ObdSnapshot()
        hud.reset()
        if (clearPhase) _phase.value = ObdConnectionPhase.Idle
    }

    fun pairedDevices(): List<Pair<String, String>> = ObdDeviceDirectory.pairedDevices(adapter)

    fun suggestedObdDevices(): List<Pair<String, String>> =
        ObdDeviceDirectory.suggestedObdDevices(adapter)

    fun simulateStoredDtcs(codes: List<String>) = hud.simulate(codes, catalog)
    fun clearSimulatedDtcs() = hud.clearSim()
    fun requestClearDtcs() = hud.clear.request()
    fun requestPidDiscovery() = hud.discovery.request()

    companion object {
        private const val TAG = "ExpeditionGauge/Obd"
    }
}
