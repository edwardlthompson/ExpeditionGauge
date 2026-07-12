package dev.foss.expeditiongauge.calibration

import kotlin.math.abs
import kotlin.math.sqrt

/** Tracks mag sample stability for interference rejection (critique C3). */
class MagStabilityGate(
    private val windowSize: Int = 12,
    private val maxVariance: Float = 80f,
    private val minMagnitude: Float = 15f,
    private val maxMagnitude: Float = 120f,
) {
    private val samples = ArrayDeque<Triple<Float, Float, Float>>(windowSize)

    fun clear() = samples.clear()

    fun onSample(mx: Float, my: Float, mz: Float) {
        if (samples.size >= windowSize) samples.removeFirst()
        samples.addLast(Triple(mx, my, mz))
    }

    fun isStable(): Boolean {
        if (samples.size < windowSize / 2) return false
        var sumMag = 0f
        var sumX = 0f
        var sumY = 0f
        var sumZ = 0f
        for (s in samples) {
            sumX += s.first
            sumY += s.second
            sumZ += s.third
            sumMag += sqrt(s.first * s.first + s.second * s.second + s.third * s.third)
        }
        val n = samples.size.toFloat()
        val meanMag = sumMag / n
        if (meanMag < minMagnitude || meanMag > maxMagnitude) return false
        val mx = sumX / n
        val my = sumY / n
        val mz = sumZ / n
        var varSum = 0f
        for (s in samples) {
            val dx = s.first - mx
            val dy = s.second - my
            val dz = s.third - mz
            varSum += dx * dx + dy * dy + dz * dz
        }
        return varSum / n < maxVariance
    }

    fun hasSamples(): Boolean = samples.isNotEmpty()
}
