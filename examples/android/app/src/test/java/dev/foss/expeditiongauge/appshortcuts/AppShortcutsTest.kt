package dev.foss.expeditiongauge.appshortcuts

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class AppShortcutsTest {
    @Test
    fun remembersLibraryAndRecord() {
        AppShortcuts.pending = null
        AppShortcuts.remember(AppShortcuts.ACTION_LIBRARY)
        assertEquals("sessions", AppShortcuts.targetScreen(AppShortcuts.consume()))
        AppShortcuts.remember(AppShortcuts.ACTION_RECORD)
        assertEquals("dashboard", AppShortcuts.targetScreen(AppShortcuts.consume()))
        assertNull(AppShortcuts.consume())
        assertEquals(listOf("record", "library"), AppShortcuts.ids())
    }
}
