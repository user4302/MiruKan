package com.user4302.mika.ui.storage

import androidx.compose.animation.graphics.res.animatedVectorResource
import androidx.compose.animation.graphics.res.rememberAnimatedVectorPainter
import androidx.compose.animation.graphics.vector.AnimatedImageVector
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import cafe.adriel.voyager.navigator.tab.LocalTabNavigator
import cafe.adriel.voyager.navigator.tab.TabOptions
import com.user4302.mika.R
import com.user4302.mika.i18n.MR
import com.user4302.mika.presentation.core.i18n.stringResource
import com.user4302.mika.ui.main.MainActivity
import com.user4302.mika.ui.storage.anime.animeStorageTab
import com.user4302.mika.ui.storage.manga.mangaStorageTab
import com.user4302.presentation.components.TabbedScreen
import com.user4302.presentation.util.Tab
import kotlinx.collections.immutable.persistentListOf

data object StorageTab : Tab {

    override val options: TabOptions
        @Composable
        get() {
            val isSelected = LocalTabNavigator.current.current.key == key
            val image = AnimatedImageVector.animatedVectorResource(R.drawable.anim_updates_enter)
            return TabOptions(
                index = 8u,
                title = stringResource(MR.strings.label_data_storage),
                icon = rememberAnimatedVectorPainter(image, isSelected),
            )
        }

    @Composable
    override fun Content() {
        val context = LocalContext.current

        val tabs = persistentListOf(
            animeStorageTab(),
            mangaStorageTab(),
        )
        val state = rememberPagerState { tabs.size }

        TabbedScreen(
            titleRes = MR.strings.label_data_storage,
            tabs = tabs,
            state = state,
        )

        LaunchedEffect(Unit) {
            (context as? MainActivity)?.ready = true
        }
    }
}
