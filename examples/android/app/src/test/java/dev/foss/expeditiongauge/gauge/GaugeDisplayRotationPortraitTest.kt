package dev.foss.expeditiongauge.gauge

import org.junit.Assert.assertNotEquals
import org.junit.Test

class GaugeDisplayRotationPortraitTest {
    @Test
    fun mapAttitude_portraitDiffersFromLandscape() {
        val landscape = GaugeDisplayRotation.mapAttitude(10f, 5f, displayRotation = 0, isPortraitLayout = false)
        val portrait = GaugeDisplayRotation.mapAttitude(10f, 5f, displayRotation = 0, isPortraitLayout = true)
        assertNotEquals(landscape.normalizedX, portrait.normalizedX, 0.001f)
    }
}
