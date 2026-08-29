package dev.foss.expeditiongauge.appshortcuts

/** Launcher shortcuts for Record (dashboard) and Library (sessions). */
object AppShortcuts {
    const val ACTION_RECORD = "dev.foss.expeditiongauge.action.RECORD"
    const val ACTION_LIBRARY = "dev.foss.expeditiongauge.action.LIBRARY"

    @Volatile
    var pending: String? = null

    fun ids(): List<String> = listOf("record", "library")

    fun remember(action: String?) {
        if (action == ACTION_RECORD || action == ACTION_LIBRARY) pending = action
    }

    fun consume(): String? {
        val action = pending
        pending = null
        return action
    }

    fun targetScreen(action: String?): String? = when (action) {
        ACTION_LIBRARY -> "sessions"
        ACTION_RECORD -> "dashboard"
        else -> null
    }
}
