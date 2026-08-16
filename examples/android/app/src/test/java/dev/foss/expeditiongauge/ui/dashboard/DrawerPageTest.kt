package dev.foss.expeditiongauge.ui.dashboard

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class DrawerPageTest {
    @Test
    fun backFromRootClosesDrawer() {
        assertEquals(DrawerBackTarget.Close, drawerBackTarget(DrawerPage.Root))
    }

    @Test
    fun backFromSubpageReturnsToRoot() {
        assertEquals(DrawerBackTarget.Root, drawerBackTarget(DrawerPage.Preset))
        assertEquals(DrawerBackTarget.Root, drawerBackTarget(DrawerPage.Library))
    }

    @Test
    fun closeResetsToRoot() {
        assertEquals(DrawerPage.Root, drawerPageAfterClosed())
    }

    @Test
    fun presetVisibilityFollowsFlag() {
        assertTrue(drawerShowsPreset(true))
        assertFalse(drawerShowsPreset(false))
    }

    @Test
    fun liveRequiresPrefAndFeature() {
        assertTrue(drawerShowsLive(true, true))
        assertFalse(drawerShowsLive(true, false))
        assertFalse(drawerShowsLive(false, true))
    }

    @Test
    fun alwaysVisibleRootStaysAtMostSix() {
        assertEquals(4, drawerAlwaysVisibleCount(presetsEnabled = false, liveEnabled = false))
        assertEquals(6, drawerAlwaysVisibleCount(presetsEnabled = true, liveEnabled = true))
    }
}
