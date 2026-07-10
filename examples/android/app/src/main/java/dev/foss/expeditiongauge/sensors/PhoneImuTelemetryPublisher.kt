package dev.foss.expeditiongauge.sensors

import dev.foss.expeditiongauge.ble.BleImuManager
import dev.foss.expeditiongauge.ble.ImuFusionLog
import dev.foss.expeditiongauge.drift.DriftAngleEstimator
import dev.foss.expeditiongauge.fusion.MultiImuFusionOutput
import dev.foss.expeditiongauge.fusion.SensorFusionEngine
import dev.foss.expeditiongauge.gps.GpsCourseLogic
import dev.foss.expeditiongauge.telemetry.TelemetryBus
import dev.foss.expeditiongauge.telemetry.TelemetrySnapshot
import kotlin.math.abs

/** Publishes fused phone (+ optional BLE) IMU into [TelemetryBus]. */
internal class PhoneImuTelemetryPublisher(
    private val fusionEngine: SensorFusionEngine,
    private val driftEstimator: DriftAngleEstimator,
    private val telemetryBus: TelemetryBus,
    private val bleImuManager: BleImuManager?,
) {
    private var peakPitch = 0f
    private var peakRoll = 0f
    private var lastGpsSnapshot = TelemetrySnapshot.empty()
    private var logThrottleNs = 0L

    fun updateGpsContext(snapshot: TelemetrySnapshot) {
        lastGpsSnapshot = snapshot
    }

    fun resetSessionPeaks() {
        peakPitch = 0f
        peakRoll = 0f
    }

    fun publish() {
        val fusion = fusionEngine.currentOutput()
        val now = System.currentTimeMillis()
        val multiImu = bleImuManager?.fuseWithPhone(fusion.yawDeg)
        val yaw = multiImu?.bodyYawDeg ?: fusion.yawDeg
        val pitch = multiImu?.pitchDeg?.takeIf { multiImu.activeCount > 0 } ?: fusion.pitchDeg
        val roll = multiImu?.rollDeg?.takeIf { multiImu.activeCount > 0 } ?: fusion.rollDeg
        val latG = multiImu?.latG?.takeIf { multiImu.activeCount > 0 } ?: fusion.latG
        val lonG = multiImu?.lonG?.takeIf { multiImu.activeCount > 0 } ?: fusion.lonG
        driftEstimator.onFusionSample(yaw, multiImu?.yawRateDegPerSec ?: 0f, now)
        if (abs(pitch) > abs(peakPitch)) peakPitch = pitch
        if (abs(roll) > abs(peakRoll)) peakRoll = roll
        val drift = driftEstimator.currentSample().copy(source = multiImu?.source ?: "phone")
        val fusionSource = multiImu?.source ?: "phone"
        maybeLog(fusionSource, multiImu, drift.driftAngleDeg, latG, pitch, roll)
        val gpsCourse = lastGpsSnapshot.velocityHeadingDeg
            ?: lastGpsSnapshot.headingDeg.takeIf { lastGpsSnapshot.gpsFix }
        telemetryBus.publish(
            lastGpsSnapshot.copy(
                timestampMs = now,
                pitchDeg = pitch,
                rollDeg = roll,
                headingDeg = GpsCourseLogic.displayHeadingDeg(
                    yaw, gpsCourse, lastGpsSnapshot.speedMps,
                ),
                latG = latG,
                lonG = lonG,
                driftAngleDeg = drift.driftAngleDeg,
                bodyYawDeg = yaw,
                velocityHeadingDeg = lastGpsSnapshot.velocityHeadingDeg
                    ?: drift.velocityHeadingDeg.takeIf { lastGpsSnapshot.gpsFix },
                fusionSource = fusionSource,
                chassisTwistDeg = multiImu?.chassisTwistDeg,
                peakAbsPitchDeg = abs(peakPitch),
                peakAbsRollDeg = abs(peakRoll),
                peakPitchDeg = peakPitch,
                peakRollDeg = peakRoll,
            ),
        )
    }

    private fun maybeLog(
        fusionSource: String,
        multiImu: MultiImuFusionOutput?,
        driftAngleDeg: Float?,
        latG: Float,
        pitch: Float,
        roll: Float,
    ) {
        val nowNs = System.nanoTime()
        if (nowNs - logThrottleNs <= 500_000_000L) return
        logThrottleNs = nowNs
        val (rawPitch, rawRoll) = fusionEngine.currentRawAttitude()
        ImuFusionLog.publish(
            fusionSource = fusionSource,
            activeCount = multiImu?.activeCount ?: 0,
            chassisTwistDeg = multiImu?.chassisTwistDeg,
            driftAngleDeg = driftAngleDeg,
            latG = latG,
            pitchDeg = pitch,
            rollDeg = roll,
            displayRotation = fusionEngine.currentDisplayRotation(),
            rawPitchDeg = rawPitch,
            rawRollDeg = rawRoll,
        )
    }
}
