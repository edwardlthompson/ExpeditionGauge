package dev.foss.expeditiongauge.obd

import android.bluetooth.BluetoothSocket
import android.util.Log
import dev.foss.expeditiongauge.obd.dtc.DtcCatalog
import dev.foss.expeditiongauge.obd.dtc.DtcEntry
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter

/**
 * Stored (Mode 03) + pending (Mode 07) DTC reads. ECUs do not push codes.
 */
internal object ObdDtcReader {
    private const val TAG = "ExpeditionGauge/Obd"

    /** Cadence for DTC refresh while connected (not every Mode 01 poll). */
    const val RESCAN_INTERVAL_MS = 30_000L

    fun readOnce(sock: BluetoothSocket, catalog: DtcCatalog): List<DtcEntry> =
        try {
            val writer = OutputStreamWriter(sock.outputStream)
            val reader = BufferedReader(InputStreamReader(sock.inputStream))
            refresh(reader, writer, catalog, emptyList())
        } catch (e: Exception) {
            Log.w(TAG, "DTC read failed (continuing Mode 01): ${e.message}")
            emptyList()
        }

    fun readStored(
        reader: BufferedReader,
        writer: OutputStreamWriter,
        catalog: DtcCatalog,
    ): List<DtcEntry> =
        try {
            val codes = Elm327Protocol.requestStoredDtcs(reader, writer)
            Log.i(TAG, "Mode 03 DTCs: ${codes.size}")
            codes.map { code -> DtcEntry(code = code, description = catalog.describe(code)) }
        } catch (e: Exception) {
            Log.w(TAG, "Mode 03 read failed: ${e.message}")
            emptyList()
        }

    fun readPending(
        reader: BufferedReader,
        writer: OutputStreamWriter,
        catalog: DtcCatalog,
    ): List<DtcEntry> =
        try {
            val codes = Elm327Protocol.requestPendingDtcs(reader, writer)
            Log.i(TAG, "Mode 07 pending DTCs: ${codes.size}")
            codes.map { code ->
                val title = catalog.describe(code)
                val desc = if (title == DtcCatalog.UNKNOWN) "Pending" else "Pending — $title"
                DtcEntry(code = code, description = desc)
            }
        } catch (e: Exception) {
            Log.w(TAG, "Mode 07 read failed: ${e.message}")
            emptyList()
        }

    /**
     * Always probe Mode 03 + Mode 07. Keep previous list if the ECU still reports
     * a stored count but both reads came back empty (noisy ELM).
     */
    fun refresh(
        reader: BufferedReader,
        writer: OutputStreamWriter,
        catalog: DtcCatalog,
        previous: List<DtcEntry>,
    ): List<DtcEntry> {
        val status = ObdMonitorStatus.request(reader, writer)
        val count = status?.storedDtcCount
        Log.d(TAG, "0101 mil=${status?.milOn} count=$count")
        val stored = readStored(reader, writer, catalog)
        val pending = readPending(reader, writer, catalog)
        val merged = mergeDistinct(stored, pending)
        if (merged.isEmpty() && count != null && count > 0 && previous.isNotEmpty()) {
            Log.w(TAG, "DTC reads empty but 0101 count=$count; keeping previous")
            return previous
        }
        return merged
    }

    internal fun mergeDistinct(stored: List<DtcEntry>, pending: List<DtcEntry>): List<DtcEntry> {
        val seen = stored.map { it.code }.toHashSet()
        return stored + pending.filter { it.code !in seen }
    }
}
