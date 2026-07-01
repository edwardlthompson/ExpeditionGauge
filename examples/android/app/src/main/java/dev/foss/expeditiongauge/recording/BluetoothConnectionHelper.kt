package dev.foss.expeditiongauge.recording

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothProfile
import android.content.Context

internal object BluetoothConnectionHelper {
    /**
     * Returns the first allowlisted address that appears in [connectedAddresses].
     * Order of [connectedAddresses] is preserved (first match wins).
     */
    fun firstAllowlistedAddress(
        allowlist: Set<String>,
        connectedAddresses: Collection<String>,
    ): String? = connectedAddresses.firstOrNull { it in allowlist }

    /**
     * Finds an allowlisted device with an active Bluetooth connection.
     * Bonded/paired devices that are not connected are ignored.
     */
    @SuppressLint("MissingPermission")
    fun findConnectedAllowlistedAddress(
        context: Context,
        allowlist: Set<String>,
    ): String? {
        if (allowlist.isEmpty()) return null
        val manager = context.getSystemService(Context.BLUETOOTH_SERVICE) as? BluetoothManager ?: return null
        val adapter = manager.adapter ?: return null
        if (!adapter.isEnabled) return null

        val connectedAddresses = linkedSetOf<String>()
        for (profile in CONNECTED_PROFILES) {
            try {
                manager.getConnectedDevices(profile)
                    .mapNotNull { it.address }
                    .forEach { connectedAddresses.add(it) }
            } catch (_: IllegalArgumentException) {
                // Profile not supported on this device.
            }
        }

        firstAllowlistedAddress(allowlist, connectedAddresses)?.let { return it }

        val bonded = adapter.bondedDevices.orEmpty()
        val aclConnected = bonded
            .mapNotNull { device -> device.address?.takeIf { isAclConnected(device) } }
        return firstAllowlistedAddress(allowlist, aclConnected)
    }

    fun isAclConnected(device: BluetoothDevice): Boolean =
        runCatching {
            @Suppress("DiscouragedPrivateApi")
            device.javaClass.getMethod("isConnected").invoke(device) as Boolean
        }.getOrDefault(false)

    private val CONNECTED_PROFILES = intArrayOf(
        BluetoothProfile.HEADSET,
        BluetoothProfile.A2DP,
        BluetoothProfile.HID_DEVICE,
        BluetoothProfile.GATT,
    )
}
