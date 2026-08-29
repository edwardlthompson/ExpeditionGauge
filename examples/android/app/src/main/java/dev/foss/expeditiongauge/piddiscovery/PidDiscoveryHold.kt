package dev.foss.expeditiongauge.piddiscovery

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class PidDiscoveryHold {
    @Volatile
    private var requested = false
    private val _pids = MutableStateFlow<Set<Int>?>(null)
    val pids: StateFlow<Set<Int>?> = _pids.asStateFlow()

    fun request() {
        requested = true
    }

    fun consume(): Boolean {
        if (!requested) return false
        requested = false
        return true
    }

    fun set(value: Set<Int>?) {
        _pids.value = value
    }
}
