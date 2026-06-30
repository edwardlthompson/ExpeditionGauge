package dev.foss.expeditiongauge.onboarding

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import dev.foss.expeditiongauge.R
import dev.foss.expeditiongauge.ui.theme.SpacingMd

@Composable
fun OnboardingTour(
    onComplete: () -> Unit,
    onSkip: () -> Unit,
    onRequestPermissions: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val steps = OnboardingStep.entries
    var stepIndex by remember { mutableIntStateOf(0) }
    val step = steps[stepIndex]

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(SpacingMd)
            .testTag("onboarding_tour"),
        verticalArrangement = Arrangement.spacedBy(SpacingMd, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = stringResource(R.string.onboarding_title),
            style = MaterialTheme.typography.headlineSmall,
        )
        Text(
            text = stepText(step),
            style = MaterialTheme.typography.bodyLarge,
        )
        Button(
            onClick = {
                if (step == OnboardingStep.Permissions) {
                    onRequestPermissions()
                }
                if (stepIndex >= steps.lastIndex) {
                    onComplete()
                } else {
                    stepIndex += 1
                }
            },
        ) {
            Text(
                if (stepIndex >= steps.lastIndex) {
                    stringResource(R.string.onboarding_done)
                } else {
                    stringResource(R.string.onboarding_next)
                },
            )
        }
        TextButton(onClick = onSkip, modifier = Modifier.testTag("onboarding_skip")) {
            Text(stringResource(R.string.onboarding_skip))
        }
    }
}

@Composable
private fun stepText(step: OnboardingStep): String = when (step) {
    OnboardingStep.Permissions -> stringResource(R.string.onboarding_permissions)
    OnboardingStep.MountLevel -> stringResource(R.string.onboarding_mount)
    OnboardingStep.FirstRecording -> stringResource(R.string.onboarding_record)
    OnboardingStep.LiveSessionTip -> stringResource(R.string.onboarding_live)
    OnboardingStep.PlaybackReview -> stringResource(R.string.onboarding_playback)
}
