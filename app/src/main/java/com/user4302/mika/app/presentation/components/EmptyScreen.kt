package com.user4302.presentation.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.HelpOutline
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.user4302.mika.presentation.core.screens.EmptyScreen
import com.user4302.mika.presentation.core.screens.EmptyScreenAction
import com.user4302.presentation.theme.MikaPreviewTheme
import kotlinx.collections.immutable.persistentListOf

@PreviewLightDark
@Composable
private fun NoActionPreview() {
    MikaPreviewTheme {
        Surface {
            EmptyScreen(
                stringRes = AYMR.strings.empty_screen,
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun WithActionPreview() {
    MikaPreviewTheme {
        Surface {
            EmptyScreen(
                stringRes = AYMR.strings.empty_screen,
                actions = persistentListOf(
                    EmptyScreenAction(
                        stringRes = AYMR.strings.action_retry,
                        icon = Icons.Outlined.Refresh,
                        onClick = {},
                    ),
                    EmptyScreenAction(
                        stringRes = AYMR.strings.getting_started_guide,
                        icon = Icons.AutoMirrored.Outlined.HelpOutline,
                        onClick = {},
                    ),
                ),
            )
        }
    }
}
