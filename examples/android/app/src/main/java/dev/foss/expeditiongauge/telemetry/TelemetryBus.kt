package dev.foss.expeditiongauge.telemetry

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class TelemetryBus {
    private val _snapshots = MutableStateFlow(TelemetrySnapshot.empty())
    val snapshots: StateFlow<TelemetrySnapshot> = _snapshots.asStateFlow()

    fun publish(snapshot: TelemetrySnapshot) {
        _snapshots.value = snapshot
    }
}
