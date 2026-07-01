package dev.foss.expeditiongauge.gauge

/** Edge readouts for the on-cube G-meter — show values only on the side the ball occupies. */
object GmeterEdgeNumerals {
    const val EDGE_THRESHOLD = 0.05f

    fun topPitchReadout(pitchDeg: Float, ballYNormalized: Float): String =
        if (ballYNormalized < -EDGE_THRESHOLD) GaugeLogic.formatWholeDegrees(pitchDeg) else "--"

    fun bottomPitchReadout(pitchDeg: Float, ballYNormalized: Float): String =
        if (ballYNormalized > EDGE_THRESHOLD) GaugeLogic.formatWholeDegrees(pitchDeg) else "--"

    fun leftRollReadout(rollDeg: Float, ballXNormalized: Float): String =
        if (ballXNormalized < -EDGE_THRESHOLD) GaugeLogic.formatWholeDegrees(rollDeg) else "--"

    fun rightRollReadout(rollDeg: Float, ballXNormalized: Float): String =
        if (ballXNormalized > EDGE_THRESHOLD) GaugeLogic.formatWholeDegrees(rollDeg) else "--"

    fun leftPitchReadout(pitchDeg: Float, ballXNormalized: Float): String =
        if (ballXNormalized < -EDGE_THRESHOLD) GaugeLogic.formatWholeDegrees(pitchDeg) else "--"

    fun rightPitchReadout(pitchDeg: Float, ballXNormalized: Float): String =
        if (ballXNormalized > EDGE_THRESHOLD) GaugeLogic.formatWholeDegrees(pitchDeg) else "--"

    fun topRollReadout(rollDeg: Float, ballYNormalized: Float): String =
        if (ballYNormalized < -EDGE_THRESHOLD) GaugeLogic.formatWholeDegrees(rollDeg) else "--"

    fun bottomRollReadout(rollDeg: Float, ballYNormalized: Float): String =
        if (ballYNormalized > EDGE_THRESHOLD) GaugeLogic.formatWholeDegrees(rollDeg) else "--"

    fun topLonGReadout(lonG: Float, ballYNormalized: Float): String =
        if (ballYNormalized < -EDGE_THRESHOLD) GaugeLogic.formatWholeG(lonG) else "--"

    fun bottomLonGReadout(lonG: Float, ballYNormalized: Float): String =
        if (ballYNormalized > EDGE_THRESHOLD) GaugeLogic.formatWholeG(lonG) else "--"
}
