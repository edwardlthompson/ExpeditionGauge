package dev.foss.expeditiongauge.car.aainclineaudio

import dev.foss.expeditiongauge.alerts.AlertType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AaInclinometerAudioTest {
    @Test
    fun routesPitchAndRollOnlyWhileAaSessionLive() {
        assertTrue(AaInclinometerAudio.useNavRoute(true, AlertType.PITCH))
        assertTrue(AaInclinometerAudio.useNavRoute(true, AlertType.ROLL))
        assertFalse(AaInclinometerAudio.useNavRoute(true, AlertType.SPEED))
        assertFalse(AaInclinometerAudio.useNavRoute(false, AlertType.PITCH))
        assertEquals(2400, AaInclinometerAudio.sampleCount(16_000, 150))
    }
}
