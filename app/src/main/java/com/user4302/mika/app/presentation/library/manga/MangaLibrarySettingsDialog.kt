package com.user4302.presentation.library.manga

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
import com.user4302.mika.domain.library.manga.model.MangaLibrarySort
import com.user4302.mika.domain.library.manga.model.sort
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
import com.user4302.mika.ui.library.manga.MangaLibrarySettingsScreenModel
import com.user4302.mika.util.system.isReleaseBuildType
import com.user4302.presentation.components.TabbedDialog
import com.user4302.presentation.components.TabbedDialogPaddings
import kotlinx.collections.immutable.persistentListOf

@Composable
fun MangaLibrarySettingsDialog(
    onDismissRequest: () -> Unit,
    screenModel: MangaLibrarySettingsScreenModel,
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
    screenModel: MangaLibrarySettingsScreenModel,
) {
    val filterDownloaded by screenModel.libraryPreferences.filterDownloadedManga().collectAsState()
    val downloadedOnly by screenModel.preferences.downloadedOnly().collectAsState()
    val autoUpdateMangaRestrictions by screenModel.libraryPreferences.autoUpdateItemRestrictions().collectAsState()

    TriStateItem(
        label = stringResource(AYMR.strings.label_downloaded),
        state = if (downloadedOnly) {
            TriState.ENABLED_IS
        } else {
            filterDownloaded
        },
        enabled = !downloadedOnly,
        onClick = { screenModel.toggleFilter(LibraryPreferences::filterDownloadedManga) },
    )
    val filterUnread by screenModel.libraryPreferences.filterUnread().collectAsState()
    TriStateItem(
        label = stringResource(AYMR.strings.action_filter_unread),
        state = filterUnread,
        onClick = { screenModel.toggleFilter(LibraryPreferences::filterUnread) },
    )
    val filterStarted by screenModel.libraryPreferences.filterStartedManga().collectAsState()
    TriStateItem(
        label = stringResource(AYMR.strings.label_started),
        state = filterStarted,
        onClick = { screenModel.toggleFilter(LibraryPreferences::filterStartedManga) },
    )
    val filterBookmarked by screenModel.libraryPreferences.filterBookmarkedManga().collectAsState()
    TriStateItem(
        label = stringResource(AYMR.strings.action_filter_bookmarked),
        state = filterBookmarked,
        onClick = { screenModel.toggleFilter(LibraryPreferences::filterBookmarkedManga) },
    )
    val filterCompleted by screenModel.libraryPreferences.filterCompletedManga().collectAsState()
    TriStateItem(
        label = stringResource(AYMR.strings.completed),
        state = filterCompleted,
        onClick = { screenModel.toggleFilter(LibraryPreferences::filterCompletedManga) },
    )

    // TODO: re-enable when custom intervals are ready for stable
    if ((!isReleaseBuildType) && LibraryPreferences.ENTRY_OUTSIDE_RELEASE_PERIOD in autoUpdateMangaRestrictions) {
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
            val filterTracker by screenModel.libraryPreferences.filterTrackedManga(
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
                val filterTracker by screenModel.libraryPreferences.filterTrackedManga(
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
    screenModel: MangaLibrarySettingsScreenModel,
) {
    val trackers by screenModel.trackersFlow.collectAsState()
    val sortingMode = category.sort.type
    val sortDescending = !category.sort.isAscending

    val options = remember(trackers.isEmpty()) {
        val trackerMeanPair = if (trackers.isNotEmpty()) {
            AYMR.strings.action_sort_tracker_score to MangaLibrarySort.Type.TrackerMean
        } else {
            null
        }
        listOfNotNull(
            AYMR.strings.action_sort_alpha to MangaLibrarySort.Type.Alphabetical,
            AYMR.strings.action_sort_total to MangaLibrarySort.Type.TotalChapters,
            AYMR.strings.action_sort_last_read to MangaLibrarySort.Type.LastRead,
            AYAYMR.strings.action_sort_last_manga_update to MangaLibrarySort.Type.LastUpdate,
            AYMR.strings.action_sort_unread_count to MangaLibrarySort.Type.UnreadCount,
            AYMR.strings.action_sort_latest_chapter to MangaLibrarySort.Type.LatestChapter,
            AYMR.strings.action_sort_chapter_fetch_date to MangaLibrarySort.Type.ChapterFetchDate,
            AYMR.strings.action_sort_date_added to MangaLibrarySort.Type.DateAdded,
            trackerMeanPair,
            AYMR.strings.action_sort_random to MangaLibrarySort.Type.Random,
        )
    }

    options.map { (titleRes, mode) ->
        if (mode == MangaLibrarySort.Type.Random) {
            BaseSortItem(
                label = stringResource(titleRes),
                icon = Icons.Default.Refresh
                    .takeIf { sortingMode == MangaLibrarySort.Type.Random },
                onClick = {
                    screenModel.setSort(category, mode, MangaLibrarySort.Direction.Ascending)
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
                        MangaLibrarySort.Direction.Ascending
                    } else {
                        MangaLibrarySort.Direction.Descending
                    }
                    else -> if (sortDescending) {
                        MangaLibrarySort.Direction.Descending
                    } else {
                        MangaLibrarySort.Direction.Ascending
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
    screenModel: MangaLibrarySettingsScreenModel,
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
            screenModel.libraryPreferences.mangaLandscapeColumns()
        } else {
            screenModel.libraryPreferences.mangaPortraitColumns()
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
        label = stringResource(AYMR.strings.action_display_download_badge),
        pref = screenModel.libraryPreferences.downloadBadge(),
    )
    CheckboxItem(
        label = stringResource(AYMR.strings.action_display_unread_badge),
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
