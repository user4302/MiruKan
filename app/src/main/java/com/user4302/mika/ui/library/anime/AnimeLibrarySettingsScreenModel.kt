package com.user4302.mika.ui.library.anime

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.user4302.domain.base.BasePreferences
import com.user4302.mika.core.common.preference.Preference
import com.user4302.mika.core.common.preference.TriState
import com.user4302.mika.core.common.preference.getAndSet
import com.user4302.mika.core.common.util.lang.launchIO
import com.user4302.mika.data.track.TrackerManager
import com.user4302.mika.domain.category.anime.interactor.SetAnimeDisplayMode
import com.user4302.mika.domain.category.anime.interactor.SetSortModeForAnimeCategory
import com.user4302.mika.domain.category.model.Category
import com.user4302.mika.domain.library.anime.model.AnimeLibrarySort
import com.user4302.mika.domain.library.model.LibraryDisplayMode
import com.user4302.mika.domain.library.service.LibraryPreferences
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import uy.kohesive.injekt.Injekt
import uy.kohesive.injekt.api.get
import kotlin.time.Duration.Companion.seconds

class AnimeLibrarySettingsScreenModel(
    val preferences: BasePreferences = Injekt.get(),
    val libraryPreferences: LibraryPreferences = Injekt.get(),
    private val setAnimeDisplayMode: SetAnimeDisplayMode = Injekt.get(),
    private val setSortModeForCategory: SetSortModeForAnimeCategory = Injekt.get(),
    trackerManager: TrackerManager = Injekt.get(),
) : ScreenModel {

    val trackersFlow = trackerManager.loggedInTrackersFlow()
        .stateIn(
            scope = screenModelScope,
            started = SharingStarted.WhileSubscribed(5.seconds.inWholeMilliseconds),
            initialValue = trackerManager.loggedInTrackers(),
        )

    fun toggleFilter(preference: (LibraryPreferences) -> Preference<TriState>) {
        preference(libraryPreferences).getAndSet {
            it.next()
        }
    }

    fun toggleTracker(id: Int) {
        toggleFilter { libraryPreferences.filterTrackedAnime(id) }
    }

    fun setDisplayMode(mode: LibraryDisplayMode) {
        setAnimeDisplayMode.await(mode)
    }

    fun setSort(
        category: Category?,
        mode: AnimeLibrarySort.Type,
        direction: AnimeLibrarySort.Direction,
    ) {
        screenModelScope.launchIO {
            setSortModeForCategory.await(category, mode, direction)
        }
    }
}
