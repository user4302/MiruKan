package com.user4302.presentation.more.stats

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CollectionsBookmark
import androidx.compose.material.icons.outlined.LocalLibrary
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.user4302.mika.i18n.mika.AYMR
import com.user4302.mika.presentation.core.components.SectionCard
import com.user4302.mika.presentation.core.components.material.padding
import com.user4302.mika.presentation.core.i18n.stringResource
import com.user4302.presentation.more.stats.components.StatsItem
import com.user4302.presentation.more.stats.components.StatsOverviewItem
import com.user4302.presentation.more.stats.data.StatsData
import com.user4302.presentation.util.toDurationString
import java.util.Locale
import kotlin.time.DurationUnit
import kotlin.time.toDuration

@Composable
fun AnimeStatsScreenContent(
    state: StatsScreenState.SuccessAnime,
    paddingValues: PaddingValues,
) {
    val statListState = rememberLazyListState()
    LazyColumn(
        state = statListState,
        contentPadding = paddingValues,
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.padding.small),
    ) {
        item {
            OverviewSection(state.overview)
        }
        item {
            TitlesStats(state.titles)
        }
        item {
            EpisodeStats(state.episodes)
        }
        item {
            TrackerStats(state.trackers)
        }
    }
}

@Composable
private fun LazyItemScope.OverviewSection(
    data: StatsData.AnimeOverview,
) {
    val none = stringResource(AYMR.strings.none)
    val context = LocalContext.current
    val readDurationString = remember(data.totalSeenDuration) {
        data.totalSeenDuration
            .toDuration(DurationUnit.MILLISECONDS)
            .toDurationString(context, fallback = none)
    }
    SectionCard(AYMR.strings.label_overview_section) {
        Row(
            modifier = Modifier.height(IntrinsicSize.Min),
        ) {
            StatsOverviewItem(
                title = data.libraryAnimeCount.toString(),
                subtitle = stringResource(AYMR.strings.in_library),
                icon = Icons.Outlined.CollectionsBookmark,
            )
            StatsOverviewItem(
                title = readDurationString,
                subtitle = stringResource(AYAYMR.strings.label_watched_duration),
                icon = Icons.Outlined.Schedule,
            )
            StatsOverviewItem(
                title = data.completedAnimeCount.toString(),
                subtitle = stringResource(AYMR.strings.label_completed_titles),
                icon = Icons.Outlined.LocalLibrary,
            )
        }
    }
}

@Composable
private fun LazyItemScope.TitlesStats(
    data: StatsData.AnimeTitles,
) {
    SectionCard(AYMR.strings.label_titles_section) {
        Row {
            StatsItem(
                data.globalUpdateItemCount.toString(),
                stringResource(AYMR.strings.label_titles_in_global_update),
            )
            StatsItem(
                data.startedAnimeCount.toString(),
                stringResource(AYMR.strings.label_started),
            )
            StatsItem(
                data.localAnimeCount.toString(),
                stringResource(AYMR.strings.label_local),
            )
        }
    }
}

@Composable
private fun LazyItemScope.EpisodeStats(
    data: StatsData.Episodes,
) {
    SectionCard(AYAYMR.strings.episodes) {
        Row {
            StatsItem(
                data.totalEpisodeCount.toString(),
                stringResource(AYMR.strings.label_total_chapters),
            )
            StatsItem(
                data.readEpisodeCount.toString(),
                stringResource(AYAYMR.strings.label_watched_episodes),
            )
            StatsItem(
                data.downloadCount.toString(),
                stringResource(AYMR.strings.label_downloaded),
            )
        }
    }
}

@Composable
private fun LazyItemScope.TrackerStats(
    data: StatsData.Trackers,
) {
    val notApplicable = stringResource(AYMR.strings.not_applicable)
    val meanScoreStr = remember(data.trackedTitleCount, data.meanScore) {
        if (data.trackedTitleCount > 0 && !data.meanScore.isNaN()) {
            // All other numbers are localized in English
            "%.2f ★".format(Locale.ENGLISH, data.meanScore)
        } else {
            notApplicable
        }
    }
    SectionCard(AYMR.strings.label_tracker_section) {
        Row {
            StatsItem(
                data.trackedTitleCount.toString(),
                stringResource(AYMR.strings.label_tracked_titles),
            )
            StatsItem(
                meanScoreStr,
                stringResource(AYMR.strings.label_mean_score),
            )
            StatsItem(
                data.trackerCount.toString(),
                stringResource(AYMR.strings.label_used),
            )
        }
    }
}
