package dev.foss.expeditiongauge.gps

import dev.foss.expeditiongauge.telemetry.TelemetrySnapshot

/** Apply GPS course-over-ground to HUD heading when moving. */
internal object GpsHeadingMerge {
    fun withCourse(
        current: TelemetrySnapshot,
        speedMps: Float,
        gpsCourseDeg: Float,
        extras: TelemetrySnapshot.() -> TelemetrySnapshot,
    ): TelemetrySnapshot {
        val bodyYaw = current.bodyYawDeg ?: current.headingDeg
        val displayHdg = GpsCourseLogic.displayHeadingDeg(bodyYaw, gpsCourseDeg, speedMps)
        return current.extras().copy(
            speedMps = speedMps,
            headingDeg = displayHdg,
            velocityHeadingDeg = gpsCourseDeg,
        )
    }
}
