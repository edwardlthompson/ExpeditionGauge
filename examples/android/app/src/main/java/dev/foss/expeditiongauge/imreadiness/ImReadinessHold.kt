package dev.foss.expeditiongauge.imreadiness

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ImReadinessHold {
    private val _report = MutableStateFlow<ImReadinessReport?>(null)
    val report: StateFlow<ImReadinessReport?> = _report.asStateFlow()

    fun set(value: ImReadinessReport?) {
        _report.value = value
    }
}
