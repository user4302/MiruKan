package com.user4302.presentation.components

import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.user4302.mika.i18n.mika.AYMR
import com.user4302.mika.presentation.core.i18n.pluralStringResource
import com.user4302.mika.presentation.core.i18n.stringResource
import com.user4302.presentation.entries.DownloadAction
import kotlinx.collections.immutable.persistentListOf

@Composable
fun EntryDownloadDropdownMenu(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    onDownloadClicked: (DownloadAction) -> Unit,
    isManga: Boolean,
    modifier: Modifier = Modifier,
) {
    val downloadAmount = if (isManga) AYMR.plurals.download_amount else AYAYMR.plurals.download_amount_anime
    val downloadUnviewed = if (isManga) AYMR.strings.download_unread else AYAYMR.strings.download_unseen
    val options = persistentListOf(
        DownloadAction.NEXT_1_ITEM to pluralStringResource(downloadAmount, 1, 1),
        DownloadAction.NEXT_5_ITEMS to pluralStringResource(downloadAmount, 5, 5),
        DownloadAction.NEXT_10_ITEMS to pluralStringResource(downloadAmount, 10, 10),
        DownloadAction.NEXT_25_ITEMS to pluralStringResource(downloadAmount, 25, 25),
        DownloadAction.UNVIEWED_ITEMS to stringResource(downloadUnviewed),
    )

    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismissRequest,
        modifier = modifier,
    ) {
        options.map { (downloadAction, string) ->
            DropdownMenuItem(
                text = { Text(text = string) },
                onClick = {
                    onDownloadClicked(downloadAction)
                    onDismissRequest()
                },
            )
        }
    }
}
