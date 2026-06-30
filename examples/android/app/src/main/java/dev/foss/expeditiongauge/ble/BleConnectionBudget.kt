package dev.foss.expeditiongauge.ble

/**
 * Tracks active GATT connections for BLE IMU devices (max 4).
 */
class BleConnectionBudget(private val maxConnections: Int = MAX_IMU_CONNECTIONS) {
    private val active = mutableSetOf<String>()

    fun canConnect(deviceId: String): Boolean =
        deviceId in active || active.size < maxConnections

    fun onConnected(deviceId: String) {
        if (active.size < maxConnections) active += deviceId
    }

    fun onDisconnected(deviceId: String) {
        active -= deviceId
    }

    val activeCount: Int get() = active.size

    companion object {
        const val MAX_IMU_CONNECTIONS = 4
    }
}
