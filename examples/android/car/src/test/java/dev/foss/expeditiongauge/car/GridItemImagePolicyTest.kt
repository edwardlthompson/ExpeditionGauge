package dev.foss.expeditiongauge.car

import org.junit.Assert.assertTrue
import org.junit.Test

class GridItemImagePolicyTest {
    @Test
    fun nullNeedsFallback() {
        assertTrue(GridItemImagePolicy.needsFallback(null))
    }
}
