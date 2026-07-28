package dev.foss.expeditiongauge.obd

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter

/** Bonded-device listing helpers for [ObdClassicManager]. */
@SuppressLint("MissingPermission")
internal object ObdDeviceDirectory {
    fun pairedDevices(adapter: BluetoothAdapter?): List<Pair<String, String>> =
        adapter?.bondedDevices?.map { it.address to (it.name ?: it.address) } ?: emptyList()

    /** Prefer names that look like ELM/OBD adapters; still includes all bonds afterward. */
    fun suggestedObdDevices(adapter: BluetoothAdapter?): List<Pair<String, String>> {
        val all = pairedDevices(adapter)
        val preferred = all.filter { (_, name) ->
            val n = name.lowercase()
            n.contains("obd") || n.contains("elm") || n.contains("vgate") || n.contains("obdlink")
        }
        return (preferred + all.filter { it !in preferred }).distinct()
    }
}
