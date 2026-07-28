package dev.foss.expeditiongauge.ui.settings.tpms

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.foss.expeditiongauge.R
import dev.foss.expeditiongauge.ble.tpms.BleTpmsManager
import dev.foss.expeditiongauge.ble.tpms.TpmsQrParseResult
import dev.foss.expeditiongauge.ble.tpms.TpmsQrPayloadParser
import dev.foss.expeditiongauge.ble.tpms.TpmsSensorIdResolver
import dev.foss.expeditiongauge.ui.navigation.GaugeBackHandler
import dev.foss.expeditiongauge.ui.theme.GaugeMenuSurface
import dev.foss.expeditiongauge.ui.theme.GaugeYellow
import dev.foss.expeditiongauge.ui.theme.SpacingMd
import kotlinx.coroutines.delay

@Composable
fun TpmsPairingWizardScreen(
    bleTpmsManager: BleTpmsManager,
    onDone: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val sessions by bleTpmsManager.sessionsFlow.collectAsStateWithLifecycle(emptyList())
    var phase by rememberSaveable { mutableStateOf(TpmsWizardPhase.Intro.name) }
    var cornerIndex by rememberSaveable { mutableStateOf(0) }
    var pendingMac by rememberSaveable { mutableStateOf<String?>(null) }
    var pendingSensorId by rememberSaveable { mutableStateOf<String?>(null) }
    var parseErrorName by rememberSaveable { mutableStateOf<String?>(null) }
    var useCamera by rememberSaveable { mutableStateOf(true) }
    var showManual by rememberSaveable { mutableStateOf(false) }
    var acceptArmed by rememberSaveable { mutableStateOf(true) }
    var waitingHint by rememberSaveable { mutableStateOf(false) }
    var assignedCsv by rememberSaveable { mutableStateOf("") }
    var skippedCsv by rememberSaveable { mutableStateOf("") }
    var permissionTick by rememberSaveable { mutableStateOf(0) }

    val assigned = remember(assignedCsv) { decodeAssigned(assignedCsv) }
    val skipped = remember(skippedCsv) { decodeSkipped(skippedCsv) }
    val wizardPhase = TpmsWizardPhase.valueOf(phase)
    val parseError = parseErrorName?.let {
        runCatching { TpmsQrParseResult.Reason.valueOf(it) }.getOrNull()
    }
    val cameraGranted = permissionTick >= 0 && ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.CAMERA,
    ) == PackageManager.PERMISSION_GRANTED
    val sensorId = pendingSensorId
    val idCandidates = remember(sessions, sensorId) {
        if (sensorId.isNullOrBlank()) emptyList()
        else TpmsSensorIdResolver.candidates(sessions.map { it.macAddress }, sensorId)
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { granted ->
        permissionTick += 1
        useCamera = granted
        if (!granted) showManual = true
    }
    val btLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult(),
    ) { }

    LaunchedEffect(Unit) { bleTpmsManager.startScan() }
    LaunchedEffect(wizardPhase, useCamera) {
        if (wizardPhase == TpmsWizardPhase.Corner && useCamera && !cameraGranted) {
            cameraLauncher.launch(Manifest.permission.CAMERA)
        }
    }
    // Auto-confirm when exactly one live advertisement matches the binding ID.
    LaunchedEffect(wizardPhase, idCandidates) {
        if (wizardPhase != TpmsWizardPhase.ResolveId) return@LaunchedEffect
        val live = idCandidates.filter {
            it.source == dev.foss.expeditiongauge.ble.tpms.TpmsIdCandidate.Source.LiveAdvertisement
        }
        if (live.size == 1) {
            pendingMac = live[0].macAddress
            acceptArmed = true
            waitingHint = false
            phase = TpmsWizardPhase.Confirm.name
        }
    }

    fun goConfirm(mac: String) {
        pendingMac = mac
        parseErrorName = null
        acceptArmed = true
        waitingHint = false
        phase = TpmsWizardPhase.Confirm.name
    }

    fun applyPayload(raw: String) {
        when (val result = TpmsQrPayloadParser.parse(raw)) {
            is TpmsQrParseResult.Ok -> goConfirm(result.macAddress)
            is TpmsQrParseResult.SensorId -> {
                pendingSensorId = result.hexSuffix
                parseErrorName = null
                phase = TpmsWizardPhase.ResolveId.name
            }
            is TpmsQrParseResult.Invalid -> {
                parseErrorName = result.reason.name
                showManual = true
            }
        }
    }

    fun advanceAfterCorner() {
        if (cornerIndex >= TpmsPairingWizardState.CORNERS.lastIndex) {
            phase = TpmsWizardPhase.Summary.name
        } else {
            cornerIndex += 1
            pendingMac = null
            pendingSensorId = null
            parseErrorName = null
            showManual = !useCamera || !cameraGranted
            acceptArmed = true
            waitingHint = false
            phase = TpmsWizardPhase.Corner.name
        }
    }

    val showCamera = useCamera && cameraGranted && wizardPhase == TpmsWizardPhase.Corner
    val scrollable = !showCamera

    GaugeMenuSurface(modifier = modifier) {
        GaugeBackHandler(
            onBack = {
                when (wizardPhase) {
                    TpmsWizardPhase.Intro, TpmsWizardPhase.Summary -> onDone()
                    TpmsWizardPhase.Corner -> {
                        if (cornerIndex == 0) phase = TpmsWizardPhase.Intro.name
                        else cornerIndex -= 1
                    }
                    TpmsWizardPhase.ResolveId -> {
                        phase = TpmsWizardPhase.Corner.name
                        pendingSensorId = null
                    }
                    TpmsWizardPhase.Confirm -> {
                        phase = if (pendingSensorId != null) {
                            TpmsWizardPhase.ResolveId.name
                        } else {
                            TpmsWizardPhase.Corner.name
                        }
                        pendingMac = null
                        acceptArmed = true
                        waitingHint = false
                    }
                }
            },
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(SpacingMd)
                .then(
                    if (scrollable) Modifier.verticalScroll(rememberScrollState())
                    else Modifier,
                )
                .testTag("tpms_wizard"),
            verticalArrangement = Arrangement.spacedBy(SpacingMd),
        ) {
            Text(
                text = stringResource(R.string.tpms_wizard_title),
                style = MaterialTheme.typography.headlineSmall,
                color = GaugeYellow,
            )
            when (wizardPhase) {
                TpmsWizardPhase.Intro -> TpmsWizardIntroStep(
                    bluetoothEnabled = bleTpmsManager.isBluetoothEnabled(),
                    cameraGranted = cameraGranted,
                    onEnableBluetooth = {
                        btLauncher.launch(Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE))
                    },
                    onAllowCamera = { cameraLauncher.launch(Manifest.permission.CAMERA) },
                    onOpenAppSettings = {
                        context.startActivity(
                            Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                                data = Uri.fromParts("package", context.packageName, null)
                            },
                        )
                    },
                    onContinueManual = {
                        useCamera = false
                        showManual = true
                        phase = TpmsWizardPhase.Corner.name
                        cornerIndex = 0
                    },
                    onContinue = {
                        useCamera = true
                        showManual = false
                        if (!cameraGranted) cameraLauncher.launch(Manifest.permission.CAMERA)
                        phase = TpmsWizardPhase.Corner.name
                        cornerIndex = 0
                    },
                )
                TpmsWizardPhase.Corner -> {
                    val corner = TpmsPairingWizardState.CORNERS[cornerIndex]
                    TpmsWizardCornerStep(
                        corner = corner,
                        stepIndex = cornerIndex,
                        stepCount = TpmsPairingWizardState.CORNERS.size,
                        showCamera = useCamera && cameraGranted,
                        parseError = parseError,
                        showManual = showManual || !(useCamera && cameraGranted),
                        onShowManual = { showManual = true },
                        onRawPayload = { applyPayload(it) },
                        onManualSubmit = { applyPayload(it) },
                        onSkip = {
                            skippedCsv = encodeSkipped(skipped + corner)
                            advanceAfterCorner()
                        },
                        onBack = {
                            if (cornerIndex == 0) phase = TpmsWizardPhase.Intro.name
                            else cornerIndex -= 1
                        },
                    )
                }
                TpmsWizardPhase.ResolveId -> {
                    val corner = TpmsPairingWizardState.CORNERS[cornerIndex]
                    TpmsWizardResolveStep(
                        corner = corner,
                        sensorId = sensorId.orEmpty(),
                        candidates = idCandidates,
                        onSelectMac = { goConfirm(it) },
                        onRescan = {
                            pendingSensorId = null
                            phase = TpmsWizardPhase.Corner.name
                        },
                    )
                }
                TpmsWizardPhase.Confirm -> {
                    val corner = TpmsPairingWizardState.CORNERS[cornerIndex]
                    val mac = pendingMac.orEmpty()
                    TpmsWizardConfirmStep(
                        corner = corner,
                        mac = mac,
                        acceptArmed = acceptArmed,
                        waitingHint = waitingHint,
                        onAccept = {
                            if (!acceptArmed || mac.isEmpty()) return@TpmsWizardConfirmStep
                            acceptArmed = false
                            bleTpmsManager.assignCornerExclusive(mac, corner)
                            assignedCsv = encodeAssigned(assigned + (corner to mac))
                            skippedCsv = encodeSkipped(skipped - corner)
                            val hasReading = bleTpmsManager.knownSessions()
                                .any { it.macAddress.equals(mac, true) && it.lastReading != null }
                            if (!hasReading) waitingHint = true
                        },
                        onRescan = {
                            phase = TpmsWizardPhase.Corner.name
                            pendingMac = null
                            pendingSensorId = null
                            acceptArmed = true
                            waitingHint = false
                        },
                    )
                    if (!acceptArmed) {
                        LaunchedEffect(mac, cornerIndex) {
                            if (waitingHint) delay(900)
                            advanceAfterCorner()
                        }
                    }
                }
                TpmsWizardPhase.Summary -> TpmsWizardSummary(
                    assigned = assigned,
                    skipped = skipped,
                    onDone = onDone,
                )
            }
        }
    }
}
