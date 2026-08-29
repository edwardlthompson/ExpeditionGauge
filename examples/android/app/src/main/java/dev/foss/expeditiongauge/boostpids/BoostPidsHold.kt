package dev.foss.expeditiongauge.boostpids

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class BoostPidsHold {
    private val _snap = MutableStateFlow<BoostPidSnapshot?>(null)
    val snap: StateFlow<BoostPidSnapshot?> = _snap.asStateFlow()

    fun set(value: BoostPidSnapshot?) {
        _snap.value = value
    }
}
