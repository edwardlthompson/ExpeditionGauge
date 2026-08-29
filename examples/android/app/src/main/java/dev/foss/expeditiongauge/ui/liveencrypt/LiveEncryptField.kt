package dev.foss.expeditiongauge.ui.liveencrypt

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import dev.foss.expeditiongauge.R
import dev.foss.expeditiongauge.liveencrypt.LiveEncrypt

@Composable
fun LiveEncryptField(modifier: Modifier = Modifier) {
    var key by remember { mutableStateOf(LiveEncrypt.activeKey.orEmpty()) }
    OutlinedTextField(
        value = key,
        onValueChange = { value ->
            key = value
            LiveEncrypt.activeKey = value.ifBlank { null }
        },
        label = { Text(stringResource(R.string.live_encrypt_key)) },
        modifier = modifier
            .fillMaxWidth()
            .testTag("live_encrypt_key"),
        singleLine = true,
    )
}
