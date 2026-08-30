package dev.foss.expeditiongauge.ui.dashboard

/** Phone HUD rail actions that match Android Auto Nav chrome, plus menu. */
object PhoneHudChrome {
    fun actions(recording: Boolean, markEnabled: Boolean): List<String> = buildList {
        add("menu")
        add("mute")
        add("record")
        add("screenshot")
        add("level")
        if (recording && markEnabled) add("mark")
    }
}
