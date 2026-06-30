package dev.foss.expeditiongauge.drivingline

import dev.foss.expeditiongauge.data.db.entities.SampleEntity
import org.junit.Assert.assertTrue
import org.junit.Test

class DrivingLineAnalyzerTest {
    @Test
    fun findsApexAtLatGPeak() {
        val samples = (0..4).map { i ->
            SampleEntity(
                id = i.toLong() + 1,
                sessionId = 1L,
                timestampMs = i * 100L,
                latG = when (i) {
                    2 -> 1.0f
                    else -> 0.2f
                },
                latitude = 0.0,
                longitude = i * 0.0001,
            )
        }
        val analysis = DrivingLineAnalyzer().analyze(samples)
        assertTrue(analysis.apexPoints.isNotEmpty())
    }
}
