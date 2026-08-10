package dev.foss.expeditiongauge.sensors

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.hardware.display.DisplayManager
import android.view.Display
import dev.foss.expeditiongauge.ble.BleImuManager
import dev.foss.expeditiongauge.calibration.AutocalibrationController
import dev.foss.expeditiongauge.calibration.CalibrationStore
import dev.foss.expeditiongauge.drift.DriftAngleEstimator
import dev.foss.expeditiongauge.fusion.SensorFusionEngine
import dev.foss.expeditiongauge.telemetry.TelemetryBus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.launch

class PhoneSensorProvider(
    private val context: Context,
    private val fusionEngine: SensorFusionEngine,
    private val driftEstimator: DriftAngleEstimator,
    private val telemetryBus: TelemetryBus,
    private val calibrationStore: CalibrationStore,
    private val scope: CoroutineScope,
    private val bleImuManager: BleImuManager? = null,
    private val autocalibrationController: AutocalibrationController? = null,
) : SensorEventListener {
    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val displayManager = context.getSystemService(Context.DISPLAY_SERVICE) as DisplayManager
    private val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    private val linearAcceleration = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION)
    private val gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)
    private val magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)
    private val publisher = PhoneImuTelemetryPublisher(
        fusionEngine, driftEstimator, telemetryBus, bleImuManager,
    )

    private val displayListener = object : DisplayManager.DisplayListener {
        override fun onDisplayAdded(displayId: Int) = Unit
        override fun onDisplayRemoved(displayId: Int) = Unit
        override fun onDisplayChanged(displayId: Int) {
            if (displayId == Display.DEFAULT_DISPLAY) syncDisplayRotation()
        }
    }

    fun start() {
        scope.launch {
            // Apply persisted zero before IMU listeners so cold start matches last calibrate.
            val initial = calibrationStore.currentOffsets()
            fusionEngine.setCalibrationOffsets(initial)
            if (initial.hasPersistedZero()) {
                autocalibrationController?.restorePersistedZeroCooldown()
            }
            syncDisplayRotation()
            displayManager.registerDisplayListener(displayListener, null)
            val imuDelay = SensorPollScheduler.phoneImuSensorDelay
            accelerometer?.let { sensorManager.registerListener(this@PhoneSensorProvider, it, imuDelay) }
            linearAcceleration?.let { sensorManager.registerListener(this@PhoneSensorProvider, it, imuDelay) }
            gyroscope?.let { sensorManager.registerListener(this@PhoneSensorProvider, it, imuDelay) }
            magnetometer?.let { sensorManager.registerListener(this@PhoneSensorProvider, it, imuDelay) }
            calibrationStore.offsets.drop(1).collect { offsets ->
                fusionEngine.setCalibrationOffsets(offsets)
                publisher.resetSessionPeaks()
            }
        }
    }

    fun stop() {
        sensorManager.unregisterListener(this)
        runCatching { displayManager.unregisterDisplayListener(displayListener) }
    }

    /** Activity [android.view.Display.getRotation] is authoritative when the HUD is visible. */
    fun updateDisplayRotation(rotation: Int) = fusionEngine.setDisplayRotation(rotation)

    fun resetSessionPeaks() = publisher.resetSessionPeaks()

    override fun onSensorChanged(event: SensorEvent) {
        when (event.sensor.type) {
            Sensor.TYPE_ACCELEROMETER -> {
                fusionEngine.onAccelerometer(
                    event.values[0], event.values[1], event.values[2], event.timestamp,
                )
                autocalibrationController?.onAccel(
                    event.values[0], event.values[1], event.values[2],
                )
            }
            Sensor.TYPE_LINEAR_ACCELERATION -> {
                fusionEngine.onLinearAccelerometer(
                    event.values[0], event.values[1], event.values[2],
                )
            }
            Sensor.TYPE_MAGNETIC_FIELD -> {
                fusionEngine.onMagnetometer(
                    event.values[0], event.values[1], event.values[2],
                )
                autocalibrationController?.onMag(
                    event.values[0], event.values[1], event.values[2],
                )
            }
            Sensor.TYPE_GYROSCOPE -> {
                fusionEngine.onGyroscope(
                    event.values[0], event.values[1], event.values[2], event.timestamp,
                )
                autocalibrationController?.onGyro(
                    event.values[0], event.values[1], event.values[2],
                )
                publisher.publish()
                val fusion = fusionEngine.currentOutput()
                autocalibrationController?.onFusionTick(
                    nowMs = System.currentTimeMillis(),
                    displayPitchDeg = fusion.pitchDeg,
                    displayRollDeg = fusion.rollDeg,
                    displayYawDeg = fusion.yawDeg,
                )
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) = Unit

    private fun syncDisplayRotation() {
        fusionEngine.setDisplayRotation(DisplayRotationReader.current(context))
    }
}
