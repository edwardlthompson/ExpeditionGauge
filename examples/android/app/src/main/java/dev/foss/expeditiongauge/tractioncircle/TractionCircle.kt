package dev.foss.expeditiongauge.tractioncircle

import dev.foss.expeditiongauge.gauge.GForceBallLogic
import kotlin.math.hypot

object TractionCircle {
    const val MAX_G = GForceBallLogic.MAX_G

    fun clamp(latG: Float, lonG: Float): Pair<Float, Float> {
        val mag = hypot(latG.toDouble(), lonG.toDouble()).toFloat()
        if (mag <= MAX_G || mag == 0f) return latG to lonG
        val scale = MAX_G / mag
        return (latG * scale) to (lonG * scale)
    }

    fun liveTrail(gForceMode: Boolean, recording: Boolean): Boolean = recording || gForceMode
}
