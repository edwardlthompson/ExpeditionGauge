package dev.foss.expeditiongauge.obd

/**
 * Limits concurrent Bluetooth Classic SPP sockets (OBD + external GPS).
 */
class ClassicBluetoothBudget(private val maxConnections: Int = MAX_CLASSIC_SPP) {
    enum class Slot { OBD, EXTERNAL_GPS }

    private val active = mutableSetOf<Slot>()

    fun canConnect(slot: Slot): Boolean = slot in active || active.size < maxConnections

    fun onConnected(slot: Slot) {
        if (active.size < maxConnections) active += slot
    }

    fun onDisconnected(slot: Slot) {
        active -= slot
    }

    fun isActive(slot: Slot): Boolean = slot in active

    companion object {
        const val MAX_CLASSIC_SPP = 2
    }
}
