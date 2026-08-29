package dev.foss.expeditiongauge.ui.inappwhatsnew

import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import dev.foss.expeditiongauge.R
import dev.foss.expeditiongauge.inappwhatsnew.WhatsNew
import dev.foss.expeditiongauge.inappwhatsnew.WhatsNewStore
import kotlinx.coroutines.launch

@Composable
fun WhatsNewButton(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val store = remember { WhatsNewStore(context) }
    val scope = rememberCoroutineScope()
    TextButton(
        onClick = { scope.launch { store.markSeen(WhatsNew.CURRENT) } },
        modifier = modifier.testTag("whats_new"),
    ) {
        Text(stringResource(R.string.whats_new_title) + "\n" + WhatsNew.body())
    }
}
