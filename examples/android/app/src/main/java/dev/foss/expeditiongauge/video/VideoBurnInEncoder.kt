package dev.foss.expeditiongauge.video

import android.graphics.Bitmap
import android.media.MediaCodec
import android.media.MediaCodecInfo
import android.media.MediaFormat
import android.media.MediaMuxer
import java.io.File
import java.nio.ByteBuffer

internal object VideoBurnInEncoder {
    fun encodeFrames(frames: List<Bitmap>, width: Int, height: Int, output: File) {
        val format = MediaFormat.createVideoFormat(MediaFormat.MIMETYPE_VIDEO_AVC, width, height).apply {
            setInteger(MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420Flexible)
            setInteger(MediaFormat.KEY_BIT_RATE, 4_000_000)
            setInteger(MediaFormat.KEY_FRAME_RATE, 10)
            setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, 1)
        }
        val encoder = MediaCodec.createEncoderByType(MediaFormat.MIMETYPE_VIDEO_AVC)
        encoder.configure(format, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE)
        encoder.start()
        val muxer = MediaMuxer(output.absolutePath, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4)
        var trackIndex = -1
        var muxerStarted = false
        val bufferInfo = MediaCodec.BufferInfo()
        val frameDurationUs = 100_000L
        frames.forEachIndexed { index, bitmap ->
            val yuv = bitmapToYuv420(bitmap, width, height)
            val inputIndex = encoder.dequeueInputBuffer(10_000)
            if (inputIndex >= 0) {
                val inputBuffer = encoder.getInputBuffer(inputIndex)!!
                inputBuffer.clear()
                inputBuffer.put(yuv)
                encoder.queueInputBuffer(inputIndex, 0, yuv.size, index * frameDurationUs, 0)
            }
            drainEncoder(encoder, muxer, bufferInfo, trackIndex) { idx ->
                trackIndex = idx
                if (!muxerStarted) {
                    muxer.start()
                    muxerStarted = true
                }
            }
            bitmap.recycle()
        }
        val inputIndex = encoder.dequeueInputBuffer(10_000)
        if (inputIndex >= 0) {
            encoder.queueInputBuffer(inputIndex, 0, 0, frames.size * frameDurationUs, MediaCodec.BUFFER_FLAG_END_OF_STREAM)
        }
        drainEncoder(encoder, muxer, bufferInfo, trackIndex) { idx ->
            trackIndex = idx
            if (!muxerStarted) {
                muxer.start()
                muxerStarted = true
            }
        }
        encoder.stop()
        encoder.release()
        if (muxerStarted) muxer.stop()
        muxer.release()
    }

    private fun drainEncoder(
        encoder: MediaCodec,
        muxer: MediaMuxer,
        info: MediaCodec.BufferInfo,
        trackIndex: Int,
        onTrack: (Int) -> Unit,
    ) {
        var track = trackIndex
        while (true) {
            val outIndex = encoder.dequeueOutputBuffer(info, 10_000)
            if (outIndex == MediaCodec.INFO_TRY_AGAIN_LATER) break
            if (outIndex == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
                track = muxer.addTrack(encoder.outputFormat)
                onTrack(track)
            } else if (outIndex >= 0) {
                val buffer = encoder.getOutputBuffer(outIndex) ?: break
                if (info.size > 0 && track >= 0) {
                    buffer.position(info.offset)
                    buffer.limit(info.offset + info.size)
                    muxer.writeSampleData(track, buffer, info)
                }
                encoder.releaseOutputBuffer(outIndex, false)
                if (info.flags and MediaCodec.BUFFER_FLAG_END_OF_STREAM != 0) break
            }
        }
    }

    private fun bitmapToYuv420(bitmap: Bitmap, width: Int, height: Int): ByteArray {
        val argb = IntArray(width * height)
        bitmap.getPixels(argb, 0, width, 0, 0, width, height)
        val yuv = ByteArray(width * height * 3 / 2)
        var yIndex = 0
        var uvIndex = width * height
        for (j in 0 until height) {
            for (i in 0 until width) {
                val c = argb[j * width + i]
                val r = (c shr 16) and 0xff
                val g = (c shr 8) and 0xff
                val b = c and 0xff
                val y = ((66 * r + 129 * g + 25 * b + 128) shr 8) + 16
                yuv[yIndex++] = y.coerceIn(0, 255).toByte()
                if (j % 2 == 0 && i % 2 == 0) {
                    val u = ((-38 * r - 74 * g + 112 * b + 128) shr 8) + 128
                    val v = ((112 * r - 94 * g - 18 * b + 128) shr 8) + 128
                    yuv[uvIndex++] = u.coerceIn(0, 255).toByte()
                    yuv[uvIndex++] = v.coerceIn(0, 255).toByte()
                }
            }
        }
        return yuv
    }
}
