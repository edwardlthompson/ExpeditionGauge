package dev.foss.expeditiongauge.obd

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import android.provider.Settings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/** Classic BT discovery + bond helpers for OBD / NMEA pairing UX. */
@SuppressLint("MissingPermission")
class ClassicBluetoothPairing(private val context: Context) {
    private val adapter: BluetoothAdapter? =
        (context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager).adapter

    private val _discovered = MutableStateFlow<List<Pair<String, String>>>(emptyList())
    val discovered: StateFlow<List<Pair<String, String>>> = _discovered.asStateFlow()

    private var discovering = false

    fun bondedDevices(): List<Pair<String, String>> =
        adapter?.bondedDevices?.map { it.address to (it.name ?: it.address) } ?: emptyList()

    fun startDiscovery(): Boolean {
        val bt = adapter ?: return false
        if (discovering) return true
        _discovered.value = emptyList()
        discovering = bt.startDiscovery()
        return discovering
    }

    fun stopDiscovery() {
        adapter?.cancelDiscovery()
        discovering = false
    }

    fun onDeviceFound(device: BluetoothDevice) {
        val entry = device.address to (device.name ?: device.address)
        val current = _discovered.value
        if (current.none { it.first == entry.first }) {
            _discovered.value = current + entry
        }
    }

    fun createBond(address: String): Boolean {
        val device = adapter?.getRemoteDevice(address) ?: return false
        return runCatching { device.createBond() }.getOrDefault(false)
    }

    fun openSystemBluetoothSettings(): Intent =
        Intent(Settings.ACTION_BLUETOOTH_SETTINGS).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
}
