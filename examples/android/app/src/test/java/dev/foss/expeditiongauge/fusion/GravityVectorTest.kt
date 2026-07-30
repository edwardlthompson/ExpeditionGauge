package dev.foss.expeditiongauge.fusion

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import dev.foss.expeditiongauge.calibration.CalibrationStore
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import kotlin.math.abs

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [26])
class GravityVectorTest {
    @Test
    fun identityPitchRoll_gravityIsPlusZ() {
        val (gx, gy, gz) = GravityVector.fromPitchRollDeg(0f, 0f)
        assertEquals(0f, gx, 0.001f)
        assertEquals(0f, gy, 0.001f)
        assertEquals(1f, gz, 0.001f)
    }

    @Test
    fun uprightPitch_removesGravityFromLatG() {
        // 90° pitch: gravity along −X in Madgwick convention → lat/lon linear ≈ 0 for matching accel
        val grav = GravityVector.fromPitchRollDeg(90f, 0f)
        val (lat, lon) = GravityVector.linearLatLonG(
            axMs2 = grav.first * GravityVector.GRAVITY_MS2,
            ayMs2 = grav.second * GravityVector.GRAVITY_MS2,
            gravUnit = grav,
        )
        assertEquals(0f, lat, 0.02f)
        assertEquals(0f, lon, 0.02f)
    }

    @Test
    fun pureLateralAccel_preservedAfterRemoval() {
        val grav = GravityVector.fromPitchRollDeg(0f, 0f) // (0,0,1)
        val (lat, lon) = GravityVector.linearLatLonG(
            axMs2 = 0f,
            ayMs2 = 2f, // ~0.2 g lateral
            gravUnit = grav,
        )
        assertEquals(2f / GravityVector.GRAVITY_MS2, lat, 0.001f)
        assertEquals(0f, lon, 0.001f)
    }

    @Test
    fun witMotionGUnits_subtractPitchRollGravity() {
        // Flat: ax=0 ay=0 az=1g implied; pitch/roll 0 → linear 0
        val (lat, lon) = GravityVector.linearLatLonFromG(0f, 0f, 0f, 0f)
        assertEquals(0f, lat, 0.001f)
        assertEquals(0f, lon, 0.001f)
    }
}

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [26])
class SensorFusionLinearGTest {
    private val context: Context = ApplicationProvider.getApplicationContext()

    @Test
    fun stationaryFlat_latLonNearZero() {
        val engine = SensorFusionEngine(CalibrationStore(context))
        repeat(80) { i ->
            val t = i * 20_000_000L
            engine.onAccelerometer(0f, 0f, 9.81f, t)
            engine.onGyroscope(0f, 0f, 0f, t)
        }
        val out = engine.currentOutput()
        assertTrue("latG=${out.latG}", abs(out.latG) < 0.2f)
        assertTrue("lonG=${out.lonG}", abs(out.lonG) < 0.2f)
    }

    @Test
    fun linearAccelSensor_zeroAtRest() {
        val engine = SensorFusionEngine(CalibrationStore(context))
        engine.onAccelerometer(0f, 9.81f, 0f, 0L)
        engine.onLinearAccelerometer(0f, 0f, 0f)
        val out = engine.currentOutput()
        assertEquals(0f, out.latG, 0.001f)
        assertEquals(0f, out.lonG, 0.001f)
    }

    @Test
    fun linearAccelSensor_preservesLateral() {
        val engine = SensorFusionEngine(CalibrationStore(context))
        engine.onAccelerometer(0f, 9.81f, 0f, 0L)
        engine.onLinearAccelerometer(0f, 2.0f, 0f) // ~0.204 g lateral
        val out = engine.currentOutput()
        assertEquals(2.0f / GravityVector.GRAVITY_MS2, out.latG, 0.001f)
        assertEquals(0f, out.lonG, 0.001f)
    }

    @Test
    fun madgwickFallback_flatNearZero() {
        val engine = SensorFusionEngine(CalibrationStore(context))
        engine.setMadgwickBeta(0.3f)
        repeat(200) { i ->
            val t = i * 20_000_000L
            engine.onAccelerometer(0f, 0f, 9.81f, t)
            engine.onGyroscope(0f, 0f, 0f, t)
        }
        val out = engine.currentOutput()
        assertTrue("latG=${out.latG}", abs(out.latG) < 0.15f)
        assertTrue("lonG=${out.lonG}", abs(out.lonG) < 0.15f)
    }
}
