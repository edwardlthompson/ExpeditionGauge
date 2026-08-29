package dev.foss.expeditiongauge.fordmode22

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class FordMode22TempsHold {
    private val _temps = MutableStateFlow<FordMode22Temps?>(null)
    val temps: StateFlow<FordMode22Temps?> = _temps.asStateFlow()

    fun set(value: FordMode22Temps?) {
        _temps.value = value
    }
}
