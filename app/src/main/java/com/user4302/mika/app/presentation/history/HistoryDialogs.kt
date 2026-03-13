package com.user4302.presentation.history

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.user4302.mika.i18n.mika.AYMR
import com.user4302.mika.presentation.core.components.LabeledCheckbox
import com.user4302.mika.presentation.core.components.material.padding
import com.user4302.mika.presentation.core.i18n.stringResource
import com.user4302.presentation.theme.MikaPreviewTheme
import kotlin.random.Random

@Composable
fun HistoryDeleteDialog(
    onDismissRequest: () -> Unit,
    onDelete: (Boolean) -> Unit,
    isManga: Boolean,
) {
    var removeEverything by remember { mutableStateOf(false) }

    AlertDialog(
        title = {
            Text(text = stringResource(AYMR.strings.action_remove))
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(MaterialTheme.padding.small),
            ) {
                val subtitle = if (isManga) {
                    AYMR.strings.dialog_with_checkbox_remove_description
                } else {
                    AYAYMR.strings.dialog_with_checkbox_remove_description_anime
                }
                Text(text = stringResource(subtitle))

                LabeledCheckbox(
                    label = if (isManga) {
                        stringResource(AYAYMR.strings.dialog_with_checkbox_reset)
                    } else {
                        stringResource(AYAYMR.strings.dialog_with_checkbox_reset_anime)
                    },
                    checked = removeEverything,
                    onCheckedChange = { removeEverything = it },
                )
            }
        },
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(onClick = {
                onDelete(removeEverything)
                onDismissRequest()
            }) {
                Text(text = stringResource(AYMR.strings.action_remove))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text(text = stringResource(AYMR.strings.action_cancel))
            }
        },
    )
}

@Composable
fun HistoryDeleteAllDialog(
    onDismissRequest: () -> Unit,
    onDelete: () -> Unit,
) {
    AlertDialog(
        title = {
            Text(text = stringResource(AYMR.strings.action_remove_everything))
        },
        text = {
            Text(text = stringResource(AYMR.strings.clear_history_confirmation))
        },
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(onClick = {
                onDelete()
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

@PreviewLightDark
@Composable
private fun HistoryDeleteDialogPreview() {
    MikaPreviewTheme {
        HistoryDeleteDialog(
            onDismissRequest = {},
            onDelete = {},
            isManga = Random.nextBoolean(),
        )
    }
}
