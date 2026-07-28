package dev.foss.expeditiongauge.obd

import android.util.Log
import dev.foss.expeditiongauge.obd.dtc.DtcCatalog
import dev.foss.expeditiongauge.obd.dtc.DtcEntry
import kotlinx.coroutines.flow.MutableStateFlow

/** Debug / DHU helpers for injecting stored DTCs without ELM. */
internal object ObdDtcSim {
    private const val TAG = "ExpeditionGauge/Obd"

    fun apply(
        codes: List<String>,
        catalog: DtcCatalog,
        target: MutableStateFlow<List<DtcEntry>>,
    ) {
        target.value = codes.map { code ->
            val normalized = code.trim().uppercase()
            DtcEntry(code = normalized, description = catalog.describe(normalized))
        }
        Log.i(TAG, "Simulated DTCs: ${codes.size}")
    }

    fun clear(target: MutableStateFlow<List<DtcEntry>>) {
        target.value = emptyList()
    }
}
