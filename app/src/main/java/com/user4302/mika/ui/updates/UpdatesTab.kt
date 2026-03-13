package com.user4302.mika.ui.updates

import androidx.compose.animation.graphics.res.animatedVectorResource
import androidx.compose.animation.graphics.res.rememberAnimatedVectorPainter
import androidx.compose.animation.graphics.vector.AnimatedImageVector
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.tab.LocalTabNavigator
import cafe.adriel.voyager.navigator.tab.TabOptions
import com.user4302.domain.ui.model.NavStyle
import com.user4302.mika.R
import com.user4302.mika.i18n.MR
import com.user4302.mika.presentation.core.i18n.stringResource
import com.user4302.mika.ui.download.DownloadsTab
import com.user4302.mika.ui.main.MainActivity
import com.user4302.mika.ui.updates.anime.animeUpdatesTab
import com.user4302.mika.ui.updates.manga.mangaUpdatesTab
import com.user4302.presentation.components.TabbedScreen
import com.user4302.presentation.util.Tab
import kotlinx.collections.immutable.persistentListOf

data object UpdatesTab : Tab {

    override val options: TabOptions
        @Composable
        get() {
            val isSelected = LocalTabNavigator.current.current.key == key
            val image = AnimatedImageVector.animatedVectorResource(R.drawable.anim_updates_enter)
            val index: UShort = when (currentNavigationStyle()) {
                NavStyle.MOVE_UPDATES_TO_MORE -> 5u
                NavStyle.MOVE_HISTORY_TO_MORE -> 2u
                NavStyle.MOVE_BROWSE_TO_MORE -> 2u
                NavStyle.MOVE_MANGA_TO_MORE -> 1u
            }
            return TabOptions(
                index = index,
                title = stringResource(MR.strings.label_recent_updates),
                icon = rememberAnimatedVectorPainter(image, isSelected),
            )
        }
    override suspend fun onReselect(navigator: Navigator) {
        navigator.push(DownloadsTab)
    }

    @Composable
    override fun Content() {
        val context = LocalContext.current
        val fromMore = currentNavigationStyle() == NavStyle.MOVE_UPDATES_TO_MORE

        TabbedScreen(
            titleRes = MR.strings.label_recent_updates,
            tabs = persistentListOf(
                animeUpdatesTab(context, fromMore),
                mangaUpdatesTab(context, fromMore),
            ),
        )

        LaunchedEffect(Unit) {
            (context as? MainActivity)?.ready = true
        }
    }
}

private const val TAB_ANIME = 0
private const val TAB_MANGA = 1
