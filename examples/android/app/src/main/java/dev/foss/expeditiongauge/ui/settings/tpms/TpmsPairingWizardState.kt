package dev.foss.expeditiongauge.ui.settings.tpms

import dev.foss.expeditiongauge.ble.ImuPlacement
import dev.foss.expeditiongauge.ble.tpms.TpmsQrParseResult

enum class TpmsWizardPhase {
    Intro,
    Corner,
    ResolveId,
    Confirm,
    Summary,
}

data class TpmsPairingWizardState(
    val phase: TpmsWizardPhase = TpmsWizardPhase.Intro,
    val cornerIndex: Int = 0,
    val pendingMac: String? = null,
    val parseError: TpmsQrParseResult.Reason? = null,
    val cameraGranted: Boolean = false,
    val useCamera: Boolean = true,
    val acceptArmed: Boolean = true,
    val assigned: Map<ImuPlacement, String> = emptyMap(),
    val skipped: Set<ImuPlacement> = emptySet(),
    val waitingHint: Boolean = false,
) {
    val currentCorner: ImuPlacement
        get() = CORNERS[cornerIndex.coerceIn(0, CORNERS.lastIndex)]

    companion object {
        val CORNERS: List<ImuPlacement> = listOf(
            ImuPlacement.FrontLeft,
            ImuPlacement.FrontRight,
            ImuPlacement.RearLeft,
            ImuPlacement.RearRight,
        )
    }
}
