package dev.foss.expeditiongauge.vinlast6

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class VinLast6Hold {
    private val _last6 = MutableStateFlow<String?>(null)
    val last6: StateFlow<String?> = _last6.asStateFlow()

    fun set(value: String?) {
        _last6.value = value
    }
}
