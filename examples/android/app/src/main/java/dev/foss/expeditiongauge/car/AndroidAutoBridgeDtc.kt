package dev.foss.expeditiongauge.car

import android.os.SystemClock
import dev.foss.expeditiongauge.obd.dtc.DtcCarousel
import dev.foss.expeditiongauge.obd.dtc.DtcEntry
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

/** Collects OBD stored DTCs and invalidates AA HUD on 5 s carousel boundaries. */
internal class AndroidAutoBridgeDtc(
    private val scope: CoroutineScope,
    private val storedDtcsFlow: StateFlow<List<DtcEntry>>,
    private val onDtcs: (List<DtcEntry>) -> Unit,
    private val invalidateForce: () -> Unit,
) {
    private var carouselJob: Job? = null

    fun startCollect() {
        scope.launch {
            storedDtcsFlow.collect { list ->
                onDtcs(list)
                invalidateForce()
                carouselJob?.cancel()
                if (list.isNotEmpty()) {
                    carouselJob = scope.launch {
                        while (isActive) {
                            val now = SystemClock.elapsedRealtime()
                            val wait = DtcCarousel.DWELL_MS - (now % DtcCarousel.DWELL_MS)
                            delay(wait.coerceAtLeast(50L))
                            invalidateForce()
                        }
                    }
                }
            }
        }
    }
}
