package com.user4302.mika.ui.browse.anime.migration.search

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FilterList
import androidx.compose.material3.Icon
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalUriHandler
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.user4302.core.util.ifAnimeSourcesLoaded
import com.user4302.mika.animesource.online.AnimeHttpSource
import com.user4302.mika.core.common.Constants
import com.user4302.mika.domain.entries.anime.model.Anime
import com.user4302.mika.i18n.MR
import com.user4302.mika.presentation.core.components.material.ExtendedFloatingActionButton
import com.user4302.mika.presentation.core.components.material.Scaffold
import com.user4302.mika.presentation.core.i18n.stringResource
import com.user4302.mika.presentation.core.screens.LoadingScreen
import com.user4302.mika.source.local.entries.anime.LocalAnimeSource
import com.user4302.mika.ui.browse.anime.migration.anime.season.MigrateSeasonSelectScreen
import com.user4302.mika.ui.browse.anime.source.browse.BrowseAnimeSourceScreenModel
import com.user4302.mika.ui.browse.anime.source.browse.SourceFilterAnimeDialog
import com.user4302.mika.ui.entries.anime.AnimeScreen
import com.user4302.mika.ui.home.HomeScreen
import com.user4302.mika.ui.webview.WebViewScreen
import com.user4302.presentation.browse.anime.BrowseAnimeSourceContent
import com.user4302.presentation.components.SearchToolbar
import com.user4302.presentation.util.Screen
import kotlinx.coroutines.launch
import mihon.presentation.core.util.collectAsLazyPagingItems

data class AnimeSourceSearchScreen(
    private val oldAnime: Anime,
    private val sourceId: Long,
    private val query: String?,
) : Screen() {

    @Composable
    override fun Content() {
        if (!ifAnimeSourcesLoaded()) {
            LoadingScreen()
            return
        }

        val uriHandler = LocalUriHandler.current
        val navigator = LocalNavigator.currentOrThrow
        val scope = rememberCoroutineScope()

        val screenModel = rememberScreenModel { BrowseAnimeSourceScreenModel(sourceId, query) }
        val state by screenModel.state.collectAsState()

        val snackbarHostState = remember { SnackbarHostState() }

        Scaffold(
            topBar = { scrollBehavior ->
                SearchToolbar(
                    searchQuery = state.toolbarQuery ?: "",
                    onChangeSearchQuery = screenModel::setToolbarQuery,
                    onClickCloseSearch = navigator::pop,
                    onSearch = screenModel::search,
                    scrollBehavior = scrollBehavior,
                )
            },
            floatingActionButton = {
                AnimatedVisibility(visible = state.filters.isNotEmpty()) {
                    ExtendedFloatingActionButton(
                        text = { Text(text = stringResource(MR.strings.action_filter)) },
                        icon = { Icon(Icons.Outlined.FilterList, contentDescription = "") },
                        onClick = screenModel::openFilterSheet,
                    )
                }
            },
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        ) { paddingValues ->
            val openMigrateDialog: (Anime) -> Unit = {
                screenModel.setDialog(BrowseAnimeSourceScreenModel.Dialog.Migrate(newAnime = it, oldAnime = oldAnime))
            }
            BrowseAnimeSourceContent(
                source = screenModel.source,
                animeList = screenModel.animePagerFlowFlow.collectAsLazyPagingItems(),
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
            is BrowseAnimeSourceScreenModel.Dialog.Filter -> {
                SourceFilterAnimeDialog(
                    onDismissRequest = onDismissRequest,
                    filters = state.filters,
                    onReset = screenModel::resetFilters,
                    onFilter = { screenModel.search(filters = state.filters) },
                    onUpdate = screenModel::setFilters,
                )
            }
            is BrowseAnimeSourceScreenModel.Dialog.Migrate -> {
                MigrateAnimeDialog(
                    oldAnime = oldAnime,
                    newAnime = dialog.newAnime,
                    screenModel = rememberScreenModel { MigrateAnimeDialogScreenModel() },
                    onDismissRequest = onDismissRequest,
                    onClickTitle = { navigator.push(AnimeScreen(dialog.newAnime.id)) },
                    onClickSeasons = { navigator.push(MigrateSeasonSelectScreen(oldAnime, dialog.newAnime)) },
                    onPopScreen = {
                        scope.launch {
                            navigator.popUntilRoot()
                            HomeScreen.openTab(HomeScreen.Tab.Browse())
                            navigator.push(AnimeScreen(dialog.newAnime.id))
                        }
                    },
                )
            }
            else -> {}
        }
    }
}
