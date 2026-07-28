package dev.foss.expeditiongauge.ui.settings.tpms

import android.util.Log
import androidx.camera.core.ImageAnalysis
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import dev.foss.expeditiongauge.R
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicLong

/**
 * FOSS CameraX + ZXing QR preview. Do not place inside a vertically scrolling parent.
 */
@Composable
fun TpmsQrScanner(
    enabled: Boolean,
    onRawPayload: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val mainExecutor = remember { ContextCompat.getMainExecutor(context) }
    val lastEmitMs = remember { AtomicLong(0L) }
    val analysisExecutor = remember { Executors.newSingleThreadExecutor() }
    var errorText by remember { mutableStateOf<String?>(null) }
    var scanning by remember { mutableStateOf(false) }
    val controller = remember {
        LifecycleCameraController(context).apply {
            setEnabledUseCases(CameraController.IMAGE_ANALYSIS)
            imageAnalysisBackpressureStrategy = ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST
        }
    }
    val payloadHandler = rememberUpdatedState(onRawPayload)

    DisposableEffect(Unit) {
        onDispose {
            runCatching {
                controller.clearImageAnalysisAnalyzer()
                controller.unbind()
            }
            analysisExecutor.shutdown()
        }
    }

    DisposableEffect(enabled) {
        if (!enabled) {
            controller.clearImageAnalysisAnalyzer()
            scanning = false
            onDispose { }
        } else {
            controller.setImageAnalysisAnalyzer(analysisExecutor) { imageProxy ->
                try {
                    if (!scanning) {
                        mainExecutor.execute { scanning = true }
                    }
                    val text = TpmsQrFrameDecoder.decode(imageProxy) ?: return@setImageAnalysisAnalyzer
                    val now = System.currentTimeMillis()
                    val prev = lastEmitMs.get()
                    if (now - prev >= DEBOUNCE_MS && lastEmitMs.compareAndSet(prev, now)) {
                        Log.i(TAG, "QR decoded len=${text.length}")
                        mainExecutor.execute { payloadHandler.value(text) }
                    }
                } catch (e: Exception) {
                    Log.w(TAG, "Frame decode error", e)
                } finally {
                    imageProxy.close()
                }
            }
            onDispose { controller.clearImageAnalysisAnalyzer() }
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(280.dp)
            .background(Color.Black),
        contentAlignment = Alignment.Center,
    ) {
        if (enabled) {
            AndroidView(
                factory = { ctx ->
                    PreviewView(ctx).apply {
                        scaleType = PreviewView.ScaleType.FILL_CENTER
                        implementationMode = PreviewView.ImplementationMode.COMPATIBLE
                        this.controller = controller
                        try {
                            controller.bindToLifecycle(lifecycleOwner)
                            errorText = null
                        } catch (e: Exception) {
                            Log.e(TAG, "Camera bind failed", e)
                            errorText = e.message ?: "Camera unavailable"
                        }
                    }
                },
                update = { view ->
                    if (view.controller !== controller) view.controller = controller
                },
                modifier = Modifier.fillMaxSize(),
                onRelease = { runCatching { controller.unbind() } },
            )
        }
        when {
            errorText != null -> Text(text = errorText!!, color = Color.White)
            enabled && scanning -> Text(
                text = stringResource(R.string.tpms_wizard_scanning),
                color = Color.White.copy(alpha = 0.85f),
                modifier = Modifier.align(Alignment.BottomCenter),
            )
        }
    }
}

private const val TAG = "TpmsQrScanner"
private const val DEBOUNCE_MS = 1200L
