package com.user4302.mika.ui.browse.anime.migration.anime.season

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalUriHandler
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.user4302.core.util.ifAnimeSourcesLoaded
import com.user4302.mika.animesource.online.AnimeHttpSource
import com.user4302.mika.core.common.Constants
import com.user4302.mika.domain.entries.anime.model.Anime
import com.user4302.mika.presentation.core.components.material.Scaffold
import com.user4302.mika.presentation.core.screens.LoadingScreen
import com.user4302.mika.source.local.entries.anime.LocalAnimeSource
import com.user4302.mika.ui.browse.anime.migration.search.MigrateAnimeDialog
import com.user4302.mika.ui.browse.anime.migration.search.MigrateAnimeDialogScreenModel
import com.user4302.mika.ui.entries.anime.AnimeScreen
import com.user4302.mika.ui.webview.WebViewScreen
import com.user4302.presentation.browse.anime.BrowseAnimeSourceContent
import com.user4302.presentation.components.AppBar
import com.user4302.presentation.util.Screen
import mihon.presentation.core.util.collectAsLazyPagingItems

data class MigrateSeasonSelectScreen(
    private val oldAnime: Anime,
    private val anime: Anime,
) : Screen() {
    @Composable
    override fun Content() {
        if (!ifAnimeSourcesLoaded()) {
            LoadingScreen()
            return
        }

        val uriHandler = LocalUriHandler.current
        val navigator = LocalNavigator.currentOrThrow

        val screenModel = rememberScreenModel { MigrateSeasonSelectScreenModel(anime) }
        val state by screenModel.state.collectAsState()

        val snackbarHostState = remember { SnackbarHostState() }

        Scaffold(
            topBar = { scrollBehavior ->
                AppBar(
                    title = anime.title,
                    navigateUp = navigator::pop,
                    scrollBehavior = scrollBehavior,
                )
            },
        ) { paddingValues ->
            val openMigrateDialog: (Anime) -> Unit = {
                screenModel.setDialog(MigrateSeasonSelectScreenModel.Dialog.Migrate(newAnime = it, oldAnime = oldAnime))
            }
            BrowseAnimeSourceContent(
                source = screenModel.source,
                animeList = screenModel.seasonPagerFlowFlow.collectAsLazyPagingItems(),
                columns = screenModel.getColumnsPreference(LocalConfiguration.current.orientation),
                displayMode = screenModel.displayMode,
                snackbarHostState = snackbarHostState,
                contentPadding = paddingValues,
                onWebViewClick = {
                    val source = screenModel.source as? AnimeHttpSource ?: return@BrowseAnimeSourceContent
                    navigator.push(
                        WebViewScreen(
                            url = source.baseUrl,
                            initialTitle = source.name,
                            sourceId = source.id,
                        ),
                    )
                },
                onHelpClick = { uriHandler.openUri(Constants.URL_HELP) },
                onLocalAnimeSourceHelpClick = { uriHandler.openUri(LocalAnimeSource.HELP_URL) },
                onAnimeClick = openMigrateDialog,
                onAnimeLongClick = { navigator.push(AnimeScreen(it.id, true)) },
            )
        }

        val onDismissRequest = { screenModel.setDialog(null) }
        when (val dialog = state.dialog) {
            is MigrateSeasonSelectScreenModel.Dialog.Migrate -> {
                MigrateAnimeDialog(
                    oldAnime = dialog.oldAnime,
                    newAnime = dialog.newAnime,
                    screenModel = rememberScreenModel { MigrateAnimeDialogScreenModel() },
                    onDismissRequest = onDismissRequest,
                    onClickTitle = { navigator.push(AnimeScreen(dialog.newAnime.id)) },
                    onClickSeasons = { navigator.push(MigrateSeasonSelectScreen(oldAnime, dialog.newAnime)) },
                    onPopScreen = {
                        onDismissRequest()
                    },
                )
            }
            else -> {}
        }
    }
}
