package dev.foss.expeditiongauge.fastlanenextchangelog

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class FastlaneNextChangelogTest {
    @Test
    fun nextCodeIsFiftyThree() {
        assertEquals(53, FastlaneNextChangelog.NEXT_CODE)
        assertEquals("53.txt", FastlaneNextChangelog.fileName())
        assertTrue(FastlaneNextChangelog.isNext(53))
    }
}
