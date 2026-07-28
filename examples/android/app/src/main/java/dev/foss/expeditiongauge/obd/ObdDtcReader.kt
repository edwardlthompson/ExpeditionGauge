package dev.foss.expeditiongauge.obd

import android.bluetooth.BluetoothSocket
import android.util.Log
import dev.foss.expeditiongauge.obd.dtc.DtcCatalog
import dev.foss.expeditiongauge.obd.dtc.DtcEntry
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter

/** One-shot Mode 03 stored-DTC read after ELM init. */
internal object ObdDtcReader {
    private const val TAG = "ExpeditionGauge/Obd"

    fun readOnce(sock: BluetoothSocket, catalog: DtcCatalog): List<DtcEntry> =
        try {
            val writer = OutputStreamWriter(sock.outputStream)
            val reader = BufferedReader(InputStreamReader(sock.inputStream))
            val codes = Elm327Protocol.requestStoredDtcs(reader, writer)
            Log.i(TAG, "Mode 03 DTCs: ${codes.size}")
            codes.map { code -> DtcEntry(code = code, description = catalog.describe(code)) }
        } catch (e: Exception) {
            Log.w(TAG, "Mode 03 read failed (continuing Mode 01): ${e.message}")
            emptyList()
        }
}
