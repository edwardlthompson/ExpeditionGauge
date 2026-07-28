package dev.foss.expeditiongauge.debug

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import dev.foss.expeditiongauge.ExpeditionGaugeApplication

/**
 * Debug-only: inject stored DTCs for AA ROW footer preview (no ELM required).
 *
 * ```
 * adb shell am broadcast -a dev.foss.expeditiongauge.action.SIMULATE_DTC \
 *   -n dev.foss.expeditiongauge/.debug.SimDtcReceiver \
 *   --es codes "P0420,P0300"
 * ```
 */
class SimDtcReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        if (intent?.action != ACTION) return
        val app = context.applicationContext as? ExpeditionGaugeApplication ?: return
        val raw = intent.getStringExtra(EXTRA_CODES)?.trim().orEmpty()
        val codes = if (raw.isEmpty()) DEFAULT_CODES else {
            raw.split(',', ';', ' ')
                .map { it.trim().uppercase() }
                .filter { it.matches(CODE_RE) }
        }
        if (codes.isEmpty()) {
            app.services.obdManager.clearSimulatedDtcs()
            Log.i(TAG, "Cleared simulated DTCs")
            return
        }
        app.services.obdManager.simulateStoredDtcs(codes)
        Log.i(TAG, "Simulated DTCs: $codes")
    }

    companion object {
        const val ACTION = "dev.foss.expeditiongauge.action.SIMULATE_DTC"
        const val EXTRA_CODES = "codes"
        private const val TAG = "ExpeditionGauge/SimDtc"
        private val CODE_RE = Regex("^[PCBU][0-9A-F]{4}$")
        private val DEFAULT_CODES = listOf("P0420", "P0300")
    }
}
