package dev.foss.expeditiongauge.ble.tpms

import dev.foss.expeditiongauge.ble.ImuPlacement
import dev.foss.expeditiongauge.telemetry.TpmsCornerReading
import dev.foss.expeditiongauge.telemetry.TpmsSnapshot
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.concurrent.ConcurrentHashMap

/** Sessions + exclusive corner map for [BleTpmsManager]. */
internal class BleTpmsSessionRegistry {
    private val sessions = ConcurrentHashMap<String, TpmsDeviceSession>()
    private val cornerAssignments = ConcurrentHashMap<String, ImuPlacement>()

    private val _snapshot = MutableStateFlow(TpmsSnapshot())
    val snapshot: StateFlow<TpmsSnapshot> = _snapshot.asStateFlow()

    private val _sessionsFlow = MutableStateFlow<List<TpmsDeviceSession>>(emptyList())
    val sessionsFlow: StateFlow<List<TpmsDeviceSession>> = _sessionsFlow.asStateFlow()

    var onAssignmentsChanged: ((Map<String, ImuPlacement>) -> Unit)? = null

    fun assignmentsSnapshot(): Map<String, ImuPlacement> = cornerAssignments.toMap()

    fun knownSessions(): List<TpmsDeviceSession> = sessions.values.toList()

    fun cornerFor(mac: String): ImuPlacement =
        cornerAssignments[mac] ?: sessions[mac]?.corner ?: ImuPlacement.Unassigned

    fun putLiveSession(session: TpmsDeviceSession) {
        sessions[session.macAddress] = session
        refreshSessions()
        publishSnapshot()
    }

    fun restoreAssignments(map: Map<String, ImuPlacement>) {
        synchronized(this) {
            cornerAssignments.clear()
            cornerAssignments.putAll(
                map
                    .mapKeys { it.key.trim().uppercase() }
                    .filterValues { it != ImuPlacement.Unassigned },
            )
            sessions.keys.toList().forEach { mac ->
                val corner = cornerAssignments[mac] ?: ImuPlacement.Unassigned
                sessions[mac]?.let { sessions[mac] = it.copy(corner = corner) }
            }
            cornerAssignments.forEach { (mac, corner) ->
                ensureAssignedSession(mac, corner)
            }
            refreshSessions()
            publishSnapshot()
        }
    }

    fun assignCornerExclusive(macAddress: String, corner: ImuPlacement) {
        synchronized(this) {
            val next = TpmsCornerAssignLogic.exclusiveAssign(
                current = cornerAssignments.toMap(),
                macAddress = macAddress,
                corner = corner,
            )
            cornerAssignments.clear()
            cornerAssignments.putAll(next)
            sessions.keys.toList().forEach { mac ->
                val c = cornerAssignments[mac] ?: ImuPlacement.Unassigned
                sessions[mac]?.let { sessions[mac] = it.copy(corner = c) }
            }
            if (corner != ImuPlacement.Unassigned) {
                ensureAssignedSession(macAddress.trim().uppercase(), corner)
            }
            refreshSessions()
            publishSnapshot()
            onAssignmentsChanged?.invoke(assignmentsSnapshot())
        }
    }

    fun ensureAssignedSession(macAddress: String, corner: ImuPlacement) {
        val mac = macAddress.trim().uppercase()
        if (mac.isEmpty() || corner == ImuPlacement.Unassigned) return
        val existing = sessions[mac]
        if (existing == null) {
            sessions[mac] = TpmsDeviceSession(
                macAddress = mac,
                corner = corner,
                lastReading = null,
                parserId = BleTpmsManager.PENDING_PARSER_ID,
                rssi = 0,
            )
        } else if (existing.corner != corner) {
            sessions[mac] = existing.copy(corner = corner)
        }
    }

    private fun refreshSessions() {
        _sessionsFlow.value = sessions.values.sortedBy { it.macAddress }
    }

    private fun publishSnapshot() {
        var snap = TpmsSnapshot()
        sessions.values.forEach { session ->
            val reading = session.lastReading ?: return@forEach
            if (session.corner == ImuPlacement.Unassigned) return@forEach
            val corner = TpmsCornerReading(
                pressureKpa = reading.pressureKpa,
                tempC = reading.tempC,
                batteryPct = (reading.batteryVolts / 3.0f * 100f).toInt().coerceIn(0, 100),
                lastSeenMs = reading.timestampMs,
            )
            snap = snap.withCorner(session.corner, corner)
            TpmsTelemetryLog.publish(
                corner = session.corner.label,
                pressureKpa = reading.pressureKpa,
                tempC = reading.tempC,
                parserId = session.parserId,
            )
        }
        _snapshot.value = snap
    }
}
