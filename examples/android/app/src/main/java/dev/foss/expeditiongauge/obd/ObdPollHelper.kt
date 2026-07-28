package dev.foss.expeditiongauge.obd

import android.bluetooth.BluetoothSocket
import dev.foss.expeditiongauge.settings.ObdPidConfig
import java.io.BufferedReader
import java.io.OutputStreamWriter

internal object ObdPollHelper {
    suspend fun pollSnapshot(
        reader: BufferedReader,
        writer: OutputStreamWriter,
        config: ObdPidConfig,
        previous: ObdSnapshot,
    ): ObdSnapshot {
        val rpm = if (config.rpm) {
            Elm327Protocol.queryPid(reader, writer, "010C")?.let { Elm327Protocol.parseRpm(it) }
        } else {
            previous.rpm
        }
        val speed = if (config.speed) {
            Elm327Protocol.queryPid(reader, writer, "010D")?.let { Elm327Protocol.parseVehicleSpeedKmh(it) }
        } else {
            previous.speedKmh
        }
        val throttle = if (config.throttle) {
            Elm327Protocol.queryPid(reader, writer, "0111")
                ?.let { raw -> Elm327Protocol.parsePidDataByte(raw, "4111")?.times(100f / 255f) }
        } else {
            previous.throttlePct
        }
        val load = if (config.load) {
            Elm327Protocol.queryPid(reader, writer, "0104")
                ?.let { raw -> Elm327Protocol.parsePidDataByte(raw, "4104")?.times(100f / 255f) }
        } else {
            previous.engineLoadPct
        }
        val voltage = if (config.voltage) {
            Elm327Protocol.queryPid(reader, writer, "0142")?.let { Elm327Protocol.parseVoltage(it) }
        } else {
            previous.batteryVoltage
        }
        val rear = if (config.rearWheels && config.speed) {
            queryRearWheelSpeeds(reader, writer)
        } else {
            null to null
        }
        return ObdSnapshot(
            connected = true,
            rpm = rpm,
            speedKmh = speed,
            throttlePct = throttle,
            engineLoadPct = load,
            wheelSpeedKmh = speed,
            rearLeftKmh = rear.first,
            rearRightKmh = rear.second,
            batteryVoltage = voltage,
        )
    }

    private fun queryRearWheelSpeeds(
        reader: BufferedReader,
        writer: OutputStreamWriter,
    ): Pair<Float?, Float?> {
        val left = Elm327Protocol.queryPid(reader, writer, "015A")
            ?.let { Elm327Protocol.parsePidDataByte(it, "415A") }
            ?.takeIf { it > 0f }
        val right = Elm327Protocol.queryPid(reader, writer, "015B")
            ?.let { Elm327Protocol.parsePidDataByte(it, "415B") }
            ?.takeIf { it > 0f }
        if (left != null || right != null) {
            return left to right
        }
        return null to null
    }
}
