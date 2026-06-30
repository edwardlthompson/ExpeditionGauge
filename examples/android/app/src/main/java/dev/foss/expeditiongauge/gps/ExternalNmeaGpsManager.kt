package dev.foss.expeditiongauge.gps

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothSocket
import android.content.Context
import dev.foss.expeditiongauge.FeatureFlags
import dev.foss.expeditiongauge.obd.ClassicBluetoothBudget
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.UUID

@SuppressLint("MissingPermission")
class ExternalNmeaGpsManager(
    private val context: Context,
    private val classicBudget: ClassicBluetoothBudget,
    private val scope: CoroutineScope,
) {
    private val adapter = (context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager).adapter
    private var socket: BluetoothSocket? = null
    private var readJob: Job? = null
    private var selectedAddress: String? = null

    private val _fix = MutableStateFlow(NmeaFix())
    val fix: StateFlow<NmeaFix> = _fix.asStateFlow()

    val connected: Boolean get() = socket?.isConnected == true

    fun selectDevice(address: String) {
        selectedAddress = address
    }

    fun connect() {
        if (!FeatureFlags.externalGpsEnabled) return
        if (!classicBudget.canConnect(ClassicBluetoothBudget.Slot.EXTERNAL_GPS)) return
        val address = selectedAddress ?: return
        disconnect()
        scope.launch(Dispatchers.IO) {
            try {
                val device = adapter?.getRemoteDevice(address) ?: return@launch
                val sock = device.createRfcommSocketToServiceRecord(SPP_UUID)
                sock.connect()
                socket = sock
                classicBudget.onConnected(ClassicBluetoothBudget.Slot.EXTERNAL_GPS)
                readJob = scope.launch(Dispatchers.IO) { readLoop(sock) }
            } catch (_: Exception) {
                disconnect()
            }
        }
    }

    fun disconnect() {
        readJob?.cancel()
        readJob = null
        try {
            socket?.close()
        } catch (_: Exception) {
        }
        socket = null
        classicBudget.onDisconnected(ClassicBluetoothBudget.Slot.EXTERNAL_GPS)
    }

    fun pairedDevices(): List<Pair<String, String>> =
        adapter?.bondedDevices?.map { it.address to (it.name ?: it.address) } ?: emptyList()

    private fun readLoop(sock: BluetoothSocket) {
        val reader = BufferedReader(InputStreamReader(sock.inputStream))
        val buffer = StringBuilder()
        while (scope.coroutineContext.isActive) {
            val line = reader.readLine() ?: break
            val fix = NmeaParser.parseLine(line) ?: continue
            _fix.value = _fix.value.merge(fix)
            if (buffer.length > 4096) buffer.clear()
            buffer.appendLine(line)
        }
    }

    private fun NmeaFix.merge(other: NmeaFix): NmeaFix = copy(
        latitude = other.latitude ?: latitude,
        longitude = other.longitude ?: longitude,
        altitudeM = other.altitudeM ?: altitudeM,
        speedMps = other.speedMps ?: speedMps,
        courseDeg = other.courseDeg ?: courseDeg,
        hdop = other.hdop ?: hdop,
        numSatellites = other.numSatellites ?: numSatellites,
        fixQuality = if (other.fixQuality > 0) other.fixQuality else fixQuality,
        valid = other.valid || valid,
        timestampMs = System.currentTimeMillis(),
    )

    companion object {
        private val SPP_UUID: UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb")
        private const val STALE_MS = 2_000L

        fun isStale(fix: NmeaFix): Boolean =
            System.currentTimeMillis() - fix.timestampMs > STALE_MS
    }
}
