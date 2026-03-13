package com.user4302.presentation.library

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.user4302.mika.core.common.preference.CheckboxState
import com.user4302.mika.i18n.mika.AYMR
import com.user4302.mika.presentation.core.components.LabeledCheckbox
import com.user4302.mika.presentation.core.i18n.stringResource
import dev.icerock.moko.resources.StringResource

@Composable
fun DeleteLibraryEntryDialog(
    containsLocalEntry: Boolean,
    onDismissRequest: () -> Unit,
    onConfirm: (Boolean, Boolean) -> Unit,
    isManga: Boolean,
) {
    var list by remember {
        mutableStateOf(
            buildList<CheckboxState.State<StringResource>> {
                val checkbox1 = if (isManga) AYAYMR.strings.manga_from_library else AYAYMR.strings.anime_from_library
                add(CheckboxState.State.None(checkbox1))
                if (!containsLocalEntry) {
                    val checkbox2 = if (isManga) {
                        AYMR.strings.downloaded_chapters
                    } else {
                        AYAYMR.strings.downloaded_episodes
                    }
                    add(CheckboxState.State.None(checkbox2))
                }
            },
        )
    }
    AlertDialog(
        onDismissRequest = onDismissRequest,
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text(text = stringResource(AYMR.strings.action_cancel))
            }
        },
        confirmButton = {
            TextButton(
                enabled = list.any { it.isChecked },
                onClick = {
                    onDismissRequest()
                    onConfirm(
                        list[0].isChecked,
                        list.getOrElse(1) { CheckboxState.State.None(0) }.isChecked,
                    )
                },
            ) {
                Text(text = stringResource(AYMR.strings.action_ok))
            }
        },
        title = {
            Text(text = stringResource(AYMR.strings.action_remove))
        },
        text = {
            Column {
                list.forEach { state ->
                    LabeledCheckbox(
                        label = stringResource(state.value),
                        checked = state.isChecked,
                        onCheckedChange = {
                            val index = list.indexOf(state)
                            if (index != -1) {
                                val mutableList = list.toMutableList()
                                mutableList[index] = state.next() as CheckboxState.State<StringResource>
                                list = mutableList.toList()
                            }
                        },
                    )
                }
            }
        },
    )
}
