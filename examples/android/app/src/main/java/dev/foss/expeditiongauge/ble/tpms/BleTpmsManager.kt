package dev.foss.expeditiongauge.ble.tpms

import android.annotation.SuppressLint
import android.bluetooth.BluetoothManager
import android.bluetooth.le.ScanFilter
import android.bluetooth.le.ScanSettings
import android.content.Context
import android.os.ParcelUuid
import dev.foss.expeditiongauge.FeatureFlags
import dev.foss.expeditiongauge.ble.BleScanCoordinator
import dev.foss.expeditiongauge.ble.ImuPlacement
import kotlinx.coroutines.flow.StateFlow
import java.util.UUID

@SuppressLint("MissingPermission")
class BleTpmsManager(
    private val context: Context,
    private val scanCoordinator: BleScanCoordinator,
    private val parsers: List<TpmsParser> = listOf(BrTpmsParser(), PechamTpmsParser()),
) {
    private val adapter = (context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager).adapter
    private val registry = BleTpmsSessionRegistry()

    val snapshot: StateFlow<dev.foss.expeditiongauge.telemetry.TpmsSnapshot> = registry.snapshot
    val sessionsFlow: StateFlow<List<TpmsDeviceSession>> = registry.sessionsFlow

    private var scanning = false

    init {
        scanCoordinator.addTpmsListener { result ->
            if (!FeatureFlags.tpmsEnabled) return@addTpmsListener
            val device = result.device
            val mac = device.address.uppercase()
            val record = result.scanRecord ?: return@addTpmsListener
            val mfg = record.manufacturerSpecificData?.let { sparse ->
                if (sparse.size() == 0) null else sparse.valueAt(0)
            } ?: return@addTpmsListener
            val parser = parsers.firstOrNull { it.canParse(device.name, mfg) } ?: return@addTpmsListener
            val reading = parser.parse(device.name, mfg) ?: return@addTpmsListener
            val corner = registry.cornerFor(mac)
            registry.putLiveSession(
                TpmsDeviceSession(mac, corner, reading.copy(macAddress = mac), parser.parserId, result.rssi),
            )
        }
    }

    fun isBluetoothEnabled(): Boolean = adapter?.isEnabled == true

    fun startScan() {
        if (!FeatureFlags.tpmsEnabled || scanning) return
        val scanner = adapter?.bluetoothLeScanner ?: return
        val filters = listOf(
            ScanFilter.Builder().setServiceUuid(ParcelUuid(TPMS_SERVICE)).build(),
        )
        scanner.startScan(filters, ScanSettings.Builder().build(), scanCoordinator.callback)
        scanning = true
    }

    fun stopScan() {
        if (!scanning) return
        adapter?.bluetoothLeScanner?.stopScan(scanCoordinator.callback)
        scanning = false
    }

    fun restoreAssignments(map: Map<String, ImuPlacement>) = registry.restoreAssignments(map)

    fun assignmentsSnapshot(): Map<String, ImuPlacement> = registry.assignmentsSnapshot()

    var onAssignmentsChanged: ((Map<String, ImuPlacement>) -> Unit)?
        get() = registry.onAssignmentsChanged
        set(value) { registry.onAssignmentsChanged = value }

    fun assignCorner(macAddress: String, corner: ImuPlacement) =
        assignCornerExclusive(macAddress, corner)

    fun assignCornerExclusive(macAddress: String, corner: ImuPlacement) =
        registry.assignCornerExclusive(macAddress, corner)

    fun ensureAssignedSession(macAddress: String, corner: ImuPlacement) =
        registry.ensureAssignedSession(macAddress, corner)

    fun knownSessions(): List<TpmsDeviceSession> = registry.knownSessions()

    companion object {
        val TPMS_SERVICE: UUID = UUID.fromString("000027a5-0000-1000-8000-00805f9b34fb")
        const val PENDING_PARSER_ID = "pending"
    }
}
