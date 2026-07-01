package dev.foss.expeditiongauge.gauge

/**
 * Remaps device-frame ball position to screen coordinates for [displayRotation].
 * Screen Y = longitudinal (front/back); screen X = lateral.
 */
object GaugeDisplayRotation {
    fun rotateBall(position: BallPosition, displayRotation: Int): BallPosition {
        val (x, y) = when (displayRotation.mod(4)) {
            0 -> position.normalizedX to position.normalizedY
            1 -> -position.normalizedY to position.normalizedX
            2 -> -position.normalizedX to -position.normalizedY
            3 -> position.normalizedY to -position.normalizedX
            else -> position.normalizedX to position.normalizedY
        }
        return position.copy(
            normalizedX = x.coerceIn(-1f, 1f),
            normalizedY = y.coerceIn(-1f, 1f),
        )
    }

    fun mapGForce(latG: Float, lonG: Float, displayRotation: Int): BallPosition =
        rotateBall(GForceBallLogic.mapLatLonG(latG, lonG), displayRotation)

    fun mapAttitude(pitchDeg: Float, rollDeg: Float, displayRotation: Int): BallPosition =
        rotateBall(AttitudeBallLogic.mapPitchRoll(pitchDeg, rollDeg), displayRotation)
}
