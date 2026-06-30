package dev.foss.expeditiongauge.fusion

import dev.foss.expeditiongauge.calibration.CalibrationOffsets
import dev.foss.expeditiongauge.calibration.CalibrationStore

data class FusionOutput(
    val pitchDeg: Float,
    val rollDeg: Float,
    val yawDeg: Float,
    val latG: Float,
    val lonG: Float,
)

class SensorFusionEngine(
    private val calibrationStore: CalibrationStore,
    private val madgwick: MadgwickFilter = MadgwickFilter(),
    private val complementary: ComplementaryFilter = ComplementaryFilter(),
    private val useMadgwick: Boolean = true,
) {
    private var lastTimestampNs: Long = 0L
    private var calibrationOffsets = CalibrationOffsets()

    fun setCalibrationOffsets(offsets: CalibrationOffsets) {
        calibrationOffsets = offsets
    }

    fun onAccelerometer(x: Float, y: Float, z: Float, timestampNs: Long) {
        lastAccel = Triple(x, y, z)
        lastTimestampNs = timestampNs
    }

    fun onGyroscope(x: Float, y: Float, z: Float, timestampNs: Long) {
        val dtSec = if (lastTimestampNs > 0L) {
            ((timestampNs - lastTimestampNs).coerceAtLeast(1L)) / 1_000_000_000f
        } else {
            0.02f
        }
        val (ax, ay, az) = lastAccel
        if (useMadgwick) {
            madgwick.update(x, y, z, ax, ay, az)
        } else {
            complementary.update(x, y, z, ax, ay, az, dtSec)
        }
        lastTimestampNs = timestampNs
    }

    fun currentOutput(): FusionOutput {
        val rawPitch = if (useMadgwick) madgwick.pitchDeg() else complementary.pitchDeg()
        val rawRoll = if (useMadgwick) madgwick.rollDeg() else complementary.rollDeg()
        val rawYaw = if (useMadgwick) madgwick.yawDeg() else complementary.yawDeg()
        val (pitch, roll) = calibrationStore.applyOffsets(rawPitch, rawRoll, calibrationOffsets)
        val (ax, ay, az) = lastAccel
        val latG = ay / GRAVITY
        val lonG = ax / GRAVITY
        return FusionOutput(
            pitchDeg = pitch,
            rollDeg = roll,
            yawDeg = rawYaw,
            latG = latG,
            lonG = lonG,
        )
    }

    private var lastAccel: Triple<Float, Float, Float> = Triple(0f, 0f, GRAVITY)

    companion object {
        private const val GRAVITY = 9.81f
    }
}
