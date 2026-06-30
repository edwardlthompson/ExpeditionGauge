package dev.foss.expeditiongauge.obd

import android.annotation.SuppressLint
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothSocket
import android.content.Context
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
                initElm327(sock)
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

    private fun initElm327(sock: BluetoothSocket) {
        val writer = OutputStreamWriter(sock.outputStream)
        val reader = BufferedReader(InputStreamReader(sock.inputStream))
        listOf("ATZ", "ATE0", "ATL0", "ATSP0").forEach { cmd ->
            writer.write("$cmd\r")
            writer.flush()
            readUntilPrompt(reader)
        }
    }

    private suspend fun pollLoop(sock: BluetoothSocket) {
        val writer = OutputStreamWriter(sock.outputStream)
        val reader = BufferedReader(InputStreamReader(sock.inputStream))
        while (scope.coroutineContext.isActive) {
            val rpm = queryPid(reader, writer, "010C")?.let { parseRpm(it) }
            val speed = queryPid(reader, writer, "010D")?.let { parseSingleByte(it) }
            val throttle = queryPid(reader, writer, "0111")?.let { parseSingleByte(it) * 100f / 255f }
            val load = queryPid(reader, writer, "0104")?.let { parseSingleByte(it) * 100f / 255f }
            val voltage = queryPid(reader, writer, "0142")?.let { parseVoltage(it) }
            _snapshot.value = ObdSnapshot(
                connected = true,
                rpm = rpm,
                speedKmh = speed,
                throttlePct = throttle,
                engineLoadPct = load,
                wheelSpeedKmh = speed,
                batteryVoltage = voltage,
            )
            delay(POLL_INTERVAL_MS)
        }
    }

    private fun queryPid(reader: BufferedReader, writer: OutputStreamWriter, pid: String): String? {
        writer.write("$pid\r")
        writer.flush()
        return readUntilPrompt(reader)?.filter { it.isLetterOrDigit() }?.uppercase()
    }

    private fun readUntilPrompt(reader: BufferedReader): String? {
        val sb = StringBuilder()
        repeat(20) {
            if (!reader.ready()) return@repeat
            val c = reader.read().toChar()
            sb.append(c)
            if (c == '>') return sb.toString()
        }
        return sb.toString().ifBlank { null }
    }

    private fun parseRpm(response: String): Float? {
        val idx = response.indexOf("410C")
        if (idx < 0 || idx + 8 > response.length) return null
        val a = response.substring(idx + 4, idx + 6).toIntOrNull(16) ?: return null
        val b = response.substring(idx + 6, idx + 8).toIntOrNull(16) ?: return null
        return (a * 256 + b) / 4f
    }

    private fun parseSingleByte(response: String): Float {
        val hex = response.takeLast(2)
        return hex.toIntOrNull(16)?.toFloat() ?: 0f
    }

    private fun parseVoltage(response: String): Float? {
        val idx = response.indexOf("4142")
        if (idx < 0 || idx + 8 > response.length) return null
        val a = response.substring(idx + 4, idx + 6).toIntOrNull(16) ?: return null
        val b = response.substring(idx + 6, idx + 8).toIntOrNull(16) ?: return null
        return (a * 256 + b) / 1000f
    }

    companion object {
        private val SPP_UUID: UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb")
        private const val POLL_INTERVAL_MS = 200L
    }
}
