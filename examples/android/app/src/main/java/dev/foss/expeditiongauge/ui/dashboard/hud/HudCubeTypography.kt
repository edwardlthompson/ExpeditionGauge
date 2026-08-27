package dev.foss.expeditiongauge.ui.dashboard.hud

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.sp
import dev.foss.expeditiongauge.ui.theme.LocalTextScale

private const val BASE_SP = 14f

/** Single monospace size for every text string in the telemetry HUD cube. */
@Composable
fun hudCubeTextStyle(): TextStyle {
    val sp = BASE_SP * LocalTextScale.current
    return TextStyle(
        fontFamily = FontFamily.Monospace,
        fontWeight = FontWeight.Bold,
        fontSize = sp.sp,
        lineHeight = (sp * 1.2f).sp,
    )
}

@Composable
fun hudCubeIconDp(): Dp {
    val sp = BASE_SP * LocalTextScale.current
    return with(LocalDensity.current) { sp.sp.toDp() }
}
