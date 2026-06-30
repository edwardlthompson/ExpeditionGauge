package dev.foss.expeditiongauge.obd

import android.annotation.SuppressLint
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothSocket
import android.content.Context
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

    var pidConfig: ObdPidConfig = ObdPidConfig()

    private val _snapshot = MutableStateFlow(ObdSnapshot())
    val snapshot: StateFlow<ObdSnapshot> = _snapshot.asStateFlow()

    fun selectDevice(address: String) {
        selectedAddress = address
    }

    fun connect() {
        if (!classicBudget.canConnect(ClassicBluetoothBudget.Slot.OBD)) return
        val address = selectedAddress ?: return
        disconnect()
        scope.launch(Dispatchers.IO) {
            try {
                val device = adapter?.getRemoteDevice(address) ?: return@launch
                val sock = device.createRfcommSocketToServiceRecord(SPP_UUID)
                sock.connect()
                socket = sock
                classicBudget.onConnected(ClassicBluetoothBudget.Slot.OBD)
                Elm327Protocol.init(sock)
                pollJob = scope.launch(Dispatchers.IO) { pollLoop(sock) }
            } catch (_: Exception) {
                disconnect()
            }
        }
    }

    fun disconnect() {
        pollJob?.cancel()
        pollJob = null
        try {
            socket?.close()
        } catch (_: Exception) {
        }
        socket = null
        classicBudget.onDisconnected(ClassicBluetoothBudget.Slot.OBD)
        _snapshot.value = ObdSnapshot()
    }

    fun pairedDevices(): List<Pair<String, String>> =
        adapter?.bondedDevices?.map { it.address to (it.name ?: it.address) } ?: emptyList()

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
        private val SPP_UUID: UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb")
        private const val POLL_INTERVAL_MS = 200L
    }
}
