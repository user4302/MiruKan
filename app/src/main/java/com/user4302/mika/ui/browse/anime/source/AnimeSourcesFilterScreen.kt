package com.user4302.mika.ui.browse.anime.source

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.user4302.mika.i18n.MR
import com.user4302.mika.presentation.core.screens.LoadingScreen
import com.user4302.mika.util.system.toast
import com.user4302.presentation.browse.anime.AnimeSourcesFilterScreen
import com.user4302.presentation.util.Screen

class AnimeSourcesFilterScreen : Screen() {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = rememberScreenModel { AnimeSourcesFilterScreenModel() }
        val state by screenModel.state.collectAsState()

        if (state is AnimeSourcesFilterScreenModel.State.Loading) {
            LoadingScreen()
            return
        }

        if (state is AnimeSourcesFilterScreenModel.State.Error) {
            val context = LocalContext.current
            LaunchedEffect(Unit) {
                context.toast(MR.strings.internal_error)
                navigator.pop()
            }
            return
        }

        val successState = state as AnimeSourcesFilterScreenModel.State.Success

        AnimeSourcesFilterScreen(
            navigateUp = navigator::pop,
            state = successState,
            onClickLanguage = screenModel::toggleLanguage,
            onClickSource = screenModel::toggleSource,
        )
    }
}
