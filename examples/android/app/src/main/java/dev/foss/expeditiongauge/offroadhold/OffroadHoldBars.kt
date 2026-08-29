package dev.foss.expeditiongauge.offroadhold

import kotlin.math.abs

object OffroadHoldBars {
    fun active(crawling: Boolean, offroadPreset: Boolean): Boolean = crawling || offroadPreset

    fun held(liveDeg: Float, peakDeg: Float): Float =
        if (abs(peakDeg) > abs(liveDeg)) peakDeg else liveDeg
}
