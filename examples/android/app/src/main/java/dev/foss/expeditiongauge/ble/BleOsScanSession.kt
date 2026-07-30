package dev.foss.expeditiongauge.ble

import android.annotation.SuppressLint
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanFilter
import android.bluetooth.le.ScanSettings
import android.util.Log

/** Owns a single debounced OS BLE scan session for [BleScanCoordinator]. */
internal class BleOsScanSession(
    private val callback: ScanCallback,
    private val filtersOf: () -> Map<BleScanCoordinator.Client, List<ScanFilter>>,
) {
    private var scanner: BluetoothLeScanner? = null
    private var settings: ScanSettings = DEFAULT_SETTINGS
    private var osScanning = false
    private var appliedClients: Set<BleScanCoordinator.Client> = emptySet()

    fun prepare(scanner: BluetoothLeScanner, settings: ScanSettings) {
        this.scanner = scanner
        this.settings = settings
    }

    fun markFailed() {
        osScanning = false
        appliedClients = emptySet()
    }

    @SuppressLint("MissingPermission")
    fun apply() {
        val clients = filtersOf()
        val s = scanner
        if (clients.isEmpty()) {
            stop()
            appliedClients = emptySet()
            return
        }
        if (s == null) return
        val want = clients.keys.toSet()
        if (osScanning && appliedClients.containsAll(want)) {
            appliedClients = want
            return
        }
        if (osScanning) stop()
        start(s, settings, clients.values.flatten())
        if (osScanning) appliedClients = want
    }

    @SuppressLint("MissingPermission")
    private fun start(scanner: BluetoothLeScanner, settings: ScanSettings, filters: List<ScanFilter>) {
        if (filters.isEmpty()) return
        try {
            scanner.startScan(filters, settings, callback)
            osScanning = true
        } catch (e: IllegalArgumentException) {
            Log.w(TAG, "startScan failed: ${e.message}")
            osScanning = false
        }
    }

    @SuppressLint("MissingPermission")
    private fun stop() {
        if (!osScanning) return
        try {
            scanner?.stopScan(callback)
        } catch (e: IllegalArgumentException) {
            Log.w(TAG, "stopScan ignored: ${e.message}")
        }
        osScanning = false
    }

    companion object {
        private const val TAG = "ExpeditionGauge/BleScan"
        val DEFAULT_SETTINGS: ScanSettings =
            ScanSettings.Builder().setScanMode(ScanSettings.SCAN_MODE_BALANCED).build()
    }
}
