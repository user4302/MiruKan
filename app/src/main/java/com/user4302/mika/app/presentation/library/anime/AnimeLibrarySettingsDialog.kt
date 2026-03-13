package com.user4302.presentation.library.anime

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import com.user4302.mika.core.common.preference.TriState
import com.user4302.mika.domain.category.model.Category
import com.user4302.mika.domain.library.anime.model.AnimeLibrarySort
import com.user4302.mika.domain.library.anime.model.sort
import com.user4302.mika.domain.library.model.LibraryDisplayMode
import com.user4302.mika.domain.library.service.LibraryPreferences
import com.user4302.mika.i18n.mika.AYMR
import com.user4302.mika.presentation.core.components.BaseSortItem
import com.user4302.mika.presentation.core.components.CheckboxItem
import com.user4302.mika.presentation.core.components.HeadingItem
import com.user4302.mika.presentation.core.components.SettingsChipRow
import com.user4302.mika.presentation.core.components.SliderItem
import com.user4302.mika.presentation.core.components.SortItem
import com.user4302.mika.presentation.core.components.TriStateItem
import com.user4302.mika.presentation.core.i18n.stringResource
import com.user4302.mika.presentation.core.util.collectAsState
import com.user4302.mika.ui.library.anime.AnimeLibrarySettingsScreenModel
import com.user4302.mika.util.system.isReleaseBuildType
import com.user4302.presentation.components.TabbedDialog
import com.user4302.presentation.components.TabbedDialogPaddings
import kotlinx.collections.immutable.persistentListOf

@Composable
fun AnimeLibrarySettingsDialog(
    onDismissRequest: () -> Unit,
    screenModel: AnimeLibrarySettingsScreenModel,
    category: Category?,
) {
    TabbedDialog(
        onDismissRequest = onDismissRequest,
        tabTitles = persistentListOf(
            stringResource(AYMR.strings.action_filter),
            stringResource(AYMR.strings.action_sort),
            stringResource(AYMR.strings.action_display),
        ),
    ) { page ->
        Column(
            modifier = Modifier
                .padding(vertical = TabbedDialogPaddings.Vertical)
                .verticalScroll(rememberScrollState()),
        ) {
            when (page) {
                0 -> FilterPage(
                    screenModel = screenModel,
                )
                1 -> SortPage(
                    category = category,
                    screenModel = screenModel,
                )
                2 -> DisplayPage(
                    screenModel = screenModel,
                )
            }
        }
    }
}

@Composable
private fun ColumnScope.FilterPage(
    screenModel: AnimeLibrarySettingsScreenModel,
) {
    val filterDownloaded by screenModel.libraryPreferences.filterDownloadedAnime().collectAsState()
    val downloadedOnly by screenModel.preferences.downloadedOnly().collectAsState()
    val autoUpdateAnimeRestrictions by screenModel.libraryPreferences.autoUpdateItemRestrictions().collectAsState()

    TriStateItem(
        label = stringResource(AYMR.strings.label_downloaded),
        state = if (downloadedOnly) {
            TriState.ENABLED_IS
        } else {
            filterDownloaded
        },
        enabled = !downloadedOnly,
        onClick = { screenModel.toggleFilter(LibraryPreferences::filterDownloadedAnime) },
    )
    val filterUnseen by screenModel.libraryPreferences.filterUnseen().collectAsState()
    TriStateItem(
        label = stringResource(AYAYMR.strings.action_filter_unseen),
        state = filterUnseen,
        onClick = { screenModel.toggleFilter(LibraryPreferences::filterUnseen) },
    )
    val filterStarted by screenModel.libraryPreferences.filterStartedAnime().collectAsState()
    TriStateItem(
        label = stringResource(AYMR.strings.label_started),
        state = filterStarted,
        onClick = { screenModel.toggleFilter(LibraryPreferences::filterStartedAnime) },
    )
    val filterBookmarked by screenModel.libraryPreferences.filterBookmarkedAnime().collectAsState()
    TriStateItem(
        label = stringResource(AYMR.strings.action_filter_bookmarked),
        state = filterBookmarked,
        onClick = { screenModel.toggleFilter(LibraryPreferences::filterBookmarkedAnime) },
    )
    val filterCompleted by screenModel.libraryPreferences.filterCompletedAnime().collectAsState()
    TriStateItem(
        label = stringResource(AYMR.strings.completed),
        state = filterCompleted,
        onClick = { screenModel.toggleFilter(LibraryPreferences::filterCompletedAnime) },
    )
    // TODO: re-enable when custom intervals are ready for stable
    if ((!isReleaseBuildType) && LibraryPreferences.ENTRY_OUTSIDE_RELEASE_PERIOD in autoUpdateAnimeRestrictions) {
        val filterIntervalCustom by screenModel.libraryPreferences.filterIntervalCustom().collectAsState()
        TriStateItem(
            label = stringResource(AYMR.strings.action_filter_interval_custom),
            state = filterIntervalCustom,
            onClick = { screenModel.toggleFilter(LibraryPreferences::filterIntervalCustom) },
        )
    }

    val trackers by screenModel.trackersFlow.collectAsState()
    when (trackers.size) {
        0 -> {
            // No trackers
        }
        1 -> {
            val service = trackers[0]
            val filterTracker by screenModel.libraryPreferences.filterTrackedAnime(
                service.id.toInt(),
            ).collectAsState()
            TriStateItem(
                label = stringResource(AYMR.strings.action_filter_tracked),
                state = filterTracker,
                onClick = { screenModel.toggleTracker(service.id.toInt()) },
            )
        }
        else -> {
            HeadingItem(AYMR.strings.action_filter_tracked)
            trackers.map { service ->
                val filterTracker by screenModel.libraryPreferences.filterTrackedAnime(
                    service.id.toInt(),
                ).collectAsState()
                TriStateItem(
                    label = service.name,
                    state = filterTracker,
                    onClick = { screenModel.toggleTracker(service.id.toInt()) },
                )
            }
        }
    }
}

@Composable
private fun ColumnScope.SortPage(
    category: Category?,
    screenModel: AnimeLibrarySettingsScreenModel,
) {
    val trackers by screenModel.trackersFlow.collectAsState()
    val sortingMode = category.sort.type
    val sortDescending = !category.sort.isAscending

    val options = remember(trackers.isEmpty()) {
        val trackerMeanPair = if (trackers.isNotEmpty()) {
            AYMR.strings.action_sort_tracker_score to AnimeLibrarySort.Type.TrackerMean
        } else {
            null
        }
        listOfNotNull(
            AYMR.strings.action_sort_alpha to AnimeLibrarySort.Type.Alphabetical,
            AYAYMR.strings.action_sort_total_episodes to AnimeLibrarySort.Type.TotalEpisodes,
            AYAYMR.strings.action_sort_last_seen to AnimeLibrarySort.Type.LastSeen,
            AYAYMR.strings.action_sort_last_anime_update to AnimeLibrarySort.Type.LastUpdate,
            AYAYMR.strings.action_sort_unseen_count to AnimeLibrarySort.Type.UnseenCount,
            AYAYMR.strings.action_sort_latest_episode to AnimeLibrarySort.Type.LatestEpisode,
            AYAYMR.strings.action_sort_episode_fetch_date to AnimeLibrarySort.Type.EpisodeFetchDate,
            AYMR.strings.action_sort_date_added to AnimeLibrarySort.Type.DateAdded,
            trackerMeanPair,
            AYAYMR.strings.action_sort_airing_time to AnimeLibrarySort.Type.AiringTime,
            AYMR.strings.action_sort_random to AnimeLibrarySort.Type.Random,
        )
    }

    options.map { (titleRes, mode) ->
        if (mode == AnimeLibrarySort.Type.Random) {
            BaseSortItem(
                label = stringResource(titleRes),
                icon = Icons.Default.Refresh
                    .takeIf { sortingMode == AnimeLibrarySort.Type.Random },
                onClick = {
                    screenModel.setSort(category, mode, AnimeLibrarySort.Direction.Ascending)
                },
            )
            return@map
        }
        SortItem(
            label = stringResource(titleRes),
            sortDescending = sortDescending.takeIf { sortingMode == mode },
            onClick = {
                val isTogglingDirection = sortingMode == mode
                val direction = when {
                    isTogglingDirection -> if (sortDescending) {
                        AnimeLibrarySort.Direction.Ascending
                    } else {
                        AnimeLibrarySort.Direction.Descending
                    }
                    else -> if (sortDescending) {
                        AnimeLibrarySort.Direction.Descending
                    } else {
                        AnimeLibrarySort.Direction.Ascending
                    }
                }
                screenModel.setSort(category, mode, direction)
            },
        )
    }
}

private val displayModes = listOf(
    AYMR.strings.action_display_grid to LibraryDisplayMode.CompactGrid,
    AYMR.strings.action_display_comfortable_grid to LibraryDisplayMode.ComfortableGrid,
    AYMR.strings.action_display_cover_only_grid to LibraryDisplayMode.CoverOnlyGrid,
    AYMR.strings.action_display_list to LibraryDisplayMode.List,
)

@Composable
private fun ColumnScope.DisplayPage(
    screenModel: AnimeLibrarySettingsScreenModel,
) {
    val displayMode by screenModel.libraryPreferences.displayMode().collectAsState()
    SettingsChipRow(AYMR.strings.action_display_mode) {
        displayModes.map { (titleRes, mode) ->
            FilterChip(
                selected = displayMode == mode,
                onClick = { screenModel.setDisplayMode(mode) },
                label = { Text(stringResource(titleRes)) },
            )
        }
    }

    val configuration = LocalConfiguration.current
    val columnPreference = remember {
        if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            screenModel.libraryPreferences.animeLandscapeColumns()
        } else {
            screenModel.libraryPreferences.animePortraitColumns()
        }
    }

    val columns by columnPreference.collectAsState()
    if (displayMode == LibraryDisplayMode.List) {
        SliderItem(
            value = columns,
            valueRange = 0..10,
            label = stringResource(AYAYMR.strings.pref_library_rows),
            valueText = if (columns > 0) {
                columns.toString()
            } else {
                stringResource(AYMR.strings.label_auto)
            },
            onChange = columnPreference::set,
            pillColor = MaterialTheme.colorScheme.surfaceContainerHighest,
        )
    } else {
        SliderItem(
            value = columns,
            valueRange = 0..10,
            label = stringResource(AYMR.strings.pref_library_columns),
            valueText = if (columns > 0) {
                columns.toString()
            } else {
                stringResource(AYMR.strings.label_auto)
            },
            onChange = columnPreference::set,
            pillColor = MaterialTheme.colorScheme.surfaceContainerHighest,
        )
    }

    HeadingItem(AYMR.strings.overlay_header)
    CheckboxItem(
        label = stringResource(AYAYMR.strings.action_display_download_badge_anime),
        pref = screenModel.libraryPreferences.downloadBadge(),
    )
    CheckboxItem(
        label = stringResource(AYAYMR.strings.action_display_unseen_badge),
        pref = screenModel.libraryPreferences.unreadBadge(),
    )
    CheckboxItem(
        label = stringResource(AYMR.strings.action_display_local_badge),
        pref = screenModel.libraryPreferences.localBadge(),
    )
    CheckboxItem(
        label = stringResource(AYMR.strings.action_display_language_badge),
        pref = screenModel.libraryPreferences.languageBadge(),
    )
    CheckboxItem(
        label = stringResource(AYAYMR.strings.action_display_show_continue_reading_button),
        pref = screenModel.libraryPreferences.showContinueViewingButton(),
    )

    HeadingItem(AYMR.strings.tabs_header)
    CheckboxItem(
        label = stringResource(AYMR.strings.action_display_show_tabs),
        pref = screenModel.libraryPreferences.categoryTabs(),
    )
    CheckboxItem(
        label = stringResource(AYMR.strings.action_display_show_number_of_items),
        pref = screenModel.libraryPreferences.categoryNumberOfItems(),
    )
}
