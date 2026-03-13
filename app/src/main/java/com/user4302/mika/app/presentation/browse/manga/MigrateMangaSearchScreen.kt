package com.user4302.presentation.browse.manga

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import com.user4302.mika.domain.entries.manga.model.Manga
import com.user4302.mika.presentation.core.components.material.Scaffold
import com.user4302.mika.source.CatalogueSource
import com.user4302.mika.ui.browse.manga.source.globalsearch.MangaSearchScreenModel
import com.user4302.mika.ui.browse.manga.source.globalsearch.MangaSourceFilter
import com.user4302.presentation.browse.manga.components.GlobalMangaSearchToolbar

@Composable
fun MigrateMangaSearchScreen(
    state: MangaSearchScreenModel.State,
    fromSourceId: Long?,
    navigateUp: () -> Unit,
    onChangeSearchQuery: (String?) -> Unit,
    onSearch: (String) -> Unit,
    onChangeSearchFilter: (MangaSourceFilter) -> Unit,
    onToggleResults: () -> Unit,
    getManga: @Composable (Manga) -> State<Manga>,
    onClickSource: (CatalogueSource) -> Unit,
    onClickItem: (Manga) -> Unit,
    onLongClickItem: (Manga) -> Unit,
) {
    Scaffold(
        topBar = { scrollBehavior ->
            GlobalMangaSearchToolbar(
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
            getManga = getManga,
            onClickSource = onClickSource,
            onClickItem = onClickItem,
            onLongClickItem = onLongClickItem,
        )
    }
}
