package dev.foss.expeditiongauge.fastlanenextchangelog

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class FastlaneNextChangelogTest {
    @Test
    fun nextCodeIsFiftyFour() {
        assertEquals(54, FastlaneNextChangelog.NEXT_CODE)
        assertEquals("54.txt", FastlaneNextChangelog.fileName())
        assertTrue(FastlaneNextChangelog.isNext(54))
    }
}
