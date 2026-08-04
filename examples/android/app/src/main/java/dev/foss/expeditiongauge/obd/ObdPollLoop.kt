package dev.foss.expeditiongauge.obd

import android.bluetooth.BluetoothSocket
import android.os.SystemClock
import dev.foss.expeditiongauge.obd.dtc.DtcCatalog
import dev.foss.expeditiongauge.obd.dtc.DtcEntry
import dev.foss.expeditiongauge.settings.ObdPidConfig
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import kotlinx.coroutines.delay

/** Mode 01 PID poll + gated Mode 03 DTC refresh on one ELM stream. */
internal object ObdPollLoop {
    const val POLL_INTERVAL_MS = 200L

    suspend fun run(
        sock: BluetoothSocket,
        pidConfig: ObdPidConfig,
        catalog: DtcCatalog,
        isActive: () -> Boolean,
        onSnapshot: (ObdSnapshot) -> Unit,
        currentDtcs: () -> List<DtcEntry>,
        onDtcs: (List<DtcEntry>) -> Unit,
    ) {
        val writer = OutputStreamWriter(sock.outputStream)
        val reader = BufferedReader(InputStreamReader(sock.inputStream))
        // Immediate first Mode 03 (after connect) so DTCs are not on the RFCOMM critical path.
        var nextDtcAt = 0L
        var previous = ObdSnapshot(connected = true)
        while (isActive()) {
            val now = SystemClock.elapsedRealtime()
            if (now >= nextDtcAt) {
                nextDtcAt = now + ObdDtcReader.RESCAN_INTERVAL_MS
                onDtcs(ObdDtcReader.refresh(reader, writer, catalog, currentDtcs()))
            }
            previous = ObdPollHelper.pollSnapshot(reader, writer, pidConfig, previous)
            onSnapshot(previous)
            delay(POLL_INTERVAL_MS)
        }
    }
}
