package dev.foss.expeditiongauge.obd

import dev.foss.expeditiongauge.dtcclear.DtcClearLatch
import dev.foss.expeditiongauge.fordmode22.FordMode22TempsHold
import dev.foss.expeditiongauge.imreadiness.ImReadinessHold
import dev.foss.expeditiongauge.obd.dtc.DtcCatalog
import dev.foss.expeditiongauge.obd.dtc.DtcEntry
import dev.foss.expeditiongauge.obdtrip.ObdTripHold
import dev.foss.expeditiongauge.piddiscovery.PidDiscoveryHold
import dev.foss.expeditiongauge.vinlast6.VinLast6Hold
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

internal class ObdHudState {
    private val _dtcs = MutableStateFlow<List<DtcEntry>>(emptyList())
    val dtcs: StateFlow<List<DtcEntry>> = _dtcs.asStateFlow()
    val clear = DtcClearLatch()
    val im = ImReadinessHold()
    val trip = ObdTripHold()
    val vin = VinLast6Hold()
    val discovery = PidDiscoveryHold()
    val fordTemps = FordMode22TempsHold()

    fun setDtcs(value: List<DtcEntry>) {
        _dtcs.value = value
    }

    fun simulate(codes: List<String>, catalog: DtcCatalog) =
        ObdDtcSim.apply(codes, catalog, _dtcs)

    fun clearSim() = ObdDtcSim.clear(_dtcs)

    fun reset() {
        _dtcs.value = emptyList()
        im.set(null)
        trip.set(null)
        vin.set(null)
        discovery.set(null)
        fordTemps.set(null)
    }
}
