package dev.foss.expeditiongauge.ble

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattDescriptor
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothProfile
import android.bluetooth.le.ScanFilter
import android.bluetooth.le.ScanSettings
import android.content.Context
import android.os.ParcelUuid
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
    private val multiFusion = dev.foss.expeditiongauge.fusion.MultiImuYawFusion()

    private val _sessionsFlow = MutableStateFlow<List<ImuDeviceSession>>(emptyList())
    val sessionsFlow: StateFlow<List<ImuDeviceSession>> = _sessionsFlow.asStateFlow()

    private val _fusionOutput = MutableStateFlow<dev.foss.expeditiongauge.fusion.MultiImuFusionOutput?>(null)
    val fusionOutput: StateFlow<dev.foss.expeditiongauge.fusion.MultiImuFusionOutput?> = _fusionOutput.asStateFlow()

    private var scanning = false

    init {
        scanCoordinator.addImuListener { result ->
            val device = result.device
            val id = device.address
            val existing = sessions[id]
            val session = (existing ?: ImuDeviceSession(id, device.name ?: id)).copy(
                displayName = device.name ?: existing?.displayName ?: id,
                rssi = result.rssi,
                lastSeenMs = System.currentTimeMillis(),
            )
            sessions[id] = session
            publishSessions()
        }
    }

    fun startScan() {
        val scanner = adapter?.bluetoothLeScanner ?: return
        if (scanning) return
        val filters = listOf(
            ScanFilter.Builder()
                .setServiceUuid(ParcelUuid(WIT_SERVICE))
                .build(),
        )
        val settings = ScanSettings.Builder()
            .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
            .build()
        scanner.startScan(filters, settings, scanCoordinator.callback)
        scanning = true
    }

    fun stopScan() {
        adapter?.bluetoothLeScanner?.stopScan(scanCoordinator.callback)
        scanning = false
    }

    fun connect(deviceId: String) {
        if (!connectionBudget.canConnect(deviceId)) return
        val device = adapter?.getRemoteDevice(deviceId) ?: return
        gattConnections[deviceId]?.close()
        gattConnections[deviceId] = device.connectGatt(context, false, gattCallback(deviceId))
    }

    fun disconnect(deviceId: String) {
        gattConnections.remove(deviceId)?.close()
        connectionBudget.onDisconnected(deviceId)
        sessions[deviceId]?.let { sessions[deviceId] = it.copy(connected = false) }
        publishSessions()
        updateFusion(0f)
    }

    fun setPlacement(deviceId: String, placement: ImuPlacement) {
        sessions[deviceId]?.let { sessions[deviceId] = it.copy(placement = placement) }
        publishSessions()
        updateFusion(0f)
    }

    fun statusList(): List<ImuDeviceStatus> = sessions.values.map {
        ImuDeviceStatus(it.deviceId, it.displayName, it.placement, it.connected, it.signalQuality)
    }

    fun fuseWithPhone(phoneYawDeg: Float): dev.foss.expeditiongauge.fusion.MultiImuFusionOutput {
        val output = multiFusion.fuse(sessions.values.toList(), phoneYawDeg)
        _fusionOutput.value = output
        return output
    }

    private fun gattCallback(deviceId: String) = object : BluetoothGattCallback() {
        override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
            when (newState) {
                BluetoothProfile.STATE_CONNECTED -> {
                    connectionBudget.onConnected(deviceId)
                    sessions[deviceId]?.let { sessions[deviceId] = it.copy(connected = true) }
                    publishSessions()
                    gatt.discoverServices()
                }
                BluetoothProfile.STATE_DISCONNECTED -> {
                    connectionBudget.onDisconnected(deviceId)
                    sessions[deviceId]?.let { sessions[deviceId] = it.copy(connected = false) }
                    publishSessions()
                    gatt.close()
                    gattConnections.remove(deviceId)
                }
            }
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
            val service = gatt.getService(WIT_SERVICE) ?: return
            val notifyChar = service.getCharacteristic(WIT_NOTIFY) ?: return
            gatt.setCharacteristicNotification(notifyChar, true)
            notifyChar.getDescriptor(CLIENT_CONFIG)?.let { desc ->
                desc.value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
                gatt.writeDescriptor(desc)
            }
            service.getCharacteristic(WIT_WRITE)?.let { write ->
                write.value = WitMotionParser.buildRateCommand(50)
                gatt.writeCharacteristic(write)
            }
        }

        override fun onCharacteristicChanged(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic) {
            val sample = WitMotionParser.parsePacket(characteristic.value ?: return) ?: return
            val session = sessions[deviceId] ?: return
            session.filter.onSample(sample)
            sessions[deviceId] = session.copy(lastSeenMs = System.currentTimeMillis())
            publishSessions()
            updateFusion(0f)
        }
    }

    private fun publishSessions() {
        _sessionsFlow.value = sessions.values.toList()
    }

    private fun updateFusion(phoneYaw: Float) {
        _fusionOutput.value = multiFusion.fuse(sessions.values.toList(), phoneYaw)
    }

    companion object {
        val WIT_SERVICE: UUID = UUID.fromString("0000ffe0-0000-1000-8000-00805f9b34fb")
        val WIT_NOTIFY: UUID = UUID.fromString("0000ffe4-0000-1000-8000-00805f9b34fb")
        val WIT_WRITE: UUID = UUID.fromString("0000ffe9-0000-1000-8000-00805f9b34fb")
        private val CLIENT_CONFIG: UUID = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb")
    }
}
