package dev.foss.expeditiongauge.ble

import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattDescriptor
import android.bluetooth.BluetoothProfile
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

internal class BleImuGattCallback(
    private val deviceId: String,
    private val sessions: ConcurrentHashMap<String, ImuDeviceSession>,
    private val gattConnections: ConcurrentHashMap<String, BluetoothGatt>,
    private val connectionBudget: BleConnectionBudget,
    private val onSessionsChanged: () -> Unit,
    private val onFusionUpdate: (Float) -> Unit,
    private val onReconnect: (String) -> Unit,
) : BluetoothGattCallback() {
    override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
        when (newState) {
            BluetoothProfile.STATE_CONNECTED -> {
                connectionBudget.onConnected(deviceId)
                sessions[deviceId]?.let { sessions[deviceId] = it.copy(connected = true) }
                onSessionsChanged()
                gatt.discoverServices()
            }
            BluetoothProfile.STATE_DISCONNECTED -> {
                connectionBudget.onDisconnected(deviceId)
                sessions[deviceId]?.let { sessions[deviceId] = it.copy(connected = false) }
                onSessionsChanged()
                gatt.close()
                gattConnections.remove(deviceId)
                onReconnect(deviceId)
            }
        }
    }

    override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
        val service = gatt.getService(BleImuManager.WIT_SERVICE) ?: return
        val notifyChar = service.getCharacteristic(BleImuManager.WIT_NOTIFY) ?: return
        gatt.setCharacteristicNotification(notifyChar, true)
        notifyChar.getDescriptor(CLIENT_CONFIG)?.let { desc ->
            desc.value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
            gatt.writeDescriptor(desc)
        }
        service.getCharacteristic(BleImuManager.WIT_WRITE)?.let { write ->
            write.value = WitMotionParser.buildRateCommand(50)
            gatt.writeCharacteristic(write)
        }
    }

    override fun onCharacteristicChanged(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic) {
        val sample = WitMotionParser.parsePacket(characteristic.value ?: return) ?: return
        val session = sessions[deviceId] ?: return
        session.filter.onSample(sample)
        sessions[deviceId] = session.copy(lastSeenMs = System.currentTimeMillis())
        onSessionsChanged()
        onFusionUpdate(0f)
    }

    companion object {
        private val CLIENT_CONFIG: UUID = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb")
    }
}
