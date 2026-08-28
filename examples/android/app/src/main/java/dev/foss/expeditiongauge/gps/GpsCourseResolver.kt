package dev.foss.expeditiongauge.gps

/**
 * Picks vehicle course-over-ground for the HUD.
 *
 * Ranked by accuracy while driving:
 * 1. GNSS chip COG (`Location.bearing` / NMEA RMC) — Doppler-based, ~1–5°.
 * 2. Lat/lon delta over a long enough segment — noisy; GPS jitter is 3–5 m.
 * 3. Last good course — hold instead of inventing 0° (due north).
 *
 * Phone IMU/mag yaw is not used here; [GpsCourseLogic.displayHeadingDeg] falls
 * back to body yaw only when no GPS course has been established.
 */
object GpsCourseResolver {
    const val MAX_BEARING_ACCURACY_DEG = 45f
    const val POSITION_COURSE_MIN_M = 8f
    const val MAX_TELEPORT_M = 250f
    const val NORTH_SENTINEL_DEG = 1.5f
    const val DISAGREE_DEG = 25f

    fun resolveCourseDeg(
        chipBearingDeg: Float?,
        speedMps: Float,
        fromLat: Double?,
        fromLon: Double?,
        toLat: Double,
        toLon: Double,
        previousCourseDeg: Float?,
        chipBearingAccuracyDeg: Float? = null,
        allowPositionDelta: Boolean = true,
    ): Float? {
        val move = positionDeltaCourse(
            allowPositionDelta, speedMps, fromLat, fromLon, toLat, toLon,
        )
        val chip = validatedChip(chipBearingDeg, chipBearingAccuracyDeg, speedMps)
        if (chip != null && !isBogusNorth(chip, move, previousCourseDeg)) {
            return GpsCourseLogic.normalize360(chip)
        }
        if (move != null) return GpsCourseLogic.normalize360(move)
        return previousCourseDeg?.let { GpsCourseLogic.normalize360(it) }
    }

    fun shouldAdvanceAnchor(segmentM: Float): Boolean =
        segmentM >= POSITION_COURSE_MIN_M || segmentM > MAX_TELEPORT_M

    fun angularDistance(aDeg: Float, bDeg: Float): Float {
        val d = kotlin.math.abs(
            GpsCourseLogic.normalize360(aDeg) - GpsCourseLogic.normalize360(bDeg),
        )
        return if (d > 180f) 360f - d else d
    }

    internal fun isBogusNorth(chip: Float, move: Float?, previous: Float?): Boolean {
        if (angularDistance(chip, 0f) > NORTH_SENTINEL_DEG) return false
        if (move != null && angularDistance(move, 0f) > DISAGREE_DEG) return true
        if (move == null && previous != null && angularDistance(previous, 0f) > DISAGREE_DEG) {
            return true
        }
        return false
    }

    private fun validatedChip(
        chipBearingDeg: Float?,
        accuracyDeg: Float?,
        speedMps: Float,
    ): Float? {
        if (chipBearingDeg == null || !chipBearingDeg.isFinite()) return null
        if (speedMps < GpsCourseLogic.MIN_SPEED_MPS) return null
        if (accuracyDeg != null && accuracyDeg > MAX_BEARING_ACCURACY_DEG) return null
        return chipBearingDeg
    }

    private fun positionDeltaCourse(
        allowPositionDelta: Boolean,
        speedMps: Float,
        fromLat: Double?,
        fromLon: Double?,
        toLat: Double,
        toLon: Double,
    ): Float? {
        if (!allowPositionDelta || fromLat == null || fromLon == null) return null
        val segment = GpsCourseLogic.distanceM(fromLat, fromLon, toLat, toLon)
        if (segment > MAX_TELEPORT_M) return null
        if (segment < POSITION_COURSE_MIN_M) return null
        if (!GpsCourseLogic.isReliableCourse(speedMps, segment)) return null
        return GpsCourseLogic.bearingDeg(fromLat, fromLon, toLat, toLon)
    }
}
