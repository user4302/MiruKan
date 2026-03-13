package com.user4302.presentation.browse

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import com.user4302.mika.presentation.core.i18n.stringResource

@Composable
fun RemoveEntryDialog(
    onDismissRequest: () -> Unit,
    onConfirm: () -> Unit,
    entryToRemove: String,
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text(text = stringResource(AYMR.strings.action_cancel))
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onDismissRequest()
                    onConfirm()
                },
            ) {
                Text(text = stringResource(AYMR.strings.action_remove))
            }
        },
        title = {
            Text(text = stringResource(AYMR.strings.are_you_sure))
        },
        text = {
            Text(text = stringResource(AYMR.strings.remove_manga, entryToRemove))
        },
    )
}
