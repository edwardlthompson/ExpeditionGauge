package dev.foss.expeditiongauge.lastsessionwidget

import android.content.Context

class LastSessionStore(context: Context) {
    private val prefs = context.applicationContext.getSharedPreferences(PREFS, Context.MODE_PRIVATE)

    fun save(name: String, startMs: Long) {
        prefs.edit().putString(KEY_NAME, name).putLong(KEY_START, startMs).apply()
    }

    fun name(): String? = prefs.getString(KEY_NAME, null)

    fun startMs(): Long = prefs.getLong(KEY_START, 0L)

    companion object {
        private const val PREFS = "last_session_widget"
        private const val KEY_NAME = "name"
        private const val KEY_START = "start_ms"
    }
}
