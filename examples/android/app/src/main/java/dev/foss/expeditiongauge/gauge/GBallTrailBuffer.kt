package dev.foss.expeditiongauge.gauge

import kotlin.math.hypot

/**
 * Ring buffer of recent G-ball screen positions for trail rendering.
 */
class GBallTrailBuffer(private val capacity: Int = DEFAULT_CAPACITY) {
    private val xs = FloatArray(capacity)
    private val ys = FloatArray(capacity)
    private var size = 0
    private var head = 0

    fun add(normalizedX: Float, normalizedY: Float) {
        if (size > 0) {
            val lastIdx = (head - 1 + capacity) % capacity
            val dx = normalizedX - xs[lastIdx]
            val dy = normalizedY - ys[lastIdx]
            if (hypot(dx.toDouble(), dy.toDouble()) < MIN_SAMPLE_DISTANCE) return
        }
        xs[head] = normalizedX
        ys[head] = normalizedY
        head = (head + 1) % capacity
        if (size < capacity) size++
    }

    fun clear() {
        size = 0
        head = 0
    }

    /** Oldest first, length [size]. */
    fun snapshot(): List<Pair<Float, Float>> {
        if (size == 0) return emptyList()
        val out = ArrayList<Pair<Float, Float>>(size)
        val start = if (size < capacity) 0 else head
        for (i in 0 until size) {
            val idx = (start + i) % capacity
            out.add(xs[idx] to ys[idx])
        }
        return out
    }

    companion object {
        const val DEFAULT_CAPACITY = 40
        const val MIN_SAMPLE_DISTANCE = 0.02
    }
}
