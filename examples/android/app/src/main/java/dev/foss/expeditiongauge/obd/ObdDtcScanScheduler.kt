package dev.foss.expeditiongauge.obd

/**
 * When to run Mode 03/07 on a live ELM stream.
 *
 * A confirmed handshake (RFCOMM + AT init, including reconnects) always
 * schedules an immediate scan. The ~30 s cadence is only the fallback.
 */
internal class ObdDtcScanScheduler(
    private val rescanIntervalMs: Long = ObdDtcReader.RESCAN_INTERVAL_MS,
) {
    private var nextScanAtMs: Long = Long.MAX_VALUE

    fun onConnectionConfirmed(nowMs: Long) {
        nextScanAtMs = nowMs
    }

    fun due(nowMs: Long): Boolean = nowMs >= nextScanAtMs

    fun markAttempt(nowMs: Long) {
        nextScanAtMs = nowMs + rescanIntervalMs
    }
}
