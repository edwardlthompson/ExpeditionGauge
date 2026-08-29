package dev.foss.expeditiongauge.ui.sessionfavorites

import androidx.compose.material3.TextButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.foss.expeditiongauge.R
import dev.foss.expeditiongauge.settings.SessionFavoritesStore
import kotlinx.coroutines.launch

@Composable
fun SessionFavoriteToggle(
    sessionId: Long,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val store = remember { SessionFavoritesStore(context) }
    val ids by store.favoriteIds.collectAsStateWithLifecycle(emptySet())
    val starred = sessionId in ids
    TextButton(
        onClick = { scope.launch { store.toggle(sessionId) } },
        modifier = modifier.testTag("session_favorite_toggle"),
    ) {
        Text(
            text = stringResource(
                if (starred) R.string.session_favorite_on else R.string.session_favorite_off,
            ),
        )
    }
}
