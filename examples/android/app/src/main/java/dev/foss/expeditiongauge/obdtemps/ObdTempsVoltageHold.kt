package dev.foss.expeditiongauge.obdtemps

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ObdTempsVoltageHold {
    private val _snap = MutableStateFlow<ObdTempsVoltageSnapshot?>(null)
    val snap: StateFlow<ObdTempsVoltageSnapshot?> = _snap.asStateFlow()

    fun set(value: ObdTempsVoltageSnapshot?) {
        _snap.value = value
    }
}
