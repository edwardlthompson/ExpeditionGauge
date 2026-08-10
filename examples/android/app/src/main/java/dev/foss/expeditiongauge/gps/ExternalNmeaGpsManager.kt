package dev.foss.expeditiongauge.gps

import android.annotation.SuppressLint
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.util.Log
import dev.foss.expeditiongauge.FeatureFlags
import dev.foss.expeditiongauge.obd.ClassicBluetoothBudget
import dev.foss.expeditiongauge.obd.ObdRfcomm
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
import java.io.IOException
import java.io.InputStreamReader

@SuppressLint("MissingPermission")
class ExternalNmeaGpsManager(
    private val context: Context,
    private val classicBudget: ClassicBluetoothBudget,
    private val scope: CoroutineScope,
) {
    private val adapter = (context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager).adapter
    private var socket: BluetoothSocket? = null
    private var readJob: Job? = null
    private var connectJob: Job? = null
    private var selectedAddress: String? = null

    private val _fix = MutableStateFlow(NmeaFix())
    val fix: StateFlow<NmeaFix> = _fix.asStateFlow()

    private val _connected = MutableStateFlow(false)
    val connectedFlow: StateFlow<Boolean> = _connected.asStateFlow()

    val connected: Boolean get() = _connected.value

    fun selectDevice(address: String) {
        selectedAddress = address
    }

    fun connect() {
        if (!FeatureFlags.externalGpsEnabled) return
        if (!classicBudget.canConnect(ClassicBluetoothBudget.Slot.EXTERNAL_GPS)) return
        val address = selectedAddress ?: return
        connectJob?.cancel()
        disconnect(clearFix = false)
        connectJob = scope.launch(Dispatchers.IO) {
            try {
                val sock = ObdRfcomm.open(adapter, address)
                if (!isActive) {
                    runCatching { sock.close() }
                    return@launch
                }
                socket = sock
                _connected.value = true
                classicBudget.onConnected(ClassicBluetoothBudget.Slot.EXTERNAL_GPS)
                readJob = scope.launch(Dispatchers.IO) { readLoop(sock) }
            } catch (e: Exception) {
                Log.w(TAG, "External GPS connect failed: ${e.message}")
                disconnect()
            }
        }
    }

    fun disconnect(clearFix: Boolean = true) {
        connectJob?.cancel()
        connectJob = null
        readJob?.cancel()
        readJob = null
        try {
            socket?.close()
        } catch (_: Exception) {
        }
        socket = null
        _connected.value = false
        if (clearFix) {
            _fix.value = NmeaFix(valid = false, timestampMs = 0L)
        }
        classicBudget.onDisconnected(ClassicBluetoothBudget.Slot.EXTERNAL_GPS)
    }

    fun pairedDevices(): List<Pair<String, String>> =
        adapter?.bondedDevices?.map { it.address to (it.name ?: it.address) } ?: emptyList()

    private suspend fun readLoop(sock: BluetoothSocket) {
        try {
            val reader = BufferedReader(InputStreamReader(sock.inputStream))
            while (scope.coroutineContext.isActive && sock.isConnected) {
                val line = try {
                    reader.readLine()
                } catch (_: IOException) {
                    break
                } ?: break
                val parsed = NmeaParser.parseLine(line) ?: continue
                _fix.value = _fix.value.merge(parsed.copy(timestampMs = System.currentTimeMillis()))
                if (parsed.valid) {
                    GpsTelemetryLog.publish(_fix.value, source = "external")
                }
            }
        } catch (e: Exception) {
            Log.w(TAG, "External GPS read ended: ${e.message}")
        } finally {
            if (socket === sock) {
                disconnect()
                maybeReconnect()
            }
        }
    }

    private fun maybeReconnect() {
        if (!FeatureFlags.externalGpsEnabled) return
        val address = selectedAddress ?: return
        scope.launch(Dispatchers.IO) {
            delay(RECONNECT_DELAY_MS)
            if (!FeatureFlags.externalGpsEnabled || connected) return@launch
            if (selectedAddress != address) return@launch
            Log.i(TAG, "External GPS reconnecting")
            connect()
        }
    }

    companion object {
        private const val TAG = "ExpeditionGauge/ExtGps"
        const val STALE_MS = 2_000L
        private const val RECONNECT_DELAY_MS = 2_500L

        fun isStale(fix: NmeaFix): Boolean =
            System.currentTimeMillis() - fix.timestampMs > STALE_MS
    }
}
