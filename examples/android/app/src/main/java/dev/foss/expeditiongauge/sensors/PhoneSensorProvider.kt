package dev.foss.expeditiongauge.sensors

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import dev.foss.expeditiongauge.fusion.SensorFusionEngine
import dev.foss.expeditiongauge.telemetry.TelemetryBus
import dev.foss.expeditiongauge.telemetry.TelemetrySnapshot
import dev.foss.expeditiongauge.drift.DriftAngleEstimator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import dev.foss.expeditiongauge.calibration.CalibrationStore
import kotlin.math.abs
import kotlin.math.max

class PhoneSensorProvider(
    context: Context,
    private val fusionEngine: SensorFusionEngine,
    private val driftEstimator: DriftAngleEstimator,
    private val telemetryBus: TelemetryBus,
    private val calibrationStore: CalibrationStore,
    private val scope: CoroutineScope,
    private val bleImuManager: dev.foss.expeditiongauge.ble.BleImuManager? = null,
) : SensorEventListener {
    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    private val gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)
    private var peakAbsPitch = 0f
    private var peakAbsRoll = 0f
    private var lastGpsSnapshot = TelemetrySnapshot.empty()

    fun start() {
        scope.launch {
            calibrationStore.offsets.collect { offsets ->
                fusionEngine.setCalibrationOffsets(offsets)
            }
        }
        accelerometer?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_GAME)
        }
        gyroscope?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_GAME)
        }
    }

    fun stop() {
        sensorManager.unregisterListener(this)
    }

    fun updateGpsContext(snapshot: TelemetrySnapshot) {
        lastGpsSnapshot = snapshot
    }

    override fun onSensorChanged(event: SensorEvent) {
        when (event.sensor.type) {
            Sensor.TYPE_ACCELEROMETER -> fusionEngine.onAccelerometer(
                event.values[0],
                event.values[1],
                event.values[2],
                event.timestamp,
            )
            Sensor.TYPE_GYROSCOPE -> {
                fusionEngine.onGyroscope(
                    event.values[0],
                    event.values[1],
                    event.values[2],
                    event.timestamp,
                )
                publishTelemetry()
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) = Unit

    private fun publishTelemetry() {
        val fusion = fusionEngine.currentOutput()
        val now = System.currentTimeMillis()
        val multiImu = bleImuManager?.fuseWithPhone(fusion.yawDeg)
        val yaw = multiImu?.bodyYawDeg ?: fusion.yawDeg
        val pitch = multiImu?.pitchDeg?.takeIf { (multiImu.activeCount) > 0 } ?: fusion.pitchDeg
        val roll = multiImu?.rollDeg?.takeIf { (multiImu.activeCount) > 0 } ?: fusion.rollDeg
        val latG = multiImu?.latG?.takeIf { (multiImu.activeCount) > 0 } ?: fusion.latG
        val lonG = multiImu?.lonG?.takeIf { (multiImu.activeCount) > 0 } ?: fusion.lonG
        val yawRate = multiImu?.yawRateDegPerSec ?: 0f
        driftEstimator.onFusionSample(yaw, yawRate, now)
        peakAbsPitch = max(peakAbsPitch, abs(pitch))
        peakAbsRoll = max(peakAbsRoll, abs(roll))
        val drift = driftEstimator.currentSample().copy(
            source = multiImu?.source ?: "phone",
        )
        telemetryBus.publish(
            lastGpsSnapshot.copy(
                timestampMs = now,
                pitchDeg = pitch,
                rollDeg = roll,
                headingDeg = yaw,
                latG = latG,
                lonG = lonG,
                driftAngleDeg = drift.driftAngleDeg,
                bodyYawDeg = drift.bodyYawDeg,
                velocityHeadingDeg = drift.velocityHeadingDeg,
                fusionSource = multiImu?.source ?: "phone",
                chassisTwistDeg = multiImu?.chassisTwistDeg,
                peakAbsPitchDeg = peakAbsPitch,
                peakAbsRollDeg = peakAbsRoll,
            ),
        )
    }
}
