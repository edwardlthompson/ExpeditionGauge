package dev.foss.expeditiongauge.maghardiron

data class MagSample(val x: Float, val y: Float, val z: Float)

data class HardIronOffset(val x: Float, val y: Float, val z: Float)

/** Fit a hard-iron offset from min/max of a figure-eight sweep. */
object MagHardIron {
    const val MIN_SAMPLES = 8
    private val sweep = ArrayDeque<MagSample>()

    @Synchronized
    fun remember(x: Float, y: Float, z: Float): HardIronOffset? {
        sweep.addLast(MagSample(x, y, z))
        if (sweep.size > 64) sweep.removeFirst()
        return fit(sweep.toList())
    }

    fun fit(samples: List<MagSample>): HardIronOffset? {
        if (samples.size < MIN_SAMPLES) return null
        val xs = samples.map { it.x }
        val ys = samples.map { it.y }
        val zs = samples.map { it.z }
        return HardIronOffset(
            x = (xs.min() + xs.max()) / 2f,
            y = (ys.min() + ys.max()) / 2f,
            z = (zs.min() + zs.max()) / 2f,
        )
    }

    fun apply(sample: MagSample, offset: HardIronOffset): MagSample =
        MagSample(sample.x - offset.x, sample.y - offset.y, sample.z - offset.z)
}
