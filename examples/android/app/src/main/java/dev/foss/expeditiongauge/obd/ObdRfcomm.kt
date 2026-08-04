package dev.foss.expeditiongauge.obd

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.util.Log
import java.util.UUID

/** Secure SPP then insecure fallback (common ELM327 clone pattern). */
@SuppressLint("MissingPermission")
internal object ObdRfcomm {
    private const val TAG = "ExpeditionGauge/Obd"
    val SPP_UUID: UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb")

    fun open(adapter: BluetoothAdapter?, address: String): BluetoothSocket {
        val device = adapter?.getRemoteDevice(address) ?: error("No adapter/device")
        adapter.cancelDiscovery()
        return try {
            connect(device, secure = true)
        } catch (e: Exception) {
            Log.w(TAG, "Secure SPP failed, trying insecure: ${e.message}")
            connect(device, secure = false)
        }
    }

    private fun connect(device: BluetoothDevice, secure: Boolean): BluetoothSocket {
        val sock = if (secure) {
            device.createRfcommSocketToServiceRecord(SPP_UUID)
        } else {
            device.createInsecureRfcommSocketToServiceRecord(SPP_UUID)
        }
        sock.connect()
        return sock
    }
}
