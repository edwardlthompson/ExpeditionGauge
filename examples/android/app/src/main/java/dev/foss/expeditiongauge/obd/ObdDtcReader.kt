package dev.foss.expeditiongauge.obd

import android.bluetooth.BluetoothSocket
import android.util.Log
import dev.foss.expeditiongauge.obd.dtc.DtcCatalog
import dev.foss.expeditiongauge.obd.dtc.DtcEntry
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter

/**
 * Stored-DTC reads (Mode 03). ECUs do **not** push codes — the tester must poll.
 * Prefer Mode 01 PID 01 (MIL + count) so Mode 03 only runs when needed.
 */
internal object ObdDtcReader {
    private const val TAG = "ExpeditionGauge/Obd"

    /** Cadence for 0101 / Mode 03 refresh while connected (not every Mode 01 poll). */
    const val RESCAN_INTERVAL_MS = 30_000L

    fun readOnce(sock: BluetoothSocket, catalog: DtcCatalog): List<DtcEntry> =
        try {
            val writer = OutputStreamWriter(sock.outputStream)
            val reader = BufferedReader(InputStreamReader(sock.inputStream))
            readStored(reader, writer, catalog)
        } catch (e: Exception) {
            Log.w(TAG, "Mode 03 read failed (continuing Mode 01): ${e.message}")
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

    /**
     * Light refresh on the shared ELM stream: ask 0101 first; skip Mode 03 when
     * the ECU reports zero codes and the UI already shows none.
     */
    fun refresh(
        reader: BufferedReader,
        writer: OutputStreamWriter,
        catalog: DtcCatalog,
        previous: List<DtcEntry>,
    ): List<DtcEntry> {
        val status = ObdMonitorStatus.request(reader, writer)
        val count = status?.storedDtcCount
        if (count != null) {
            if (count == 0) {
                if (previous.isEmpty()) return previous
                Log.i(TAG, "0101: 0 stored DTCs (cleared)")
                return emptyList()
            }
        } else {
            Log.d(TAG, "0101 unavailable; Mode 03 fallback")
        }
        val next = readStored(reader, writer, catalog)
        if (next.isEmpty() && count != null && count > 0 && previous.isNotEmpty()) {
            Log.w(TAG, "Mode 03 empty but 0101 count=$count; keeping previous")
            return previous
        }
        return next
    }
}
