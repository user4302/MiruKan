package com.user4302.mika.ui.browse

import androidx.compose.animation.graphics.res.animatedVectorResource
import androidx.compose.animation.graphics.res.rememberAnimatedVectorPainter
import androidx.compose.animation.graphics.vector.AnimatedImageVector
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.tab.LocalTabNavigator
import cafe.adriel.voyager.navigator.tab.TabOptions
import com.user4302.mika.R
import com.user4302.mika.i18n.MR
import com.user4302.mika.presentation.core.i18n.stringResource
import com.user4302.mika.ui.browse.anime.extension.AnimeExtensionsScreenModel
import com.user4302.mika.ui.browse.anime.extension.animeExtensionsTab
import com.user4302.mika.ui.browse.anime.migration.sources.migrateAnimeSourceTab
import com.user4302.mika.ui.browse.anime.source.animeSourcesTab
import com.user4302.mika.ui.browse.anime.source.globalsearch.GlobalAnimeSearchScreen
import com.user4302.mika.ui.browse.manga.extension.MangaExtensionsScreenModel
import com.user4302.mika.ui.browse.manga.extension.mangaExtensionsTab
import com.user4302.mika.ui.browse.manga.migration.sources.migrateMangaSourceTab
import com.user4302.mika.ui.browse.manga.source.mangaSourcesTab
import com.user4302.mika.ui.main.MainActivity
import com.user4302.presentation.components.TabbedScreen
import com.user4302.presentation.util.Tab
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.receiveAsFlow

data object BrowseTab : Tab {

    override val options: TabOptions
        @Composable
        get() {
            val isSelected = LocalTabNavigator.current.current is BrowseTab
            val image = AnimatedImageVector.animatedVectorResource(R.drawable.anim_browse_enter)
            return TabOptions(
                index = 3u,
                title = stringResource(MR.strings.browse),
                icon = rememberAnimatedVectorPainter(image, isSelected),
            )
        }

    // TODO: Find a way to let it open Global Anime/Manga Search depending on what Tab(e.g. Anime/Manga Source Tab) is open
    override suspend fun onReselect(navigator: Navigator) {
        navigator.push(GlobalAnimeSearchScreen())
    }

    private val switchToTabNumberChannel = Channel<Int>(1, BufferOverflow.DROP_OLDEST)

    fun showExtension() {
        switchToTabNumberChannel.trySend(3) // Manga extensions: tab no. 3
    }

    fun showAnimeExtension() {
        switchToTabNumberChannel.trySend(2) // Anime extensions: tab no. 2
    }

    @Composable
    override fun Content() {
        val context = LocalContext.current

        // Hoisted for extensions tab's search bar
        val mangaExtensionsScreenModel = rememberScreenModel { MangaExtensionsScreenModel() }
        val mangaExtensionsState by mangaExtensionsScreenModel.state.collectAsState()

        val animeExtensionsScreenModel = rememberScreenModel { AnimeExtensionsScreenModel() }
        val animeExtensionsState by animeExtensionsScreenModel.state.collectAsState()

        val tabs = persistentListOf(
            animeSourcesTab(),
            mangaSourcesTab(),
            animeExtensionsTab(animeExtensionsScreenModel),
            mangaExtensionsTab(mangaExtensionsScreenModel),
            migrateAnimeSourceTab(),
            migrateMangaSourceTab(),
        )

        val state = rememberPagerState { tabs.size }

        TabbedScreen(
            titleRes = MR.strings.browse,
            tabs = tabs,
            state = state,
            mangaSearchQuery = mangaExtensionsState.searchQuery,
            onChangeMangaSearchQuery = mangaExtensionsScreenModel::search,
            animeSearchQuery = animeExtensionsState.searchQuery,
            onChangeAnimeSearchQuery = animeExtensionsScreenModel::search,
            scrollable = true,
        )
        LaunchedEffect(Unit) {
            switchToTabNumberChannel.receiveAsFlow()
                .collectLatest { state.scrollToPage(it) }
        }

        LaunchedEffect(Unit) {
            (context as? MainActivity)?.ready = true
        }
    }
}
