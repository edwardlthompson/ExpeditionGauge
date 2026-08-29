package dev.foss.expeditiongauge.feedback

import android.content.Context
import dev.foss.expeditiongauge.crash.CrashLogStore
import dev.foss.expeditiongauge.crash.PendingCrashStore

class FeedbackPrefs(private val context: Context) {
    fun saveCrashes(): Boolean =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getBoolean(KEY, false)

    fun setSaveCrashes(on: Boolean) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().putBoolean(KEY, on).apply()
        if (!on) {
            PendingCrashStore(context).clear()
            CrashLogStore.fromContext(context).clear()
        }
    }

    companion object {
        const val PREFS = "eg_feedback"
        const val KEY = "save_crashes"
    }
}
