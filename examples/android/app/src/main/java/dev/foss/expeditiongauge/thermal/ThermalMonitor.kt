package dev.foss.expeditiongauge.thermal

import android.content.Context
import android.os.Build
import android.os.PowerManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class ThermalStatus {
    Normal,
    Warning,
    Critical,
}

class ThermalMonitor(context: Context) {
    private val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
    private val _status = MutableStateFlow(ThermalStatus.Normal)
    val status: StateFlow<ThermalStatus> = _status.asStateFlow()

    fun refresh() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            _status.value = ThermalStatus.Normal
            return
        }
        _status.value = when (powerManager.currentThermalStatus) {
            PowerManager.THERMAL_STATUS_SEVERE,
            PowerManager.THERMAL_STATUS_CRITICAL,
            PowerManager.THERMAL_STATUS_EMERGENCY,
            PowerManager.THERMAL_STATUS_SHUTDOWN,
            -> ThermalStatus.Critical
            PowerManager.THERMAL_STATUS_MODERATE,
            PowerManager.THERMAL_STATUS_LIGHT,
            -> ThermalStatus.Warning
            else -> ThermalStatus.Normal
        }
    }
}
