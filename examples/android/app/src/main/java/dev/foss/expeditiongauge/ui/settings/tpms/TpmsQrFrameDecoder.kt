package dev.foss.expeditiongauge.ui.settings.tpms

import androidx.camera.core.ImageProxy
import com.google.zxing.BarcodeFormat
import com.google.zxing.BinaryBitmap
import com.google.zxing.DecodeHintType
import com.google.zxing.InvertedLuminanceSource
import com.google.zxing.MultiFormatReader
import com.google.zxing.PlanarYUVLuminanceSource
import com.google.zxing.common.HybridBinarizer

/** Robust CameraX YUV → ZXing QR decode (stride, invert, 90° rotations). */
internal object TpmsQrFrameDecoder {
    private val hints: Map<DecodeHintType, Any> = mapOf(
        DecodeHintType.POSSIBLE_FORMATS to listOf(BarcodeFormat.QR_CODE),
        DecodeHintType.TRY_HARDER to true,
        DecodeHintType.CHARACTER_SET to "UTF-8",
        DecodeHintType.ALSO_INVERTED to true,
    )

    fun decode(imageProxy: ImageProxy): String? {
        val y = extractY(imageProxy) ?: return null
        var width = imageProxy.width
        var height = imageProxy.height
        var data = y
        val reader = MultiFormatReader().apply { setHints(hints) }
        repeat(4) {
            tryDecode(reader, data, width, height)?.let { return it }
            val rotated = rotateCw(data, width, height)
            data = rotated
            val tmp = width
            width = height
            height = tmp
        }
        return null
    }

    private fun tryDecode(
        reader: MultiFormatReader,
        data: ByteArray,
        width: Int,
        height: Int,
    ): String? {
        val source = PlanarYUVLuminanceSource(data, width, height, 0, 0, width, height, false)
        return decodeSource(reader, source) ?: decodeSource(reader, InvertedLuminanceSource(source))
    }

    private fun decodeSource(reader: MultiFormatReader, source: com.google.zxing.LuminanceSource): String? =
        try {
            reader.decodeWithState(BinaryBitmap(HybridBinarizer(source))).text
        } catch (_: Exception) {
            null
        } finally {
            reader.reset()
        }

    private fun extractY(imageProxy: ImageProxy): ByteArray? {
        val plane = imageProxy.planes.firstOrNull() ?: return null
        val buffer = plane.buffer.duplicate()
        val width = imageProxy.width
        val height = imageProxy.height
        val rowStride = plane.rowStride
        val pixelStride = plane.pixelStride
        val out = ByteArray(width * height)
        if (pixelStride == 1 && rowStride == width) {
            val n = minOf(out.size, buffer.remaining())
            buffer.get(out, 0, n)
            return out
        }
        // Handle padded rows and/or pixelStride > 1 (common on CameraX YUV_420_888).
        var dst = 0
        for (row in 0 until height) {
            val rowStart = row * rowStride
            for (col in 0 until width) {
                out[dst++] = buffer.get(rowStart + col * pixelStride)
            }
        }
        return out
    }

    private fun rotateCw(src: ByteArray, width: Int, height: Int): ByteArray {
        val out = ByteArray(src.size)
        for (y in 0 until height) {
            for (x in 0 until width) {
                out[x * height + (height - 1 - y)] = src[y * width + x]
            }
        }
        return out
    }
}
