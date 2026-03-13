package com.user4302.presentation.more.settings.screen.player.editor.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import com.user4302.mika.i18n.mika.AYMR
import com.user4302.mika.presentation.core.i18n.stringResource
import com.user4302.presentation.more.settings.screen.player.editor.FileCreationResult
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.seconds

@Composable
fun FileCreateDialog(
    initialName: String?,
    fileExtension: String,
    onDismissRequest: () -> Unit,
    isValid: (String, String?) -> FileCreationResult,
    onConfirm: (String) -> Unit,
) {
    val initialTextValue = initialName ?: "file.$fileExtension"
    var fileName by remember {
        mutableStateOf(
            TextFieldValue(
                text = initialTextValue,
                selection = TextRange(
                    0,
                    initialTextValue.indexOfLast { it == '.' }.takeUnless { it == -1 } ?: initialTextValue.length,
                ),
            ),
        )
    }
    val result = remember(fileName.text) {
        isValid(fileName.text, initialName)
    }

    val focusRequester = remember { FocusRequester() }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(
                enabled = result is FileCreationResult.Success,
                onClick = {
                    onConfirm(fileName.text)
                    onDismissRequest()
                },
            ) {
                Text(
                    text = stringResource(
                        if (initialName ==
                            null
                        ) {
                            AYMR.strings.action_add
                        } else {
                            AYMR.strings.action_edit
                        },
                    ),
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text(text = stringResource(AYMR.strings.action_cancel))
            }
        },
        title = {
            Text(
                text = stringResource(
                    if (initialName ==
                        null
                    ) {
                        AYAYMR.strings.editor_create_file
                    } else {
                        AYAYMR.strings.editor_edit_file
                    },
                ),
            )
        },
        text = {
            OutlinedTextField(
                modifier = Modifier.focusRequester(focusRequester),
                value = fileName,
                onValueChange = { fileName = it },
                label = { Text(text = stringResource(AYAYMR.strings.editor_filename)) },
                supportingText = {
                    when (result) {
                        is FileCreationResult.Failure -> {
                            Text(text = stringResource(result.stringRes))
                        }
                        FileCreationResult.Success -> {}
                    }
                },
                isError = result is FileCreationResult.Failure,
                singleLine = true,
            )
        },
    )

    LaunchedEffect(focusRequester) {
        // TODO: https://issuetracker.google.com/issues/204502668
        delay(0.1.seconds)
        focusRequester.requestFocus()
    }
}

@Composable
fun FileDeleteDialog(
    name: String,
    onDismissRequest: () -> Unit,
    onDelete: () -> Unit,
) {
    AlertDialog(
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
        title = {
            Text(text = stringResource(AYAYMR.strings.editor_delete_file))
        },
        text = {
            Text(text = stringResource(AYAYMR.strings.editor_delete_file_confirmation, name))
        },
    )
}

@Composable
fun UnsavedChangesDialog(
    onDismissRequest: () -> Unit,
    onConfirm: () -> Unit,
) {
    AlertDialog(
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
        title = {
            Text(text = stringResource(AYMR.strings.label_warning))
        },
        text = {
            Text(text = stringResource(AYAYMR.strings.editor_unsaved_progress))
        },
    )
}
