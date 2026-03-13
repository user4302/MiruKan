package com.user4302.presentation.entries.anime

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.user4302.domain.base.BasePreferences
import com.user4302.domain.entries.anime.model.downloadedFilter
import com.user4302.mika.core.common.preference.TriState
import com.user4302.mika.domain.entries.anime.model.Anime
import com.user4302.mika.i18n.mika.AYMR
import com.user4302.mika.presentation.core.components.CheckboxItem
import com.user4302.mika.presentation.core.components.LabeledCheckbox
import com.user4302.mika.presentation.core.components.RadioItem
import com.user4302.mika.presentation.core.components.SortItem
import com.user4302.mika.presentation.core.components.TriStateItem
import com.user4302.mika.presentation.core.i18n.stringResource
import com.user4302.presentation.components.TabbedDialog
import com.user4302.presentation.components.TabbedDialogPaddings
import kotlinx.collections.immutable.persistentListOf
import uy.kohesive.injekt.Injekt
import uy.kohesive.injekt.api.get

@Composable
fun EpisodeSettingsDialog(
    onDismissRequest: () -> Unit,
    anime: Anime? = null,
    onDownloadFilterChanged: (TriState) -> Unit,
    onUnseenFilterChanged: (TriState) -> Unit,
    onBookmarkedFilterChanged: (TriState) -> Unit,
    onFillermarkedFilterChanged: (TriState) -> Unit,
    onSortModeChanged: (Long) -> Unit,
    onDisplayModeChanged: (Long) -> Unit,
    onShowPreviewsEnabled: (Long) -> Unit,
    onShowSummariesEnabled: (Long) -> Unit,
    onSetAsDefault: (applyToExistingAnime: Boolean) -> Unit,
) {
    var showSetAsDefaultDialog by rememberSaveable { mutableStateOf(false) }
    if (showSetAsDefaultDialog) {
        SetAsDefaultDialog(
            onDismissRequest = { showSetAsDefaultDialog = false },
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
                    FilterPage(
                        downloadFilter = anime?.downloadedFilter ?: TriState.DISABLED,
                        onDownloadFilterChanged = onDownloadFilterChanged
                            .takeUnless { downloadedOnly },
                        unseenFilter = anime?.unseenFilter ?: TriState.DISABLED,
                        onUnseenFilterChanged = onUnseenFilterChanged,
                        bookmarkedFilter = anime?.bookmarkedFilter ?: TriState.DISABLED,
                        onBookmarkedFilterChanged = onBookmarkedFilterChanged,
                        fillermarkedFilter = anime?.fillermarkedFilter ?: TriState.DISABLED,
                        onFillermarkedFilterChanged = onFillermarkedFilterChanged,
                    )
                }
                1 -> {
                    SortPage(
                        sortingMode = anime?.sorting ?: 0,
                        sortDescending = anime?.sortDescending() ?: false,
                        onItemSelected = onSortModeChanged,
                    )
                }
                2 -> {
                    DisplayPage(
                        displayMode = anime?.displayMode ?: 0,
                        onDisplayModeChanged = onDisplayModeChanged,
                        showPreviews = anime?.showPreviews() ?: true,
                        onShowPreviewsEnabled = onShowPreviewsEnabled,
                        showSummaries = anime?.showSummaries() ?: true,
                        onShowSummariesEnabled = onShowSummariesEnabled,
                    )
                }
            }
        }
    }
}

@Composable
private fun ColumnScope.FilterPage(
    downloadFilter: TriState,
    onDownloadFilterChanged: ((TriState) -> Unit)?,
    unseenFilter: TriState,
    onUnseenFilterChanged: (TriState) -> Unit,
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
private fun ColumnScope.SortPage(
    sortingMode: Long,
    sortDescending: Boolean,
    onItemSelected: (Long) -> Unit,
) {
    listOf(
        AYMR.strings.sort_by_source to Anime.EPISODE_SORTING_SOURCE,
        AYAYMR.strings.sort_by_episode_number to Anime.EPISODE_SORTING_NUMBER,
        AYMR.strings.sort_by_upload_date to Anime.EPISODE_SORTING_UPLOAD_DATE,
        AYMR.strings.action_sort_alpha to Anime.EPISODE_SORTING_ALPHABET,
    ).map { (titleRes, mode) ->
        SortItem(
            label = stringResource(titleRes),
            sortDescending = sortDescending.takeIf { sortingMode == mode },
            onClick = { onItemSelected(mode) },
        )
    }
}

@Composable
private fun ColumnScope.DisplayPage(
    displayMode: Long,
    onDisplayModeChanged: (Long) -> Unit,
    showPreviews: Boolean,
    onShowPreviewsEnabled: (Long) -> Unit,
    showSummaries: Boolean,
    onShowSummariesEnabled: (Long) -> Unit,
) {
    listOf(
        AYMR.strings.show_title to Anime.EPISODE_DISPLAY_NAME,
        AYAYMR.strings.show_episode_number to Anime.EPISODE_DISPLAY_NUMBER,
    ).map { (titleRes, mode) ->
        RadioItem(
            label = stringResource(titleRes),
            selected = displayMode == mode,
            onClick = { onDisplayModeChanged(mode) },
        )
    }
    val showPreviewsFlag = if (showPreviews) Anime.EPISODE_SHOW_NOT_PREVIEWS else Anime.EPISODE_SHOW_PREVIEWS
    CheckboxItem(
        label = stringResource(AYAYMR.strings.show_episode_previews),
        checked = showPreviews,
        onClick = { onShowPreviewsEnabled(showPreviewsFlag) },
    )
    val showSummariesFlag = if (showSummaries) Anime.EPISODE_SHOW_NOT_SUMMARIES else Anime.EPISODE_SHOW_SUMMARIES
    CheckboxItem(
        label = stringResource(AYAYMR.strings.show_episode_summaries),
        checked = showSummaries,
        onClick = { onShowSummariesEnabled(showSummariesFlag) },
    )
}

@Composable
internal fun SetAsDefaultDialog(
    onDismissRequest: () -> Unit,
    isEpisode: Boolean = true,
    onConfirmed: (optionalChecked: Boolean) -> Unit,
) {
    var optionalChecked by rememberSaveable { mutableStateOf(false) }
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = {
            Text(
                text = if (isEpisode) {
                    stringResource(
                        AYAYMR.strings.episode_settings,
                    )
                } else {
                    stringResource(AYAYMR.strings.season_settings)
                },
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Text(text = stringResource(AYMR.strings.confirm_set_chapter_settings))

                LabeledCheckbox(
                    label = stringResource(AYAYMR.strings.also_set_episode_settings_for_library),
                    checked = optionalChecked,
                    onCheckedChange = { optionalChecked = it },
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text(text = stringResource(AYMR.strings.action_cancel))
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirmed(optionalChecked)
                    onDismissRequest()
                },
            ) {
                Text(text = stringResource(AYMR.strings.action_ok))
            }
        },
    )
}
