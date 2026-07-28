package dev.foss.expeditiongauge.ble

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothManager
import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

data class ImuDeviceStatus(
    val deviceId: String,
    val displayName: String,
    val placement: ImuPlacement,
    val connected: Boolean,
    val signalQuality: SignalQuality,
)

@SuppressLint("MissingPermission")
class BleImuManager(
    private val context: Context,
    private val scanCoordinator: BleScanCoordinator = BleScanCoordinator(),
    private val connectionBudget: BleConnectionBudget = BleConnectionBudget(),
) {
    private val bluetoothManager = context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
    private val adapter: BluetoothAdapter? = bluetoothManager.adapter
    private val sessions = ConcurrentHashMap<String, ImuDeviceSession>()
    private val gattConnections = ConcurrentHashMap<String, BluetoothGatt>()
    private val userDisconnected = ConcurrentHashMap.newKeySet<String>()
    private val multiFusion = dev.foss.expeditiongauge.fusion.MultiImuYawFusion()
    private val preferences = BleImuPreferencesSupport(sessions, ::publishSessions, ::updateFusion)
    private val connection = BleImuConnectionSupport(
        context, adapter, scanCoordinator, connectionBudget, sessions, gattConnections, userDisconnected,
        onSessionsChanged = ::publishSessions,
        onFusionUpdate = ::updateFusion,
    )

    private val _sessionsFlow = MutableStateFlow<List<ImuDeviceSession>>(emptyList())
    val sessionsFlow: StateFlow<List<ImuDeviceSession>> = _sessionsFlow.asStateFlow()
    private val _fusionOutput = MutableStateFlow<dev.foss.expeditiongauge.fusion.MultiImuFusionOutput?>(null)
    val fusionOutput: StateFlow<dev.foss.expeditiongauge.fusion.MultiImuFusionOutput?> = _fusionOutput.asStateFlow()

    var onPlacementsChanged: ((Map<String, ImuPlacement>) -> Unit)?
        get() = preferences.onPlacementsChanged
        set(value) { preferences.onPlacementsChanged = value }
    var onPreferredDevicesChanged: ((Set<String>) -> Unit)?
        get() = preferences.onPreferredDevicesChanged
        set(value) { preferences.onPreferredDevicesChanged = value }

    init {
        scanCoordinator.addImuListener { result ->
            val device = result.device
            val id = device.address
            val existing = sessions[id]
            sessions[id] = (existing ?: ImuDeviceSession(id, device.name ?: id)).copy(
                displayName = device.name ?: existing?.displayName ?: id,
                rssi = result.rssi,
                lastSeenMs = System.currentTimeMillis(),
            )
            publishSessions()
        }
    }

    fun startScan() = connection.startScan()
    fun stopScan() = connection.stopScan()
    fun connect(deviceId: String) {
        userDisconnected.remove(deviceId)
        preferences.rememberPreferred(deviceId)
        connection.connectGatt(deviceId)
    }

    fun disconnect(deviceId: String) {
        userDisconnected.add(deviceId)
        preferences.forgetPreferred(deviceId)
        connection.disconnectGatt(deviceId)
        publishSessions()
        updateFusion(0f)
    }

    fun restorePlacements(map: Map<String, ImuPlacement>) = preferences.restorePlacements(map)
    fun placementsSnapshot(): Map<String, ImuPlacement> = preferences.placementsSnapshot()
    fun setPlacement(deviceId: String, placement: ImuPlacement) = preferences.setPlacement(deviceId, placement)
    fun connectPreferred(deviceIds: Set<String>) =
        preferences.connectPreferred(deviceIds, userDisconnected, connection::connectGatt)

    fun statusList(): List<ImuDeviceStatus> = sessions.values.map {
        ImuDeviceStatus(it.deviceId, it.displayName, it.placement, it.connected, it.signalQuality)
    }

    fun currentSessions(): List<ImuDeviceSession> = sessions.values.toList()

    fun fuseWithPhone(phoneYawDeg: Float): dev.foss.expeditiongauge.fusion.MultiImuFusionOutput {
        val output = multiFusion.fuse(sessions.values.toList(), phoneYawDeg)
        _fusionOutput.value = output
        return output
    }

    private fun publishSessions() { _sessionsFlow.value = sessions.values.toList() }
    private fun updateFusion(phoneYaw: Float) {
        _fusionOutput.value = multiFusion.fuse(sessions.values.toList(), phoneYaw)
    }

    companion object {
        val WIT_SERVICE: UUID = UUID.fromString("0000ffe0-0000-1000-8000-00805f9b34fb")
        val WIT_NOTIFY: UUID = UUID.fromString("0000ffe4-0000-1000-8000-00805f9b34fb")
        val WIT_WRITE: UUID = UUID.fromString("0000ffe9-0000-1000-8000-00805f9b34fb")
    }
}
