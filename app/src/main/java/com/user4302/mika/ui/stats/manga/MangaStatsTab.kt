package com.user4302.mika.ui.stats.manga

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.user4302.mika.i18n.MR
import com.user4302.mika.presentation.core.screens.LoadingScreen
import com.user4302.presentation.components.TabContent
import com.user4302.presentation.more.stats.MangaStatsScreenContent
import com.user4302.presentation.more.stats.StatsScreenState

@Composable
fun Screen.mangaStatsTab(): TabContent {
    val navigator = LocalNavigator.currentOrThrow

    val screenModel = rememberScreenModel { MangaStatsScreenModel() }
    val state by screenModel.state.collectAsState()

    if (state is StatsScreenState.Loading) {
        LoadingScreen()
    }

    return TabContent(
        titleRes = MR.strings.manga,
        content = { contentPadding, _ ->

            if (state is StatsScreenState.Loading) {
                LoadingScreen()
            } else {
                MangaStatsScreenContent(
                    state = state as StatsScreenState.SuccessManga,
                    paddingValues = contentPadding,
                )
            }
        },
        navigateUp = navigator::pop,
    )
}
