package dev.foss.expeditiongauge.obd

import android.bluetooth.BluetoothSocket
import java.io.InputStream
import java.io.OutputStream

internal class ObdLink(
    val input: InputStream,
    val output: OutputStream,
    private val onClose: () -> Unit,
) {
    fun close() {
        runCatching { onClose() }
    }

    companion object {
        fun bluetooth(sock: BluetoothSocket) =
            ObdLink(sock.inputStream, sock.outputStream) { sock.close() }
    }
}
