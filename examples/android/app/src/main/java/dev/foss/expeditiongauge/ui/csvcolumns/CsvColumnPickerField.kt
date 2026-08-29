package dev.foss.expeditiongauge.ui.csvcolumns

import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.foss.expeditiongauge.csvcolumns.CsvColumnPicker
import dev.foss.expeditiongauge.settings.CsvColumnStore
import kotlinx.coroutines.launch

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CsvColumnPickerField(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val store = remember { CsvColumnStore(context) }
    val selected by store.columns.collectAsStateWithLifecycle(CsvColumnPicker.ALL.toSet())
    FlowRow(modifier = modifier.testTag("csv_column_picker")) {
        CsvColumnPicker.ALL.forEach { col ->
            FilterChip(
                selected = col in selected,
                onClick = { scope.launch { store.toggle(col) } },
                label = { Text(col) },
            )
        }
    }
}
