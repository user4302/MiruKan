package com.user4302.presentation.browse.anime

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import com.user4302.mika.animesource.AnimeCatalogueSource
import com.user4302.mika.domain.entries.anime.model.Anime
import com.user4302.mika.presentation.core.components.material.Scaffold
import com.user4302.mika.ui.browse.anime.source.globalsearch.AnimeSearchScreenModel
import com.user4302.mika.ui.browse.anime.source.globalsearch.AnimeSourceFilter
import com.user4302.presentation.browse.anime.components.GlobalAnimeSearchToolbar

@Composable
fun MigrateAnimeSearchScreen(
    state: AnimeSearchScreenModel.State,
    fromSourceId: Long?,
    navigateUp: () -> Unit,
    onChangeSearchQuery: (String?) -> Unit,
    onSearch: (String) -> Unit,
    onChangeSearchFilter: (AnimeSourceFilter) -> Unit,
    onToggleResults: () -> Unit,
    getAnime: @Composable (Anime) -> State<Anime>,
    onClickSource: (AnimeCatalogueSource) -> Unit,
    onClickItem: (Anime) -> Unit,
    onLongClickItem: (Anime) -> Unit,
) {
    Scaffold(
        topBar = { scrollBehavior ->
            GlobalAnimeSearchToolbar(
                searchQuery = state.searchQuery,
                progress = state.progress,
                total = state.total,
                navigateUp = navigateUp,
                onChangeSearchQuery = onChangeSearchQuery,
                onSearch = onSearch,
                sourceFilter = state.sourceFilter,
                onChangeSearchFilter = onChangeSearchFilter,
                onlyShowHasResults = state.onlyShowHasResults,
                onToggleResults = onToggleResults,
                scrollBehavior = scrollBehavior,
            )
        },
    ) { paddingValues ->
        GlobalSearchContent(
            fromSourceId = fromSourceId,
            items = state.filteredItems,
            contentPadding = paddingValues,
            getAnime = getAnime,
            onClickSource = onClickSource,
            onClickItem = onClickItem,
            onLongClickItem = onLongClickItem,
        )
    }
}
