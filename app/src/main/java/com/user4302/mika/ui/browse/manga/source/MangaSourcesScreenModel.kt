package com.user4302.mika.ui.browse.manga.source

import androidx.compose.runtime.Immutable
import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.user4302.domain.base.BasePreferences
import com.user4302.domain.source.manga.interactor.GetEnabledMangaSources
import com.user4302.domain.source.manga.interactor.ToggleExcludeFromMangaDataSaver
import com.user4302.domain.source.manga.interactor.ToggleMangaSource
import com.user4302.domain.source.manga.interactor.ToggleMangaSourcePin
import com.user4302.domain.source.service.SourcePreferences
import com.user4302.domain.source.service.SourcePreferences.DataSaver
import com.user4302.mika.core.common.util.lang.launchIO
import com.user4302.mika.core.common.util.system.logcat
import com.user4302.mika.domain.source.manga.model.Pin
import com.user4302.mika.domain.source.manga.model.Source
import com.user4302.mika.util.system.LAST_USED_KEY
import com.user4302.mika.util.system.PINNED_KEY
import com.user4302.presentation.browse.manga.MangaSourceUiModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import logcat.LogPriority
import uy.kohesive.injekt.Injekt
import uy.kohesive.injekt.api.get
import java.util.TreeMap

class MangaSourcesScreenModel(
    private val preferences: BasePreferences = Injekt.get(),
    private val sourcePreferences: SourcePreferences = Injekt.get(),
    private val getEnabledSources: GetEnabledMangaSources = Injekt.get(),
    private val toggleSource: ToggleMangaSource = Injekt.get(),
    private val toggleSourcePin: ToggleMangaSourcePin = Injekt.get(),
    // SY -->
    private val toggleExcludeFromMangaDataSaver: ToggleExcludeFromMangaDataSaver = Injekt.get(),
    // SY <--
) : StateScreenModel<MangaSourcesScreenModel.State>(State()) {

    private val _events = Channel<Event>(Int.MAX_VALUE)
    val events = _events.receiveAsFlow()

    init {
        screenModelScope.launchIO {
            getEnabledSources.subscribe()
                .catch {
                    logcat(LogPriority.ERROR, it)
                    _events.send(Event.FailedFetchingSources)
                }
                .collectLatest(::collectLatestSources)
        }
        // SY -->
        sourcePreferences.dataSaver().changes()
            .onEach {
                mutableState.update {
                    it.copy(
                        dataSaverEnabled = sourcePreferences.dataSaver().get() != DataSaver.NONE,
                    )
                }
            }
            .launchIn(screenModelScope)
        // SY <--
    }

    private fun collectLatestSources(sources: List<Source>) {
        mutableState.update { state ->
            val map = TreeMap<String, MutableList<Source>> { d1, d2 ->
                // Sources without a lang defined will be placed at the end
                when {
                    d1 == LAST_USED_KEY && d2 != LAST_USED_KEY -> -1
                    d2 == LAST_USED_KEY && d1 != LAST_USED_KEY -> 1
                    d1 == PINNED_KEY && d2 != PINNED_KEY -> -1
                    d2 == PINNED_KEY && d1 != PINNED_KEY -> 1
                    d1 == "" && d2 != "" -> 1
                    d2 == "" && d1 != "" -> -1
                    else -> d1.compareTo(d2)
                }
            }
            val byLang = sources.groupByTo(map) {
                when {
                    it.isUsedLast -> LAST_USED_KEY
                    Pin.Actual in it.pin -> PINNED_KEY
                    else -> it.lang
                }
            }

            state.copy(
                isLoading = false,
                items = byLang
                    .flatMap {
                        listOf(
                            MangaSourceUiModel.Header(it.key),
                            *it.value.map { source ->
                                MangaSourceUiModel.Item(source)
                            }.toTypedArray(),
                        )
                    }
                    .toImmutableList(),
            )
        }
    }

    fun toggleSource(source: Source) {
        toggleSource.await(source)
    }

    fun togglePin(source: Source) {
        toggleSourcePin.await(source)
    }

    // SY -->
    fun toggleExcludeFromMangaDataSaver(source: Source) {
        toggleExcludeFromMangaDataSaver.await(source)
    }
    // SY <--

    fun showSourceDialog(source: Source) {
        mutableState.update { it.copy(dialog = Dialog(source)) }
    }

    fun closeDialog() {
        mutableState.update { it.copy(dialog = null) }
    }

    sealed interface Event {
        data object FailedFetchingSources : Event
    }

    data class Dialog(val source: Source)

    @Immutable
    data class State(
        val dialog: Dialog? = null,
        val isLoading: Boolean = true,
        val items: ImmutableList<MangaSourceUiModel> = persistentListOf(),
        // SY -->
        val dataSaverEnabled: Boolean = false,
        // SY <--
    ) {
        val isEmpty = items.isEmpty()
    }
}
