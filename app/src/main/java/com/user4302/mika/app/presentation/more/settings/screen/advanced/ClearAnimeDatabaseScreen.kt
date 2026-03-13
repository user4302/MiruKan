package com.user4302.presentation.more.settings.screen.advanced

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FlipToBack
import androidx.compose.material.icons.outlined.SelectAll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastMap
import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.user4302.mika.animesource.model.FetchType
import com.user4302.mika.core.common.util.lang.launchIO
import com.user4302.mika.core.common.util.lang.launchUI
import com.user4302.mika.core.common.util.lang.withNonCancellableContext
import com.user4302.mika.data.anime.AnimeDatabase
import com.user4302.mika.data.source.anime.mapSourceToDomainSource
import com.user4302.mika.domain.source.anime.interactor.GetAnimeSourcesWithNonLibraryAnime
import com.user4302.mika.domain.source.anime.model.AnimeSource
import com.user4302.mika.domain.source.anime.model.AnimeSourceWithIds
import com.user4302.mika.domain.source.anime.model.StubAnimeSource
import com.user4302.mika.domain.source.anime.service.AnimeSourceManager
import com.user4302.mika.i18n.mika.AYMR
import com.user4302.mika.presentation.core.components.material.Scaffold
import com.user4302.mika.presentation.core.i18n.stringResource
import com.user4302.mika.presentation.core.screens.EmptyScreen
import com.user4302.mika.presentation.core.screens.LoadingScreen
import com.user4302.mika.presentation.core.util.selectedBackground
import com.user4302.mika.util.system.toast
import com.user4302.presentation.browse.anime.components.AnimeSourceIcon
import com.user4302.presentation.components.AppBar
import com.user4302.presentation.components.AppBarActions
import com.user4302.presentation.util.Screen
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import uy.kohesive.injekt.Injekt
import uy.kohesive.injekt.api.get

class ClearAnimeDatabaseScreen : Screen() {

    @Composable
    override fun Content() {
        val context = LocalContext.current
        val navigator = LocalNavigator.currentOrThrow
        val model = rememberScreenModel { ClearAnimeDatabaseScreenModel() }
        val state by model.state.collectAsState()
        val scope = rememberCoroutineScope()

        when (val s = state) {
            is ClearAnimeDatabaseScreenModel.State.Loading -> LoadingScreen()
            is ClearAnimeDatabaseScreenModel.State.Ready -> {
                if (s.showConfirmation) {
                    AlertDialog(
                            onDismissRequest = model::hideConfirmation,
                            confirmButton = {
                                TextButton(
                                        onClick = {
                                            scope.launchUI {
                                                model.removeAnimeBySourceId()
                                                model.clearSelection()
                                                model.hideConfirmation()
                                                context.toast(AYMR.strings.clear_database_completed)
                                            }
                                        },
                                ) { Text(text = stringResource(AYMR.strings.action_ok)) }
                            },
                            dismissButton = {
                                TextButton(onClick = model::hideConfirmation) {
                                    Text(text = stringResource(AYMR.strings.action_cancel))
                                }
                            },
                            text = {
                                Text(
                                        text =
                                                stringResource(
                                                        AYAYMR.strings.clear_database_confirmation
                                                )
                                )
                            },
                    )
                }

                Scaffold(
                        topBar = { scrollBehavior ->
                            AppBar(
                                    title =
                                            stringResource(
                                                    AYAYMR.strings.pref_clear_anime_database
                                            ),
                                    navigateUp = navigator::pop,
                                    actions = {
                                        if (s.items.isNotEmpty()) {
                                            AppBarActions(
                                                    actions =
                                                            persistentListOf(
                                                                    AppBar.Action(
                                                                            title =
                                                                                    stringResource(
                                                                                            AYMR.strings
                                                                                                    .action_select_all
                                                                                    ),
                                                                            icon =
                                                                                    Icons.Outlined
                                                                                            .SelectAll,
                                                                            onClick =
                                                                                    model::selectAll,
                                                                    ),
                                                                    AppBar.Action(
                                                                            title =
                                                                                    stringResource(
                                                                                            AYMR.strings
                                                                                                    .action_select_all
                                                                                    ),
                                                                            icon =
                                                                                    Icons.Outlined
                                                                                            .FlipToBack,
                                                                            onClick =
                                                                                    model::invertSelection,
                                                                    ),
                                                            ),
                                            )
                                        }
                                    },
                                    scrollBehavior = scrollBehavior,
                            )
                        },
                ) { contentPadding ->
                    if (s.items.isEmpty()) {
                        EmptyScreen(
                                message = stringResource(AYMR.strings.database_clean),
                                modifier = Modifier.padding(contentPadding),
                        )
                    } else {
                        Column(
                                modifier = Modifier.padding(contentPadding).fillMaxSize(),
                        ) {
                            LazyColumn(
                                    modifier = Modifier.weight(1f),
                            ) {
                                items(s.items) { sourceWithCount ->
                                    ClearDatabaseItem(
                                            source = sourceWithCount.source,
                                            count = sourceWithCount.count,
                                            isSelected = s.selection.contains(sourceWithCount.id),
                                            onClickSelect = {
                                                model.toggleSelection(
                                                        sourceWithCount.source,
                                                )
                                            },
                                    )
                                }
                            }

                            HorizontalDivider()

                            Button(
                                    modifier =
                                            Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                                                    .fillMaxWidth(),
                                    onClick = model::showConfirmation,
                                    enabled = s.selection.isNotEmpty(),
                            ) {
                                Text(
                                        text = stringResource(AYMR.strings.action_delete),
                                        color = MaterialTheme.colorScheme.onPrimary,
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    @Composable
    private fun ClearDatabaseItem(
            source: AnimeSource,
            count: Long,
            isSelected: Boolean,
            onClickSelect: () -> Unit,
    ) {
        Row(
                modifier =
                        Modifier.selectedBackground(isSelected)
                                .clickable(onClick = onClickSelect)
                                .padding(horizontal = 8.dp)
                                .height(56.dp),
                verticalAlignment = Alignment.CenterVertically,
        ) {
            AnimeSourceIcon(source = source)
            Column(
                    modifier = Modifier.padding(start = 8.dp).weight(1f),
            ) {
                Text(
                        text = source.visualName,
                        style = MaterialTheme.typography.bodyMedium,
                )
                Text(text = stringResource(AYMR.strings.clear_database_source_item_count, count))
            }
            Checkbox(
                    checked = isSelected,
                    onCheckedChange = { onClickSelect() },
            )
        }
    }
}

private class ClearAnimeDatabaseScreenModel :
        StateScreenModel<ClearAnimeDatabaseScreenModel.State>(
                State.Loading,
        ) {
    private val getSourcesWithNonLibraryAnime: GetAnimeSourcesWithNonLibraryAnime = Injekt.get()
    private val database: AnimeDatabase = Injekt.get()
    private val sourceManager: AnimeSourceManager = Injekt.get()

    init {
        screenModelScope.launchIO {
            getSourcesWithNonLibraryAnime.subscribe().collectLatest { list ->
                val items =
                        list.groupBy { it.sourceId }.map { (sourceId, deletableAnime) ->
                            val source = sourceManager.getOrStub(sourceId)
                            val domainSource =
                                    mapSourceToDomainSource(source)
                                            .copy(
                                                    isStub = source is StubAnimeSource,
                                            )

                            val ids = mutableListOf<Long>()
                            val orphaned = mutableListOf<Long>()

                            deletableAnime.forEach {
                                ids.add(it.animeId)
                                if (it.fetchType == FetchType.Seasons) {
                                    val (childrenIds, orphanedIds) =
                                            getDeletableChildren(it.animeId)
                                    ids.addAll(childrenIds)
                                    orphaned.addAll(orphanedIds)
                                }
                            }

                            AnimeSourceWithIds(domainSource, ids, orphaned)
                        }

                mutableState.update { old ->
                    val items = items.sortedBy { it.name }
                    when (old) {
                        State.Loading -> State.Ready(items)
                        is State.Ready -> old.copy(items = items)
                    }
                }
            }
        }
    }

    /**
     * Get all children of an anime that can be deleted, as well as any orphans. Children that are
     * favorited needs their parentId removed or else they won't be able to be removed later.
     */
    private suspend fun getDeletableChildren(animeId: Long): Pair<List<Long>, List<Long>> {
        val ids = mutableListOf<Long>()
        val orphaned = mutableListOf<Long>()
        val children = getSourcesWithNonLibraryAnime.getDeletableChildren(animeId)
        children.forEach { c ->
            if (c.favorite) {
                orphaned.add(c.id)
            } else {
                ids.add(c.id)
                if (c.fetchType == FetchType.Seasons) {
                    val (childrenIds, orphanedIds) = getDeletableChildren(c.id)
                    ids.addAll(childrenIds)
                    orphaned.addAll(orphanedIds)
                }
            }
        }
        return Pair(ids, orphaned)
    }

    suspend fun removeAnimeBySourceId() = withNonCancellableContext {
        val state = state.value as? State.Ready ?: return@withNonCancellableContext
        val selected = state.items.filter { it.id in state.selection }

        val animeIds = selected.flatMap { it.ids }
        val orphaned = selected.flatMap { it.orphaned }.filterNot { it in animeIds }

        database.animesQueries.deleteAnimesNotInLibraryByAnimeIds(animeIds)
        database.animesQueries.removeParentIdByIds(orphaned)
        database.animehistoryQueries.removeResettedHistory()
    }

    fun toggleSelection(source: AnimeSource) =
            mutableState.update { state ->
                if (state !is State.Ready) return@update state
                val mutableList = state.selection.toMutableList()
                if (mutableList.contains(source.id)) {
                    mutableList.remove(source.id)
                } else {
                    mutableList.add(source.id)
                }
                state.copy(selection = mutableList)
            }

    fun clearSelection() =
            mutableState.update { state ->
                if (state !is State.Ready) return@update state
                state.copy(selection = emptyList())
            }

    fun selectAll() =
            mutableState.update { state ->
                if (state !is State.Ready) return@update state
                state.copy(selection = state.items.fastMap { it.id })
            }

    fun invertSelection() =
            mutableState.update { state ->
                if (state !is State.Ready) return@update state
                state.copy(
                        selection =
                                state.items.fastMap { it.id }.filterNot { it in state.selection },
                )
            }

    fun showConfirmation() =
            mutableState.update { state ->
                if (state !is State.Ready) return@update state
                state.copy(showConfirmation = true)
            }

    fun hideConfirmation() =
            mutableState.update { state ->
                if (state !is State.Ready) return@update state
                state.copy(showConfirmation = false)
            }

    sealed interface State {
        @Immutable data object Loading : State

        @Immutable
        data class Ready(
                val items: List<AnimeSourceWithIds>,
                val selection: List<Long> = emptyList(),
                val showConfirmation: Boolean = false,
        ) : State
    }
}
