package dev.foss.expeditiongauge.multiecu

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class MultiEcuHold {
    private val _ids = MutableStateFlow<List<String>?>(null)
    val ids: StateFlow<List<String>?> = _ids.asStateFlow()

    fun set(value: List<String>?) {
        _ids.value = value
    }
}
