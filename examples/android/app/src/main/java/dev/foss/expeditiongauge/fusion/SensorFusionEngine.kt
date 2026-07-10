package dev.foss.expeditiongauge.fusion

import dev.foss.expeditiongauge.calibration.CalibrationOffsets
import dev.foss.expeditiongauge.calibration.CalibrationStore
import dev.foss.expeditiongauge.gauge.SensorAxisRemap
import dev.foss.expeditiongauge.gauge.VehicleAttitudeLogic

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
    @Volatile
    private var displayRotation: Int = 0

    fun setCalibrationOffsets(offsets: CalibrationOffsets) {
        calibrationOffsets = offsets
    }

    fun setDisplayRotation(rotation: Int) {
        val next = rotation.mod(4)
        if (next != displayRotation) {
            displayRotation = next
            // New screen frame — drop stale quaternion so Madgwick re-aligns to remapped gravity.
            madgwick.reset()
            complementary.reset()
            lastTimestampNs = 0L
        }
    }

    fun setMadgwickBeta(beta: Float) {
        madgwick.setBeta(beta)
    }

    fun onAccelerometer(x: Float, y: Float, z: Float, timestampNs: Long) {
        // Keep device-frame for G-meter (still uses displayRotation ball remaps).
        lastAccel = Triple(x, y, z)
        lastTimestampNs = timestampNs
    }

    fun onGyroscope(x: Float, y: Float, z: Float, timestampNs: Long) {
        val dtSec = if (lastTimestampNs > 0L) {
            ((timestampNs - lastTimestampNs).coerceAtLeast(1L)) / 1_000_000_000f
        } else {
            0.02f
        }
        val (ax0, ay0, az0) = lastAccel
        val (ax, ay, az) = SensorAxisRemap.remap(ax0, ay0, az0, displayRotation)
        val (gx, gy, gz) = SensorAxisRemap.remap(x, y, z, displayRotation)
        if (useMadgwick) {
            madgwick.update(gx, gy, gz, ax, ay, az)
        } else {
            complementary.update(gx, gy, gz, ax, ay, az, dtSec)
        }
        lastTimestampNs = timestampNs
    }

    fun currentOutput(): FusionOutput {
        // Madgwick already runs in screen-stable (portrait-equivalent) axes.
        val rawPitch = if (useMadgwick) madgwick.pitchDeg() else complementary.pitchDeg()
        val rawRoll = if (useMadgwick) madgwick.rollDeg() else complementary.rollDeg()
        val rawYaw = if (useMadgwick) madgwick.yawDeg() else complementary.yawDeg()
        val (vehiclePitch, vehicleRoll) = VehicleAttitudeLogic.fromDevice(
            rawPitch, rawRoll, displayRotation = 0,
        )
        val (pitch, roll) = calibrationStore.applyOffsets(
            vehiclePitch, vehicleRoll, calibrationOffsets,
        )
        val (ax, ay, az) = lastAccel
        return FusionOutput(
            pitchDeg = pitch,
            rollDeg = roll,
            yawDeg = rawYaw,
            latG = ay / GRAVITY,
            lonG = ax / GRAVITY,
        )
    }

    fun currentDisplayRotation(): Int = displayRotation

    /** Screen-stable Madgwick/complementary before portrait swap (debug / logs). */
    fun currentRawAttitude(): Pair<Float, Float> {
        val rawPitch = if (useMadgwick) madgwick.pitchDeg() else complementary.pitchDeg()
        val rawRoll = if (useMadgwick) madgwick.rollDeg() else complementary.rollDeg()
        return rawPitch to rawRoll
    }

    private var lastAccel: Triple<Float, Float, Float> = Triple(0f, 0f, GRAVITY)

    companion object {
        private const val GRAVITY = 9.81f
    }
}
