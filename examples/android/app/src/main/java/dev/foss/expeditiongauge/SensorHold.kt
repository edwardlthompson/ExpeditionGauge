package dev.foss.expeditiongauge

/**
 * Refcounted sensor lifetime so Android Auto can keep IMU/GPS/BLE alive after
 * [android.app.Activity.onStop] while the car session is still connected.
 */
class SensorHold(
    private val onStart: () -> Unit,
    private val onStop: () -> Unit,
) {
    private val lock = Any()
    private var count = 0

    fun acquire() {
        synchronized(lock) {
            if (count == 0) onStart()
            count++
        }
    }

    fun release() {
        synchronized(lock) {
            if (count <= 0) return
            count--
            if (count == 0) onStop()
        }
    }

    fun holdCount(): Int = synchronized(lock) { count }
}
