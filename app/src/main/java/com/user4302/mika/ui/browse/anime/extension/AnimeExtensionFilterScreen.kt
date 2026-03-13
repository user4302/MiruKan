package com.user4302.mika.ui.browse.anime.extension

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
import com.user4302.presentation.browse.anime.AnimeExtensionFilterScreen
import com.user4302.presentation.util.Screen
import kotlinx.coroutines.flow.collectLatest

class AnimeExtensionFilterScreen : Screen() {

    @Composable
    override fun Content() {
        val context = LocalContext.current
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = rememberScreenModel { AnimeExtensionFilterScreenModel() }
        val state by screenModel.state.collectAsState()

        if (state is AnimeExtensionFilterState.Loading) {
            LoadingScreen()
            return
        }

        val successState = state as AnimeExtensionFilterState.Success

        AnimeExtensionFilterScreen(
            navigateUp = navigator::pop,
            state = successState,
            onClickToggle = screenModel::toggle,
        )

        LaunchedEffect(Unit) {
            screenModel.events.collectLatest {
                when (it) {
                    AnimeExtensionFilterEvent.FailedFetchingLanguages -> {
                        context.toast(MR.strings.internal_error)
                    }
                }
            }
        }
    }
}
