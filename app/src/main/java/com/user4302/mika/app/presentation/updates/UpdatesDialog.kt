package com.user4302.presentation.updates

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import com.user4302.mika.i18n.mika.AYMR
import com.user4302.mika.presentation.core.i18n.stringResource

@Composable
fun UpdatesDeleteConfirmationDialog(
    onDismissRequest: () -> Unit,
    onConfirm: () -> Unit,
    isManga: Boolean,
) {
    val title = if (isManga) AYMR.strings.confirm_delete_chapters else AYAYMR.strings.confirm_delete_episodes
    AlertDialog(
        text = {
            Text(text = stringResource(title))
        },
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(onClick = {
                onConfirm()
                onDismissRequest()
            }) {
                Text(text = stringResource(AYMR.strings.action_ok))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text(text = stringResource(AYMR.strings.action_cancel))
            }
        },
    )
}
