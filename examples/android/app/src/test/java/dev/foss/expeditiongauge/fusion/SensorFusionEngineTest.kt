package dev.foss.expeditiongauge.fusion

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import dev.foss.expeditiongauge.calibration.CalibrationOffsets
import dev.foss.expeditiongauge.calibration.CalibrationStore
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [26])
class SensorFusionEngineTest {
    private val context: Context = ApplicationProvider.getApplicationContext()

    @Test
    fun appliesCalibrationOffsets() {
        val calibrationStore = CalibrationStore(context)
        val engine = SensorFusionEngine(calibrationStore)
        engine.setCalibrationOffsets(CalibrationOffsets(pitchOffsetDeg = 5f, rollOffsetDeg = -3f))
        repeat(20) {
            engine.onAccelerometer(0f, 0f, 9.81f, it * 20_000_000L)
            engine.onGyroscope(0f, 0f, 0f, it * 20_000_000L)
        }
        val output = engine.currentOutput()
        assertEquals(-5f, output.pitchDeg, 6f)
        assertEquals(3f, output.rollDeg, 6f)
    }
}
