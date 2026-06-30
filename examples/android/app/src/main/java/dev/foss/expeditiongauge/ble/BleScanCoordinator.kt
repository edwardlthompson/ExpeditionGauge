package dev.foss.expeditiongauge.ble

import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult

/**
 * Demultiplexes a single BLE scan callback to IMU and TPMS listeners.
 */
class BleScanCoordinator {
    private val imuListeners = mutableListOf<(ScanResult) -> Unit>()
    private val tpmsListeners = mutableListOf<(ScanResult) -> Unit>()

    val callback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            dispatch(result)
        }

        override fun onBatchScanResults(results: MutableList<ScanResult>) {
            results.forEach(::dispatch)
        }
    }

    fun addImuListener(listener: (ScanResult) -> Unit) {
        imuListeners += listener
    }

    fun addTpmsListener(listener: (ScanResult) -> Unit) {
        tpmsListeners += listener
    }

    fun removeImuListener(listener: (ScanResult) -> Unit) {
        imuListeners -= listener
    }

    fun removeTpmsListener(listener: (ScanResult) -> Unit) {
        tpmsListeners -= listener
    }

    private fun dispatch(result: ScanResult) {
        val name = result.device.name ?: return
        when {
            name.startsWith("WT", ignoreCase = true) ||
                result.scanRecord?.serviceUuids?.any { it.uuid.toString().contains("ffe0", ignoreCase = true) } == true ->
                imuListeners.forEach { it(result) }
            name.startsWith("BR", ignoreCase = true) ||
                result.scanRecord?.serviceUuids?.any { it.uuid.toString().contains("27a5", ignoreCase = true) } == true ->
                tpmsListeners.forEach { it(result) }
        }
    }
}
