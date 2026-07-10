package dev.foss.expeditiongauge.telemetry

import dev.foss.expeditiongauge.ble.BleImuManager
import dev.foss.expeditiongauge.ble.tpms.BleTpmsManager
import dev.foss.expeditiongauge.gps.ExternalNmeaGpsManager
import dev.foss.expeditiongauge.gps.FusedGpsLocationProvider
import dev.foss.expeditiongauge.gps.GpsCourseLogic
import dev.foss.expeditiongauge.obd.ObdClassicManager
import dev.foss.expeditiongauge.obd.ObdTelemetryLog
import dev.foss.expeditiongauge.slip.TireSlipCalculator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * Merges peripheral manager outputs into the single TelemetryBus stream.
 */
class TelemetryOrchestrator(
    private val telemetryBus: TelemetryBus,
    private val bleImuManager: BleImuManager,
    private val bleTpmsManager: BleTpmsManager,
    private val obdManager: ObdClassicManager,
    private val externalGps: ExternalNmeaGpsManager,
    private val fusedGps: FusedGpsLocationProvider,
    private val slipCalculator: TireSlipCalculator = TireSlipCalculator(),
    private val scope: CoroutineScope,
) {
    fun start() {
        scope.launch {
            obdManager.snapshot.collectLatest { obd ->
                mergeObd(obd)
            }
        }
        scope.launch {
            bleTpmsManager.snapshot.collectLatest { tpms ->
                mergeTpms(tpms)
            }
        }
        scope.launch {
            externalGps.fix.collectLatest { fix ->
                fusedGps.onExternalFix(fix)
            }
        }
        scope.launch {
            bleImuManager.sessionsFlow.collectLatest {
                mergeImu()
            }
        }
    }

    private fun mergeObd(obd: dev.foss.expeditiongauge.obd.ObdSnapshot) {
        val current = telemetryBus.snapshots.value
        val gpsSpeed = current.speedMps
        val wheelKmh = obd.wheelSpeedKmh ?: obd.speedKmh
        val wheelMps = wheelKmh?.div(3.6f)
        val slip = slipCalculator.compute(
            gpsSpeedMps = gpsSpeed,
            wheelSpeedMps = wheelMps,
            rearLeftMps = obd.rearLeftKmh?.div(3.6f),
            rearRightMps = obd.rearRightKmh?.div(3.6f),
        )
        telemetryBus.publish(
            current.copy(
                rpm = obd.rpm,
                throttlePct = obd.throttlePct,
                engineLoadPct = obd.engineLoadPct,
                batteryVoltage = obd.batteryVoltage ?: current.batteryVoltage,
                obdConnected = obd.connected,
                slipRatio = slip.slipRatio,
                rearSlipRatio = slip.rearSlipRatio,
                slipSource = slip.source,
                speedFromObd = obd.connected && obd.speedKmh != null,
                speedMps = if (obd.speedKmh != null) obd.speedKmh / 3.6f else current.speedMps,
            ),
        )
        ObdTelemetryLog.publish(
            snapshot = obd,
            slipRatio = slip.slipRatio,
            rearSlipRatio = slip.rearSlipRatio,
            betaDeg = current.driftAngleDeg,
        )
    }

    private fun mergeTpms(tpms: TpmsSnapshot) {
        val current = telemetryBus.snapshots.value
        fun corner(reading: dev.foss.expeditiongauge.telemetry.TpmsCornerReading): TirePressureReading {
            val stale = reading.lastSeenMs == 0L ||
                System.currentTimeMillis() - reading.lastSeenMs > dev.foss.expeditiongauge.ble.tpms.TpmsDeviceSession.STALE_MS
            val psi = reading.pressureKpa?.let { it / 6.894757f }
            return TirePressureReading(
                psi = psi,
                tempC = reading.tempC,
                batteryPct = reading.batteryPct,
                stale = stale,
            )
        }
        telemetryBus.publish(
            current.copy(
                tpms = tpms,
                frontLeftPressure = corner(tpms.frontLeft),
                frontRightPressure = corner(tpms.frontRight),
                rearLeftPressure = corner(tpms.rearLeft),
                rearRightPressure = corner(tpms.rearRight),
            ),
        )
    }

    private fun mergeImu() {
        val current = telemetryBus.snapshots.value
        val bodyYaw = current.bodyYawDeg ?: current.headingDeg
        val fusion = bleImuManager.fuseWithPhone(bodyYaw)
        val statuses = bleImuManager.currentSessions().map { session ->
            ImuStatusEntry(
                deviceId = session.deviceId,
                label = session.displayName,
                placement = session.placement.label,
                connected = session.connected,
                signalQuality = session.signalQuality.name,
                rawYawDeg = session.filter.rawYawDeg(),
                filteredYawDeg = session.filter.yawDeg(),
                latG = session.filter.latG(),
            )
        }
        val yaw = if (fusion.activeCount > 0) fusion.bodyYawDeg else bodyYaw
        val displayHdg = GpsCourseLogic.displayHeadingDeg(
            bodyYawDeg = yaw,
            gpsCourseDeg = current.velocityHeadingDeg,
            speedMps = current.speedMps,
        )
        telemetryBus.publish(
            current.copy(
                imuStatuses = statuses,
                fusionSource = fusion.source,
                chassisTwistDeg = fusion.chassisTwistDeg,
                pitchDeg = if (fusion.activeCount > 0) fusion.pitchDeg else current.pitchDeg,
                rollDeg = if (fusion.activeCount > 0) fusion.rollDeg else current.rollDeg,
                headingDeg = displayHdg,
                bodyYawDeg = yaw,
                latG = if (fusion.activeCount > 0) fusion.latG else current.latG,
                lonG = if (fusion.activeCount > 0) fusion.lonG else current.lonG,
            ),
        )
    }
}
