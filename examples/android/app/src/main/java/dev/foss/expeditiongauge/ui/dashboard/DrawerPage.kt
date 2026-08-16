package dev.foss.expeditiongauge.ui.dashboard

enum class DrawerPage {
    Root,
    Preset,
    Library,
}

enum class DrawerBackTarget {
    Root,
    Close,
}

fun drawerBackTarget(page: DrawerPage): DrawerBackTarget =
    if (page == DrawerPage.Root) DrawerBackTarget.Close else DrawerBackTarget.Root

fun drawerPageAfterClosed(): DrawerPage = DrawerPage.Root

fun drawerShowsPreset(presetsEnabled: Boolean): Boolean = presetsEnabled

fun drawerShowsLive(livePrefEnabled: Boolean, liveFeatureEnabled: Boolean): Boolean =
    livePrefEnabled && liveFeatureEnabled

/** Always-visible root rows: Record, Library, IMU, Settings, plus optional Preset and Live. */
fun drawerAlwaysVisibleCount(presetsEnabled: Boolean, liveEnabled: Boolean): Int {
    var count = 4
    if (presetsEnabled) count++
    if (liveEnabled) count++
    return count
}
