package dev.foss.expeditiongauge.obd

import android.bluetooth.BluetoothSocket
import android.os.SystemClock
import android.util.Log
import dev.foss.expeditiongauge.obd.dtc.DtcCatalog
import dev.foss.expeditiongauge.obd.dtc.DtcEntry
import dev.foss.expeditiongauge.settings.ObdPidConfig
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import kotlinx.coroutines.delay

/** Mode 01 PID poll + connect-triggered Mode 03/07, then gated rescan. */
internal object ObdPollLoop {
    const val POLL_INTERVAL_MS = 200L
    private const val TAG = "ExpeditionGauge/Obd"

    suspend fun run(
        sock: BluetoothSocket,
        pidConfig: ObdPidConfig,
        catalog: DtcCatalog,
        isActive: () -> Boolean,
        onSnapshot: (ObdSnapshot) -> Unit,
        currentDtcs: () -> List<DtcEntry>,
        onDtcs: (List<DtcEntry>) -> Unit,
        clock: () -> Long = { SystemClock.elapsedRealtime() },
        scheduler: ObdDtcScanScheduler = ObdDtcScanScheduler(),
        consumeClear: () -> Boolean = { false },
    ) {
        val writer = OutputStreamWriter(sock.outputStream)
        val reader = BufferedReader(InputStreamReader(sock.inputStream))
        var previous = ObdSnapshot(connected = true)
        try {
            pump(
                isActive = isActive,
                clock = clock,
                scheduler = scheduler,
                currentDtcs = currentDtcs,
                onDtcs = onDtcs,
                scanDtcs = { prev -> ObdDtcReader.refresh(reader, writer, catalog, prev) },
                pollOnce = {
                    previous = ObdPollHelper.pollSnapshot(reader, writer, pidConfig, previous)
                    onSnapshot(previous)
                },
                consumeClear = consumeClear,
                performClear = { Elm327DtcClear.request(reader, writer) },
            )
        } catch (e: IOException) {
            // Socket closed / broken pipe — disconnect path, not a fatal crash.
            Log.w(TAG, "OBD poll socket closed: ${e.message}")
            onSnapshot(ObdSnapshot(connected = false))
        }
    }

    /**
     * Every [pump] start is a confirmed connection (or reconnect).
     * Immediate Mode 03/07, then [ObdDtcReader.RESCAN_INTERVAL_MS] fallback.
     */
    internal suspend fun pump(
        isActive: () -> Boolean,
        clock: () -> Long,
        scheduler: ObdDtcScanScheduler,
        currentDtcs: () -> List<DtcEntry>,
        onDtcs: (List<DtcEntry>) -> Unit,
        scanDtcs: (List<DtcEntry>) -> List<DtcEntry>,
        pollOnce: suspend () -> Unit,
        delayMs: suspend (Long) -> Unit = { delay(it) },
        consumeClear: () -> Boolean = { false },
        performClear: () -> Boolean = { false },
    ) {
        scheduler.onConnectionConfirmed(clock())
        while (isActive()) {
            val cleared = consumeClear() && performClear()
            if (cleared) {
                onDtcs(emptyList())
            }
            val now = clock()
            if (!cleared && scheduler.due(now)) {
                onDtcs(scanDtcs(currentDtcs()))
                scheduler.markAttempt(now)
            }
            pollOnce()
            delayMs(POLL_INTERVAL_MS)
        }
    }
}
