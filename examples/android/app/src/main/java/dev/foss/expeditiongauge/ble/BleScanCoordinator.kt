package dev.foss.expeditiongauge.ble

import android.annotation.SuppressLint
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanFilter
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.os.Handler
import android.os.Looper
import android.util.Log

/**
 * Demultiplexes a single BLE [ScanCallback] to IMU and TPMS listeners.
 *
 * One OS scan session is shared and **debounced** so IMU+TPMS register as a single
 * [BluetoothLeScanner.startScan] (Android limits ~5 starts / 30s).
 */
class BleScanCoordinator {
    enum class Client { IMU, TPMS }

    private val imuListeners = mutableListOf<(ScanResult) -> Unit>()
    private val tpmsListeners = mutableListOf<(ScanResult) -> Unit>()
    private val clientFilters = mutableMapOf<Client, List<ScanFilter>>()
    private val lock = Any()
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var session: BleOsScanSession

    val callback: ScanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult) = dispatch(result)

        override fun onBatchScanResults(results: MutableList<ScanResult>) {
            results.forEach(::dispatch)
        }

        override fun onScanFailed(errorCode: Int) {
            Log.w(TAG, "onScanFailed code=$errorCode")
            synchronized(lock) { session.markFailed() }
        }
    }

    private val applyRunnable = Runnable { synchronized(lock) { session.apply() } }

    init {
        session = BleOsScanSession(callback) { clientFilters }
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

    /** Register [client]; OS start is debounced so multiple clients coalesce. */
    @SuppressLint("MissingPermission")
    fun startClient(
        client: Client,
        scanner: BluetoothLeScanner,
        filters: List<ScanFilter>,
        settings: ScanSettings = BleOsScanSession.DEFAULT_SETTINGS,
    ) {
        synchronized(lock) {
            session.prepare(scanner, settings)
            clientFilters[client] = filters
            scheduleApplyLocked()
        }
    }

    /** Drop [client]; stop OS scan only when none remain (no mid-scan restart). */
    @SuppressLint("MissingPermission")
    fun stopClient(client: Client) {
        synchronized(lock) {
            if (clientFilters.remove(client) == null) return
            scheduleApplyLocked()
        }
    }

    private fun scheduleApplyLocked() {
        handler.removeCallbacks(applyRunnable)
        handler.postDelayed(applyRunnable, APPLY_DEBOUNCE_MS)
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

    companion object {
        private const val TAG = "ExpeditionGauge/BleScan"
        private const val APPLY_DEBOUNCE_MS = 150L
    }
}
