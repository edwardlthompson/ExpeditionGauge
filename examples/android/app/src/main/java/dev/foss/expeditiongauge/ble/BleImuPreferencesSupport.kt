package dev.foss.expeditiongauge.ble

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothGatt
import android.bluetooth.le.ScanFilter
import android.bluetooth.le.ScanSettings
import android.content.Context
import android.os.ParcelUuid
import java.util.concurrent.ConcurrentHashMap

/** Preferred-device / placement persistence for [BleImuManager]. */
internal class BleImuPreferencesSupport(
    private val sessions: ConcurrentHashMap<String, ImuDeviceSession>,
    private val publishSessions: () -> Unit,
    private val updateFusion: (Float) -> Unit,
) {
    private val preferredPlacements = ConcurrentHashMap<String, ImuPlacement>()
    private val preferredDevices = ConcurrentHashMap.newKeySet<String>()
    private var suppressPreferredPersist = false
    var onPlacementsChanged: ((Map<String, ImuPlacement>) -> Unit)? = null
    var onPreferredDevicesChanged: ((Set<String>) -> Unit)? = null

    fun rememberPreferred(deviceId: String) {
        preferredDevices.add(deviceId)
        persistPreferredIfNeeded()
    }

    fun forgetPreferred(deviceId: String) {
        preferredDevices.remove(deviceId)
        persistPreferredIfNeeded()
    }

    fun restorePlacements(map: Map<String, ImuPlacement>) {
        preferredPlacements.clear()
        preferredPlacements.putAll(map.filterValues { it != ImuPlacement.Unassigned })
        preferredPlacements.forEach { (id, placement) ->
            sessions[id]?.let { sessions[id] = it.copy(placement = placement) }
        }
        publishSessions()
    }

    fun placementsSnapshot(): Map<String, ImuPlacement> = preferredPlacements.toMap()

    fun setPlacement(deviceId: String, placement: ImuPlacement) {
        if (placement == ImuPlacement.Unassigned) {
            preferredPlacements.remove(deviceId)
        } else {
            preferredPlacements[deviceId] = placement
        }
        sessions[deviceId]?.let { sessions[deviceId] = it.copy(placement = placement) }
        publishSessions()
        updateFusion(0f)
        onPlacementsChanged?.invoke(placementsSnapshot())
    }

    /** Cold-start reconnect for saved preferred MACs (does not re-persist). */
    fun connectPreferred(
        deviceIds: Set<String>,
        userDisconnected: MutableSet<String>,
        connectGatt: (String) -> Unit,
    ) {
        suppressPreferredPersist = true
        try {
            preferredDevices.clear()
            preferredDevices.addAll(deviceIds)
            deviceIds.forEach { id ->
                preferredPlacements[id]?.let { placement ->
                    sessions[id]?.let { sessions[id] = it.copy(placement = placement) }
                        ?: run { sessions[id] = ImuDeviceSession(id, id, placement = placement) }
                }
                userDisconnected.remove(id)
                connectGatt(id)
            }
            publishSessions()
        } finally {
            suppressPreferredPersist = false
        }
    }

    private fun persistPreferredIfNeeded() {
        if (!suppressPreferredPersist) {
            onPreferredDevicesChanged?.invoke(preferredDevices.toSet())
        }
    }
}

/** Scan / GATT connect helpers used by [BleImuManager]. */
@SuppressLint("MissingPermission")
internal class BleImuConnectionSupport(
    private val context: Context,
    private val adapter: BluetoothAdapter?,
    private val scanCoordinator: BleScanCoordinator,
    private val connectionBudget: BleConnectionBudget,
    private val sessions: ConcurrentHashMap<String, ImuDeviceSession>,
    private val gattConnections: ConcurrentHashMap<String, BluetoothGatt>,
    private val userDisconnected: MutableSet<String>,
    private val onSessionsChanged: () -> Unit,
    private val onFusionUpdate: (Float) -> Unit,
) {
    private var scanning = false

    fun startScan() {
        val scanner = adapter?.bluetoothLeScanner ?: return
        if (scanning) return
        val filters = listOf(
            ScanFilter.Builder().setServiceUuid(ParcelUuid(BleImuManager.WIT_SERVICE)).build(),
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

    fun connectGatt(deviceId: String) {
        if (!connectionBudget.canConnect(deviceId)) return
        val device = adapter?.getRemoteDevice(deviceId) ?: return
        gattConnections[deviceId]?.close()
        gattConnections[deviceId] = device.connectGatt(
            context,
            false,
            BleImuGattCallback(
                deviceId = deviceId,
                sessions = sessions,
                gattConnections = gattConnections,
                connectionBudget = connectionBudget,
                onSessionsChanged = onSessionsChanged,
                onFusionUpdate = onFusionUpdate,
                onReconnect = { id ->
                    if (!userDisconnected.contains(id)) connectGatt(id)
                },
            ),
        )
    }

    fun disconnectGatt(deviceId: String) {
        gattConnections.remove(deviceId)?.close()
        connectionBudget.onDisconnected(deviceId)
        sessions[deviceId]?.let { sessions[deviceId] = it.copy(connected = false) }
    }
}
