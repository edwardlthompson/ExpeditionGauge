package dev.foss.expeditiongauge.obd

import android.annotation.SuppressLint
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.util.Log
import dev.foss.expeditiongauge.obd.dtc.DtcCatalog
import dev.foss.expeditiongauge.settings.ObdPidConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withTimeout

@SuppressLint("MissingPermission")
class ObdClassicManager(
    private val context: Context,
    private val classicBudget: ClassicBluetoothBudget,
    private val scope: CoroutineScope,
) {
    private val adapter = (context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager).adapter
    private var socket: BluetoothSocket? = null
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

    fun selectDevice(address: String) {
        selectedAddress = address
    }

    fun selectedAddressOrNull(): String? = selectedAddress

    fun connect() {
        if (!classicBudget.canConnect(ClassicBluetoothBudget.Slot.OBD)) {
            Log.w(TAG, "OBD connect denied: classic SPP budget full")
            _phase.value = ObdConnectionPhase.Failed
            return
        }
        val address = selectedAddress ?: run {
            Log.w(TAG, "OBD connect denied: no device address")
            _phase.value = ObdConnectionPhase.Failed
            return
        }
        connectJob?.cancel()
        disconnect(clearPhase = false)
        _phase.value = ObdConnectionPhase.Connecting
        connectJob = scope.launch(Dispatchers.IO) {
            connectMutex.withLock {
                try {
                    val sock = withTimeout(RFCOMM_TIMEOUT_MS) { ObdRfcomm.open(adapter, address) }
                    withTimeout(INIT_TIMEOUT_MS) { Elm327Protocol.init(sock) }
                    socket = sock
                    classicBudget.onConnected(ClassicBluetoothBudget.Slot.OBD)
                    _phase.value = ObdConnectionPhase.Connected
                    _snapshot.value = ObdSnapshot(connected = true)
                    Log.i(TAG, "OBD connected — scheduling immediate DTC scan")
                    pollJob = ObdPollStarter.launch(
                        scope, sock, pidConfig, catalog, hud,
                        onSnapshot = { _snapshot.value = it },
                    ) {
                        if (socket === sock) {
                            disconnect(clearPhase = false)
                            _phase.value = ObdConnectionPhase.Failed
                        }
                    }
                } catch (e: Exception) {
                    Log.w(TAG, "OBD connect/validate failed: ${e.message}")
                    disconnect(clearPhase = false)
                    _phase.value = ObdConnectionPhase.Failed
                }
            }
        }
    }

    fun disconnect(clearPhase: Boolean = true) {
        connectJob?.cancel()
        connectJob = null
        pollJob?.cancel()
        pollJob = null
        try {
            socket?.close()
        } catch (_: Exception) {
        }
        socket = null
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
        private const val RFCOMM_TIMEOUT_MS = 20_000L
        private const val INIT_TIMEOUT_MS = 40_000L
    }
}
