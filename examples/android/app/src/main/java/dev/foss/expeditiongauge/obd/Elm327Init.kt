package dev.foss.expeditiongauge.obd

import android.util.Log
import java.io.BufferedReader
import java.io.OutputStreamWriter

/**
 * ATZ + echo/line/headers off + auto protocol, then lock with 0100.
 * Ford 2004–2006 (e.g. Expedition U222) has J1850 PWM and HS-CAN on the same
 * DLC; ELM ATSP0 often wins on PWM first. PCM generic OBD is CAN — prefer 6.
 */
internal object Elm327Init {
    private const val TAG = "ExpeditionGauge/Obd"

    fun run(reader: BufferedReader, writer: OutputStreamWriter) {
        Elm327Io.sendCommand(reader, writer, "ATZ", timeoutMs = 8_000L)
        for (cmd in listOf("ATE0", "ATL0", "ATH0", "ATS0", "ATSP0")) {
            Elm327Io.sendCommand(reader, writer, cmd, timeoutMs = 2_500L)
        }
        val autoOk = probePid00(reader, writer)
        val dpn = readDpn(reader, writer)
        val pwmLocked = dpn != null && dpn in '1'..'5'
        if (!autoOk || pwmLocked) {
            if (tryProtocol(reader, writer, "ATSP6")) {
                applyDpn(reader, writer)
                return
            }
            if (!autoOk && tryProtocol(reader, writer, "ATSP1")) {
                applyDpn(reader, writer)
                return
            }
        }
        applyDpn(reader, writer, dpn)
    }

    private fun tryProtocol(
        reader: BufferedReader,
        writer: OutputStreamWriter,
        atsp: String,
    ): Boolean {
        Elm327Io.sendCommand(reader, writer, atsp, timeoutMs = 2_500L)
        return probePid00(reader, writer)
    }

    private fun probePid00(reader: BufferedReader, writer: OutputStreamWriter): Boolean {
        val raw = Elm327Io.sendCommand(reader, writer, "0100", timeoutMs = 12_000L) ?: return false
        return raw.uppercase().contains("4100")
    }

    private fun readDpn(reader: BufferedReader, writer: OutputStreamWriter): Char? {
        val raw = Elm327Io.sendCommand(reader, writer, "ATDPN", timeoutMs = 2_000L) ?: return null
        val chars = Elm327DtcParse.stripNoise(raw).uppercase().filter { it.isLetterOrDigit() }
        return chars.lastOrNull { it in '1'..'9' || it in 'A'..'C' }
    }

    private fun applyDpn(
        reader: BufferedReader,
        writer: OutputStreamWriter,
        known: Char? = null,
    ) {
        val dpn = known ?: readDpn(reader, writer)
        val raw = dpn?.toString().orEmpty()
        Elm327Protocol.canFraming = Elm327DtcParse.canFramingFromDpn(raw)
        Log.i(TAG, "ELM protocol dpn=$raw canFraming=${Elm327Protocol.canFraming}")
    }
}
