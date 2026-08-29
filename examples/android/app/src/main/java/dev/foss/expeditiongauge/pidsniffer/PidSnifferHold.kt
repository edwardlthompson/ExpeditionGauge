package dev.foss.expeditiongauge.pidsniffer

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class PidSnifferHold {
    @Volatile
    private var pending: String? = null
    private val _last = MutableStateFlow<String?>(null)
    val last: StateFlow<String?> = _last.asStateFlow()

    fun request(command: String) {
        pending = PidSniffer.normalize(command)
    }

    fun consume(): String? {
        val cmd = pending
        pending = null
        return cmd
    }

    fun set(value: String?) {
        _last.value = value
    }
}
