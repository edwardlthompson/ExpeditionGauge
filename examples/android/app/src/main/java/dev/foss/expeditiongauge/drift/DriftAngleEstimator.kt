package dev.foss.expeditiongauge.drift

data class DriftSample(
    val driftAngleDeg: Float,
    val bodyYawDeg: Float,
    val velocityHeadingDeg: Float,
    val yawRateDegPerSec: Float,
    val confidence: Float,
    val source: String = "phone",
)

class DriftAngleEstimator(
    private val sideslipEkf: SideslipEkf = SideslipEkf(),
) {
    private var lastTimestampMs: Long = 0L

    fun onFusionSample(yawDeg: Float, yawRateDegPerSec: Float, timestampMs: Long) {
        val dtSec = if (lastTimestampMs > 0L) {
            ((timestampMs - lastTimestampMs).coerceAtLeast(1L)) / 1000f
        } else {
            0.02f
        }
        sideslipEkf.predict(dtSec, yawRateDegPerSec)
        sideslipEkf.updateBodyYaw(yawDeg)
        lastTimestampMs = timestampMs
    }

    fun onGpsSample(velocityHeadingDeg: Float, speedMps: Float) {
        sideslipEkf.updateVelocityHeading(velocityHeadingDeg, speedMps)
    }

    fun currentSample(): DriftSample {
        val speedFactor = if (kotlin.math.abs(sideslipEkf.betaDeg) > 0.01f) 0.8f else 0.3f
        return DriftSample(
            driftAngleDeg = sideslipEkf.currentBeta(),
            bodyYawDeg = sideslipEkf.yawDeg,
            velocityHeadingDeg = sideslipEkf.velocityHeadingDeg,
            yawRateDegPerSec = sideslipEkf.yawRateDegPerSec,
            confidence = speedFactor,
            source = "phone",
        )
    }
}
