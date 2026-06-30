package dev.foss.expeditiongauge.ble.tpms

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.le.ScanFilter
import android.bluetooth.le.ScanSettings
import android.content.Context
import android.os.ParcelUuid
import dev.foss.expeditiongauge.FeatureFlags
import dev.foss.expeditiongauge.ble.BleScanCoordinator
import dev.foss.expeditiongauge.ble.ImuPlacement
import dev.foss.expeditiongauge.telemetry.TpmsCornerReading
import dev.foss.expeditiongauge.telemetry.TpmsSnapshot
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

@SuppressLint("MissingPermission")
class BleTpmsManager(
    private val context: Context,
    private val scanCoordinator: BleScanCoordinator,
    private val parsers: List<TpmsParser> = listOf(BrTpmsParser(), PechamTpmsParser()),
) {
    private val adapter = (context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager).adapter
    private val sessions = ConcurrentHashMap<String, TpmsDeviceSession>()
    private val cornerAssignments = ConcurrentHashMap<String, ImuPlacement>()

    private val _snapshot = MutableStateFlow(TpmsSnapshot())
    val snapshot: StateFlow<TpmsSnapshot> = _snapshot.asStateFlow()

    private val _sessionsFlow = MutableStateFlow<List<TpmsDeviceSession>>(emptyList())
    val sessionsFlow: StateFlow<List<TpmsDeviceSession>> = _sessionsFlow.asStateFlow()

    private var scanning = false

    init {
        scanCoordinator.addTpmsListener { result ->
            if (!FeatureFlags.tpmsEnabled) return@addTpmsListener
            val device = result.device
            val mac = device.address
            val record = result.scanRecord ?: return@addTpmsListener
            val mfg = record.manufacturerSpecificData?.let { sparse ->
                if (sparse.size() == 0) null else sparse.valueAt(0)
            } ?: return@addTpmsListener
            val parser = parsers.firstOrNull { it.canParse(device.name, mfg) } ?: return@addTpmsListener
            val reading = parser.parse(device.name, mfg) ?: return@addTpmsListener
            val corner = cornerAssignments[mac] ?: sessions[mac]?.corner ?: ImuPlacement.Unassigned
            val session = TpmsDeviceSession(mac, corner, reading.copy(macAddress = mac), parser.parserId, result.rssi)
            sessions[mac] = session
            refreshSessions()
            publishSnapshot()
        }
    }

    fun startScan() {
        if (!FeatureFlags.tpmsEnabled || scanning) return
        val scanner = adapter?.bluetoothLeScanner ?: return
        val filters = listOf(
            ScanFilter.Builder()
                .setServiceUuid(ParcelUuid(TPMS_SERVICE))
                .build(),
        )
        scanner.startScan(filters, ScanSettings.Builder().build(), scanCoordinator.callback)
        scanning = true
    }

    fun stopScan() {
        if (!scanning) return
        adapter?.bluetoothLeScanner?.stopScan(scanCoordinator.callback)
        scanning = false
    }

    fun assignCorner(macAddress: String, corner: ImuPlacement) {
        cornerAssignments[macAddress] = corner
        sessions[macAddress]?.let { sessions[macAddress] = it.copy(corner = corner) }
        refreshSessions()
        publishSnapshot()
    }

    fun knownSessions(): List<TpmsDeviceSession> = sessions.values.toList()

    private fun refreshSessions() {
        _sessionsFlow.value = sessions.values.sortedBy { it.macAddress }
    }

    private fun publishSnapshot() {
        var snap = TpmsSnapshot()
        sessions.values.forEach { session ->
            val reading = session.lastReading ?: return@forEach
            if (session.corner == ImuPlacement.Unassigned) return@forEach
            val corner = TpmsCornerReading(
                pressureKpa = reading.pressureKpa,
                tempC = reading.tempC,
                batteryPct = (reading.batteryVolts / 3.0f * 100f).toInt().coerceIn(0, 100),
                lastSeenMs = reading.timestampMs,
            )
            snap = snap.withCorner(session.corner, corner)
            TpmsTelemetryLog.publish(
                corner = session.corner.label,
                pressureKpa = reading.pressureKpa,
                tempC = reading.tempC,
                parserId = session.parserId,
            )
        }
        _snapshot.value = snap
    }

    companion object {
        val TPMS_SERVICE: UUID = UUID.fromString("000027a5-0000-1000-8000-00805f9b34fb")
    }
}
