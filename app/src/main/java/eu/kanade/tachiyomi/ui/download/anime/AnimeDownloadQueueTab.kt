package eu.kanade.tachiyomi.ui.download.anime

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import eu.kanade.presentation.components.TabContent
import tachiyomi.i18n.aniyomi.AYMR

@Composable
fun Screen.animeDownloadTab(
    nestedScrollConnection: NestedScrollConnection,
): TabContent {
    val navigator = LocalNavigator.currentOrThrow
    val scope = rememberCoroutineScope()
    val screenModel = rememberScreenModel { AnimeDownloadQueueScreenModel() }
    val accordionState by screenModel.accordionState.collectAsState()
    val downloadList by remember(accordionState) {
        derivedStateOf { screenModel.buildDownloadItems(accordionState) }
    }
    val downloadCount by remember {
        derivedStateOf { screenModel.state.value.size }
    }

    return TabContent(
        titleRes = AYMR.strings.label_anime,
        searchEnabled = false,
        content = { contentPadding, _ ->
            AnimeDownloadQueueScreen(
                contentPadding = contentPadding,
                scope = scope,
                screenModel = screenModel,
                nestedScrollConnection = nestedScrollConnection,
            )
        },
        numberTitle = downloadCount,
        navigateUp = navigator::pop,
    )
}
