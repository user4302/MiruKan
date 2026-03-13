package com.user4302.presentation.crash

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BugReport
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.user4302.mika.presentation.core.components.material.padding
import com.user4302.mika.presentation.core.i18n.stringResource
import com.user4302.mika.presentation.core.screens.InfoScreen
import com.user4302.mika.util.CrashLogUtil
import com.user4302.presentation.theme.MikaPreviewTheme
import kotlinx.coroutines.launch

@Composable
fun CrashScreen(
    exception: Throwable?,
    onRestartClick: () -> Unit,
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    InfoScreen(
        icon = Icons.Outlined.BugReport,
        headingText = stringResource(AYMR.strings.crash_screen_title),
        subtitleText = stringResource(
            AYMR.strings.crash_screen_description,
            stringResource(AYMR.strings.app_name),
        ),
        acceptText = stringResource(AYMR.strings.pref_dump_crash_logs),
        onAcceptClick = {
            scope.launch {
                CrashLogUtil(context).dumpLogs(exception)
            }
        },
        rejectText = stringResource(AYMR.strings.crash_screen_restart_application),
        onRejectClick = onRestartClick,
    ) {
        Box(
            modifier = Modifier
                .padding(vertical = MaterialTheme.padding.small)
                .clip(MaterialTheme.shapes.small)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surfaceVariant),
        ) {
            Text(
                text = exception.toString(),
                modifier = Modifier
                    .padding(all = MaterialTheme.padding.small),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun CrashScreenPreview() {
    MikaPreviewTheme {
        CrashScreen(exception = RuntimeException("Dummy")) {}
    }
}
