package dev.foss.expeditiongauge.obd

import android.annotation.SuppressLint
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.util.Log
import dev.foss.expeditiongauge.obd.dtc.DtcCatalog
import dev.foss.expeditiongauge.obd.dtc.DtcEntry
import dev.foss.expeditiongauge.settings.ObdPidConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.util.UUID

data class ObdSnapshot(
    val connected: Boolean = false,
    val rpm: Float? = null,
    val speedKmh: Float? = null,
    val throttlePct: Float? = null,
    val engineLoadPct: Float? = null,
    val wheelSpeedKmh: Float? = null,
    val rearLeftKmh: Float? = null,
    val rearRightKmh: Float? = null,
    val batteryVoltage: Float? = null,
)

@SuppressLint("MissingPermission")
class ObdClassicManager(
    private val context: Context,
    private val classicBudget: ClassicBluetoothBudget,
    private val scope: CoroutineScope,
) {
    private val adapter = (context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager).adapter
    private var socket: BluetoothSocket? = null
    private var pollJob: Job? = null
    private var selectedAddress: String? = null
    private val catalog: DtcCatalog by lazy {
        runCatching { DtcCatalog.load(context) }
            .onFailure { Log.w(TAG, "DTC catalog load failed: ${it.message}") }
            .getOrElse { DtcCatalog.of(emptyMap()) }
    }

    var pidConfig: ObdPidConfig = ObdPidConfig()

    private val _snapshot = MutableStateFlow(ObdSnapshot())
    val snapshot: StateFlow<ObdSnapshot> = _snapshot.asStateFlow()

    private val _phase = MutableStateFlow(ObdConnectionPhase.Idle)
    val phase: StateFlow<ObdConnectionPhase> = _phase.asStateFlow()

    private val _storedDtcs = MutableStateFlow<List<DtcEntry>>(emptyList())
    /** Mode 03 / sim DTCs; cleared on disconnect. */
    val storedDtcs: StateFlow<List<DtcEntry>> = _storedDtcs.asStateFlow()

    fun selectDevice(address: String) {
        selectedAddress = address
    }

    fun selectedAddressOrNull(): String? = selectedAddress

    fun connect() {
        if (!classicBudget.canConnect(ClassicBluetoothBudget.Slot.OBD)) {
            _phase.value = ObdConnectionPhase.Failed
            return
        }
        val address = selectedAddress ?: run {
            _phase.value = ObdConnectionPhase.Failed
            return
        }
        disconnect(clearPhase = false)
        _phase.value = ObdConnectionPhase.Connecting
        scope.launch(Dispatchers.IO) {
            try {
                withTimeout(CONNECT_TIMEOUT_MS) {
                    val device = adapter?.getRemoteDevice(address)
                        ?: error("No adapter/device")
                    adapter?.cancelDiscovery()
                    val sock = device.createRfcommSocketToServiceRecord(SPP_UUID)
                    sock.connect()
                    Elm327Protocol.init(sock)
                    socket = sock
                    classicBudget.onConnected(ClassicBluetoothBudget.Slot.OBD)
                    _phase.value = ObdConnectionPhase.Connected
                    _snapshot.value = ObdSnapshot(connected = true)
                    _storedDtcs.value = ObdDtcReader.readOnce(sock, catalog)
                    pollJob = scope.launch(Dispatchers.IO) { pollLoop(sock) }
                }
            } catch (e: Exception) {
                Log.w(TAG, "OBD connect/validate failed: ${e.message}")
                disconnect(clearPhase = false)
                _phase.value = ObdConnectionPhase.Failed
            }
        }
    }

    fun disconnect(clearPhase: Boolean = true) {
        pollJob?.cancel()
        pollJob = null
        try {
            socket?.close()
        } catch (_: Exception) {
        }
        socket = null
        classicBudget.onDisconnected(ClassicBluetoothBudget.Slot.OBD)
        _snapshot.value = ObdSnapshot()
        _storedDtcs.value = emptyList()
        if (clearPhase) _phase.value = ObdConnectionPhase.Idle
    }

    fun pairedDevices(): List<Pair<String, String>> = ObdDeviceDirectory.pairedDevices(adapter)

    fun suggestedObdDevices(): List<Pair<String, String>> =
        ObdDeviceDirectory.suggestedObdDevices(adapter)

    fun simulateStoredDtcs(codes: List<String>) = ObdDtcSim.apply(codes, catalog, _storedDtcs)
    fun clearSimulatedDtcs() = ObdDtcSim.clear(_storedDtcs)

    private suspend fun pollLoop(sock: BluetoothSocket) {
        val writer = OutputStreamWriter(sock.outputStream)
        val reader = BufferedReader(InputStreamReader(sock.inputStream))
        while (scope.coroutineContext.isActive) {
            _snapshot.value = ObdPollHelper.pollSnapshot(
                reader = reader,
                writer = writer,
                config = pidConfig,
                previous = _snapshot.value,
            )
            delay(POLL_INTERVAL_MS)
        }
    }

    companion object {
        private const val TAG = "ExpeditionGauge/Obd"
        private val SPP_UUID: UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb")
        private const val POLL_INTERVAL_MS = 200L
        private const val CONNECT_TIMEOUT_MS = 15_000L
    }
}
