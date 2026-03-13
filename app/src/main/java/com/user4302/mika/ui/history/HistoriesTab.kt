package com.user4302.mika.ui.history

import androidx.compose.animation.graphics.res.animatedVectorResource
import androidx.compose.animation.graphics.res.rememberAnimatedVectorPainter
import androidx.compose.animation.graphics.vector.AnimatedImageVector
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.tab.LocalTabNavigator
import cafe.adriel.voyager.navigator.tab.TabOptions
import com.user4302.domain.ui.model.NavStyle
import com.user4302.mika.R
import com.user4302.mika.i18n.MR
import com.user4302.mika.presentation.core.i18n.stringResource
import com.user4302.mika.ui.history.anime.AnimeHistoryScreenModel
import com.user4302.mika.ui.history.anime.animeHistoryTab
import com.user4302.mika.ui.history.anime.resumeLastEpisodeSeenEvent
import com.user4302.mika.ui.history.manga.MangaHistoryScreenModel
import com.user4302.mika.ui.history.manga.mangaHistoryTab
import com.user4302.mika.ui.main.MainActivity
import com.user4302.presentation.components.TabbedScreen
import com.user4302.presentation.util.Tab
import kotlinx.collections.immutable.persistentListOf

data object HistoriesTab : Tab {

    override val options: TabOptions
        @Composable
        get() {
            val isSelected = LocalTabNavigator.current.current.key == key
            val image = AnimatedImageVector.animatedVectorResource(R.drawable.anim_history_enter)
            val index: UShort = when (currentNavigationStyle()) {
                NavStyle.MOVE_HISTORY_TO_MORE -> 5u
                NavStyle.MOVE_BROWSE_TO_MORE -> 3u
                else -> 2u
            }
            return TabOptions(
                index = index,
                title = stringResource(MR.strings.history),
                icon = rememberAnimatedVectorPainter(image, isSelected),
            )
        }

    override suspend fun onReselect(navigator: Navigator) {
        resumeLastEpisodeSeenEvent.send(Unit)
    }

    @Composable
    override fun Content() {
        val context = LocalContext.current
        val fromMore = currentNavigationStyle() == NavStyle.MOVE_HISTORY_TO_MORE
        // Hoisted for history tab's search bar
        val mangaHistoryScreenModel = rememberScreenModel { MangaHistoryScreenModel() }
        val mangaSearchQuery by mangaHistoryScreenModel.query.collectAsState()

        val animeHistoryScreenModel = rememberScreenModel { AnimeHistoryScreenModel() }
        val animeSearchQuery by animeHistoryScreenModel.query.collectAsState()

        TabbedScreen(
            titleRes = MR.strings.label_recent_manga,
            tabs = persistentListOf(
                animeHistoryTab(context, fromMore),
                mangaHistoryTab(context, fromMore),
            ),
            mangaSearchQuery = mangaSearchQuery,
            onChangeMangaSearchQuery = mangaHistoryScreenModel::search,
            animeSearchQuery = animeSearchQuery,
            onChangeAnimeSearchQuery = animeHistoryScreenModel::search,
        )

        LaunchedEffect(Unit) {
            (context as? MainActivity)?.ready = true
        }
    }
}

private const val TAB_ANIME = 0
private const val TAB_MANGA = 1
