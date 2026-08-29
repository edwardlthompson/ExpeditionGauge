package dev.foss.expeditiongauge.obdtrip

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ObdTripHold {
    private val _trip = MutableStateFlow<ObdTripSinceClear?>(null)
    val trip: StateFlow<ObdTripSinceClear?> = _trip.asStateFlow()

    fun set(value: ObdTripSinceClear?) {
        _trip.value = value
    }
}
