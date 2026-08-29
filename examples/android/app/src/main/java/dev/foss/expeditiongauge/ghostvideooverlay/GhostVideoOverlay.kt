package dev.foss.expeditiongauge.ghostvideooverlay

import dev.foss.expeditiongauge.data.db.entities.SampleEntity
import dev.foss.expeditiongauge.settings.SpeedUnit
import dev.foss.expeditiongauge.video.VideoOverlayCompositor

/** Pair lap and ghost burn-in lines for overlay export. */
object GhostVideoOverlay {
    fun pairedLines(
        lap: SampleEntity?,
        ghost: SampleEntity?,
        speedUnit: SpeedUnit = SpeedUnit.METRIC,
    ): List<String> {
        val lapLines = VideoOverlayCompositor.overlayLines(lap, speedUnit)
        if (ghost == null) return lapLines
        val ghostLines = VideoOverlayCompositor.overlayLines(ghost, speedUnit)
        val paired = lapLines.zip(ghostLines) { left, right -> "$left | $right" }
        return listOf("Lap | Ghost") + paired
    }

    fun linesForTimestamp(
        lapSamples: List<SampleEntity>,
        ghostSamples: List<SampleEntity>,
        sessionMs: Long,
        speedUnit: SpeedUnit = SpeedUnit.METRIC,
    ): List<String> {
        val lap = VideoOverlayCompositor.nearestSample(lapSamples, sessionMs)
        val ghost = VideoOverlayCompositor.nearestSample(ghostSamples, sessionMs)
            .takeIf { ghostSamples.isNotEmpty() }
        return pairedLines(lap, ghost, speedUnit)
    }
}
