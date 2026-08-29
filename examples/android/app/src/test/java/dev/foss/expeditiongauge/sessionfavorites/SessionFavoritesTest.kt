package dev.foss.expeditiongauge.sessionfavorites

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SessionFavoritesTest {
    @Test
    fun parseEncodeAndToggle() {
        assertEquals(setOf(1L, 3L), SessionFavorites.parseIds("1, 3"))
        assertEquals("1,3", SessionFavorites.encodeIds(setOf(3L, 1L)))
        assertTrue(2L in SessionFavorites.toggle(emptySet(), 2L))
        assertFalse(2L in SessionFavorites.toggle(setOf(2L), 2L))
    }

    @Test
    fun favoritesFirstKeepsRelativeOrderOfRest() {
        val ordered = SessionFavorites.favoritesFirst(listOf(10L, 20L, 30L), setOf(20L)) { it }
        assertEquals(listOf(20L, 10L, 30L), ordered)
    }
}
