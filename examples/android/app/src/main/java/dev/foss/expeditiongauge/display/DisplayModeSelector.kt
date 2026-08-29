package dev.foss.expeditiongauge.display

data class DisplayModeChoice(
    val modeId: Int,
    val widthPx: Int,
    val heightPx: Int,
    val refreshHz: Float,
)

object DisplayModeSelector {
    fun fastestSameResolution(
        modes: List<DisplayModeChoice>,
        current: DisplayModeChoice,
    ): DisplayModeChoice? {
        val sameSize = modes.filter { it.widthPx == current.widthPx && it.heightPx == current.heightPx }
        return sameSize.maxByOrNull { it.refreshHz }
    }
}
