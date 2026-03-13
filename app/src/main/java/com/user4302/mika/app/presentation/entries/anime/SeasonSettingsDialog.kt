package com.user4302.presentation.entries.anime

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.user4302.domain.base.BasePreferences
import com.user4302.domain.entries.anime.model.seasonDownloadedFilter
import com.user4302.mika.core.common.preference.TriState
import com.user4302.mika.domain.anime.SeasonDisplayMode
import com.user4302.mika.domain.entries.anime.model.Anime
import com.user4302.mika.i18n.mika.AYMR
import com.user4302.mika.presentation.core.components.HeadingItem
import com.user4302.mika.presentation.core.components.LabeledCheckbox
import com.user4302.mika.presentation.core.components.RadioItem
import com.user4302.mika.presentation.core.components.SettingsChipRow
import com.user4302.mika.presentation.core.components.SettingsItemsPaddings
import com.user4302.mika.presentation.core.components.SliderItem
import com.user4302.mika.presentation.core.components.SortItem
import com.user4302.mika.presentation.core.components.TriStateItem
import com.user4302.mika.presentation.core.i18n.stringResource
import com.user4302.presentation.components.TabbedDialog
import com.user4302.presentation.components.TabbedDialogPaddings
import kotlinx.collections.immutable.persistentListOf
import uy.kohesive.injekt.Injekt
import uy.kohesive.injekt.api.get

@Composable
fun SeasonSettingsDialog(
    onDismissRequest: () -> Unit,
    anime: Anime? = null,

    // Filter page
    onDownloadFilterChanged: (TriState) -> Unit,
    onUnseenFilterChanged: (TriState) -> Unit,
    onStartedFilterChanged: (TriState) -> Unit,
    onCompletedFilterChanged: (TriState) -> Unit,
    onBookmarkedFilterChanged: (TriState) -> Unit,
    onFillermarkedFilterChanged: (TriState) -> Unit,

    // Sort page
    onSortModeChanged: (Long) -> Unit,

    // Display page
    onDisplayGridModeChanged: (SeasonDisplayMode) -> Unit,
    onDisplayGridSizeChanged: (Int) -> Unit,
    onOverlayDownloadedChanged: (Boolean) -> Unit,
    onOverlayUnseenChanged: (Boolean) -> Unit,
    onOverlayLocalChanged: (Boolean) -> Unit,
    onOverlayLangChanged: (Boolean) -> Unit,
    onOverlayContinueChanged: (Boolean) -> Unit,
    onDisplayModeChanged: (Long) -> Unit,

    // Overflow action
    onSetAsDefault: (applyToExistingAnime: Boolean) -> Unit,
) {
    var showSetAsDefaultDialog by rememberSaveable { mutableStateOf(false) }
    if (showSetAsDefaultDialog) {
        SetAsDefaultDialog(
            onDismissRequest = { showSetAsDefaultDialog = false },
            isEpisode = false,
            onConfirmed = onSetAsDefault,
        )
    }

    val downloadedOnly = remember { Injekt.get<BasePreferences>().downloadedOnly().get() }

    TabbedDialog(
        onDismissRequest = onDismissRequest,
        tabTitles = persistentListOf(
            stringResource(AYMR.strings.action_filter),
            stringResource(AYMR.strings.action_sort),
            stringResource(AYMR.strings.action_display),
        ),
        tabOverflowMenuContent = { closeMenu ->
            DropdownMenuItem(
                text = { Text(stringResource(AYMR.strings.set_chapter_settings_as_default)) },
                onClick = {
                    showSetAsDefaultDialog = true
                    closeMenu()
                },
            )
        },
    ) { page ->
        Column(
            modifier = Modifier
                .padding(vertical = TabbedDialogPaddings.Vertical)
                .verticalScroll(rememberScrollState()),
        ) {
            when (page) {
                0 -> {
                    SeasonFilterPage(
                        downloadFilter = anime?.seasonDownloadedFilter ?: TriState.DISABLED,
                        onDownloadFilterChanged = onDownloadFilterChanged
                            .takeUnless { downloadedOnly },
                        unseenFilter = anime?.seasonUnseenFilter ?: TriState.DISABLED,
                        onUnseenFilterChanged = onUnseenFilterChanged,
                        startedFilter = anime?.seasonStartedFilter ?: TriState.DISABLED,
                        onStartedFilterChanged = onStartedFilterChanged,
                        completedFilter = anime?.seasonCompletedFilter ?: TriState.DISABLED,
                        onCompletedFilterChanged = onCompletedFilterChanged,
                        bookmarkedFilter = anime?.seasonBookmarkedFilter ?: TriState.DISABLED,
                        onBookmarkedFilterChanged = onBookmarkedFilterChanged,
                        fillermarkedFilter = anime?.seasonFillermarkedFilter ?: TriState.DISABLED,
                        onFillermarkedFilterChanged = onFillermarkedFilterChanged,
                    )
                }
                1 -> {
                    SeasonSortPage(
                        sortingMode = anime?.seasonSorting ?: 0,
                        sortDescending = anime?.seasonSortDescending() ?: false,
                        onItemSelected = onSortModeChanged,
                    )
                }
                2 -> {
                    SeasonDisplayPage(
                        displayGridMode = anime?.seasonDisplayGridMode ?: SeasonDisplayMode.CompactGrid,
                        displayGridModeChange = onDisplayGridModeChanged,
                        displayGridModeSize = anime?.seasonDisplayGridSize ?: 0,
                        displayGridModeSizeChange = onDisplayGridSizeChanged,
                        overlayDownloaded = anime?.seasonDownloadedOverlay ?: false,
                        overlayDownloadedChange = onOverlayDownloadedChanged,
                        overlayUnseen = anime?.seasonUnseenOverlay ?: true,
                        overlayUnseenChange = onOverlayUnseenChanged,
                        overlayLocal = anime?.seasonLocalOverlay ?: true,
                        overlayLocalChange = onOverlayLocalChanged,
                        overlayLang = anime?.seasonLangOverlay ?: false,
                        overlayLangChange = onOverlayLangChanged,
                        overlayContinue = anime?.seasonContinueOverlay ?: true,
                        overlayContinueChange = onOverlayContinueChanged,
                        displayMode = anime?.seasonDisplayMode ?: 0L,
                        displayModeChange = onDisplayModeChanged,
                    )
                }
            }
        }
    }
}

@Composable
private fun ColumnScope.SeasonFilterPage(
    downloadFilter: TriState,
    onDownloadFilterChanged: ((TriState) -> Unit)?,
    unseenFilter: TriState,
    onUnseenFilterChanged: (TriState) -> Unit,
    startedFilter: TriState,
    onStartedFilterChanged: (TriState) -> Unit,
    completedFilter: TriState,
    onCompletedFilterChanged: (TriState) -> Unit,
    bookmarkedFilter: TriState,
    onBookmarkedFilterChanged: (TriState) -> Unit,
    fillermarkedFilter: TriState,
    onFillermarkedFilterChanged: (TriState) -> Unit,
) {
    TriStateItem(
        label = stringResource(AYMR.strings.label_downloaded),
        state = downloadFilter,
        onClick = onDownloadFilterChanged,
    )
    TriStateItem(
        label = stringResource(AYAYMR.strings.action_filter_unseen),
        state = unseenFilter,
        onClick = onUnseenFilterChanged,
    )
    TriStateItem(
        label = stringResource(AYMR.strings.label_started),
        state = startedFilter,
        onClick = onStartedFilterChanged,
    )
    TriStateItem(
        label = stringResource(AYMR.strings.completed),
        state = completedFilter,
        onClick = onCompletedFilterChanged,
    )
    TriStateItem(
        label = stringResource(AYMR.strings.action_filter_bookmarked),
        state = bookmarkedFilter,
        onClick = onBookmarkedFilterChanged,
    )
    TriStateItem(
        label = stringResource(AYAYMR.strings.action_filter_fillermarked),
        state = fillermarkedFilter,
        onClick = onFillermarkedFilterChanged,
    )
}

@Composable
private fun ColumnScope.SeasonSortPage(
    sortingMode: Long,
    sortDescending: Boolean,
    onItemSelected: (Long) -> Unit,
) {
    listOf(
        AYMR.strings.sort_by_source to Anime.SEASON_SORT_SOURCE,
        AYAYMR.strings.sort_by_season_number to Anime.SEASON_SORT_SEASON,
        AYMR.strings.sort_by_upload_date to Anime.SEASON_SORT_UPLOAD,
        AYMR.strings.action_sort_alpha to Anime.SEASON_SORT_ALPHABET,
        AYAYMR.strings.action_sort_unseen_count to Anime.SEASON_SORT_COUNT,
        AYAYMR.strings.action_sort_last_seen to Anime.SEASON_SORT_LAST_SEEN,
        AYAYMR.strings.action_sort_episode_fetch_date to Anime.SEASON_SORT_FETCHED,
    ).map { (titleRes, mode) ->
        SortItem(
            label = stringResource(titleRes),
            sortDescending = sortDescending.takeIf { sortingMode == mode },
            onClick = { onItemSelected(mode) },
        )
    }
}

private val displayModes = listOf(
    AYMR.strings.action_display_grid to SeasonDisplayMode.CompactGrid,
    AYMR.strings.action_display_comfortable_grid to SeasonDisplayMode.ComfortableGrid,
    AYMR.strings.action_display_cover_only_grid to SeasonDisplayMode.CoverOnlyGrid,
    AYMR.strings.action_display_list to SeasonDisplayMode.List,
)

@Composable
private fun ColumnScope.SeasonDisplayPage(
    displayGridMode: SeasonDisplayMode,
    displayGridModeChange: (SeasonDisplayMode) -> Unit,
    displayGridModeSize: Int,
    displayGridModeSizeChange: (Int) -> Unit,
    overlayDownloaded: Boolean,
    overlayDownloadedChange: (Boolean) -> Unit,
    overlayUnseen: Boolean,
    overlayUnseenChange: (Boolean) -> Unit,
    overlayLocal: Boolean,
    overlayLocalChange: (Boolean) -> Unit,
    overlayLang: Boolean,
    overlayLangChange: (Boolean) -> Unit,
    overlayContinue: Boolean,
    overlayContinueChange: (Boolean) -> Unit,
    displayMode: Long,
    displayModeChange: (Long) -> Unit,
) {
    SettingsChipRow(AYMR.strings.action_display_mode) {
        displayModes.map { (titleRes, mode) ->
            FilterChip(
                selected = displayGridMode == mode,
                onClick = { displayGridModeChange(mode) },
                label = { Text(stringResource(titleRes)) },
            )
        }
    }

    if (displayGridMode == SeasonDisplayMode.List) {
        SliderItem(
            value = displayGridModeSize,
            valueRange = 0..10,
            label = stringResource(AYAYMR.strings.pref_library_rows),
            valueText = if (displayGridModeSize > 0) {
                displayGridModeSize.toString()
            } else {
                stringResource(AYMR.strings.label_auto)
            },
            onChange = { displayGridModeSizeChange(it) },
            pillColor = MaterialTheme.colorScheme.surfaceContainerHighest,
        )
    } else {
        SliderItem(
            value = displayGridModeSize,
            valueRange = 0..10,
            label = stringResource(AYMR.strings.pref_library_columns),
            valueText = if (displayGridModeSize > 0) {
                displayGridModeSize.toString()
            } else {
                stringResource(AYMR.strings.label_auto)
            },
            onChange = { displayGridModeSizeChange(it) },
            pillColor = MaterialTheme.colorScheme.surfaceContainerHighest,
        )
    }

    HeadingItem(AYMR.strings.overlay_header)
    LabeledCheckbox(
        label = stringResource(AYAYMR.strings.action_display_download_badge_anime),
        checked = overlayDownloaded,
        onCheckedChange = overlayDownloadedChange,
        modifier = Modifier.padding(
            horizontal = SettingsItemsPaddings.Horizontal,
        ),
    )
    LabeledCheckbox(
        label = stringResource(AYAYMR.strings.action_display_unseen_badge),
        checked = overlayUnseen,
        onCheckedChange = overlayUnseenChange,
        modifier = Modifier.padding(
            horizontal = SettingsItemsPaddings.Horizontal,
        ),
    )
    LabeledCheckbox(
        label = stringResource(AYMR.strings.action_display_local_badge),
        checked = overlayLocal,
        onCheckedChange = overlayLocalChange,
        modifier = Modifier.padding(
            horizontal = SettingsItemsPaddings.Horizontal,
        ),
    )
    LabeledCheckbox(
        label = stringResource(AYMR.strings.action_display_language_badge),
        checked = overlayLang,
        onCheckedChange = overlayLangChange,
        modifier = Modifier.padding(
            horizontal = SettingsItemsPaddings.Horizontal,
        ),
    )
    LabeledCheckbox(
        label = stringResource(AYAYMR.strings.action_display_show_continue_watching_button),
        checked = overlayContinue,
        onCheckedChange = overlayContinueChange,
        modifier = Modifier.padding(
            horizontal = SettingsItemsPaddings.Horizontal,
        ),
    )

    HeadingItem(AYAYMR.strings.action_display_grid_mode)
    listOf(
        AYMR.strings.show_title to Anime.SEASON_DISPLAY_MODE_SOURCE,
        AYAYMR.strings.show_season_number to Anime.SEASON_DISPLAY_MODE_NUMBER,
    ).map { (titleRes, mode) ->
        RadioItem(
            label = stringResource(titleRes),
            selected = displayMode == mode,
            onClick = { displayModeChange(mode) },
        )
    }
}
