package dev.foss.expeditiongauge.recording

import dev.foss.expeditiongauge.batterysaverrecord.BatterySaverRecord
import dev.foss.expeditiongauge.data.db.entities.SampleEntity
import dev.foss.expeditiongauge.telemetry.TelemetrySnapshot
import org.json.JSONArray
import org.json.JSONObject

internal object RecordingSampleWriter {
    fun toEntity(raw: TelemetrySnapshot, sessionId: Long): SampleEntity {
        val snapshot = BatterySaverRecord.apply(raw)
        val extras = JSONObject().apply {
            put("fusionSource", snapshot.fusionSource)
            put("gpsSource", snapshot.gpsSource)
            snapshot.hdop?.let { put("hdop", it) }
            snapshot.numSatellites?.let { put("numSatellites", it) }
            if (snapshot.fixQuality > 0) put("fixQuality", snapshot.fixQuality)
            snapshot.chassisTwistDeg?.let { put("chassisTwistDeg", it) }
            if (snapshot.peakAbsPitchDeg > 0f) put("peakAbsPitchDeg", snapshot.peakAbsPitchDeg)
            if (snapshot.peakAbsRollDeg > 0f) put("peakAbsRollDeg", snapshot.peakAbsRollDeg)
            snapshot.rearSlipRatio?.let { put("rearSlipRatio", it) }
            snapshot.slipSource?.let { put("slipSource", it) }
            snapshot.tpms?.let { tp ->
                put("tpms", JSONObject().apply {
                    listOf("fl", "fr", "rl", "rr").zip(
                        listOf(tp.frontLeft, tp.frontRight, tp.rearLeft, tp.rearRight),
                    ).forEach { (key, corner) ->
                        put(key, JSONObject().apply {
                            corner.pressureKpa?.let { put("pressureKpa", it) }
                            corner.tempC?.let { put("tempC", it) }
                            corner.batteryPct?.let { put("batteryPct", it) }
                            put("lastSeenMs", corner.lastSeenMs)
                        })
                    }
                })
            }
            if (snapshot.tpms != null && snapshot.slipRatio != null) {
                put(
                    "slipTpmsCorrelation",
                    JSONObject().apply {
                        put("slipRatio", snapshot.slipRatio)
                        snapshot.rearSlipRatio?.let { put("rearSlipRatio", it) }
                        snapshot.slipSource?.let { put("slipSource", it) }
                        put(
                            "minPressureKpa",
                            listOfNotNull(
                                snapshot.tpms.frontLeft.pressureKpa,
                                snapshot.tpms.frontRight.pressureKpa,
                                snapshot.tpms.rearLeft.pressureKpa,
                                snapshot.tpms.rearRight.pressureKpa,
                            ).minOrNull() ?: JSONObject.NULL,
                        )
                    },
                )
            }
            if (snapshot.imuStatuses.isNotEmpty()) {
                put(
                    "imuDevices",
                    JSONArray().apply {
                        snapshot.imuStatuses.forEach { entry ->
                            put(
                                JSONObject().apply {
                                    put("deviceId", entry.deviceId)
                                    put("placement", entry.placement)
                                    put("connected", entry.connected)
                                    put("signalQuality", entry.signalQuality)
                                    entry.rawYawDeg?.let { put("rawYawDeg", it) }
                                    entry.filteredYawDeg?.let { put("filteredYawDeg", it) }
                                    entry.latG?.let { put("latG", it) }
                                },
                            )
                        }
                    },
                )
            }
        }
        return SampleEntity(
            sessionId = sessionId,
            timestampMs = snapshot.timestampMs,
            latitude = snapshot.latitude,
            longitude = snapshot.longitude,
            altitudeM = snapshot.altitudeM,
            speedMps = snapshot.speedMps,
            headingDeg = snapshot.headingDeg,
            pitchDeg = snapshot.pitchDeg,
            rollDeg = snapshot.rollDeg,
            lonAccel = snapshot.lonG,
            latG = snapshot.latG,
            yawRate = 0f,
            driftAngleDeg = snapshot.driftAngleDeg,
            bodyYawDeg = snapshot.bodyYawDeg,
            velocityHeadingDeg = snapshot.velocityHeadingDeg,
            throttle = snapshot.throttlePct,
            rpm = snapshot.rpm,
            load = snapshot.engineLoadPct,
            slipRatio = snapshot.slipRatio,
            extrasJson = extras.toString(),
        )
    }
}
